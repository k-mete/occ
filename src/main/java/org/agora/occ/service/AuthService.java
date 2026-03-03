package org.agora.occ.service;

import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.agora.occ.dto.auth.request.LoginRequest;
import org.agora.occ.dto.auth.request.RefreshTokenRequest;
import org.agora.occ.dto.auth.response.LoginResponse;
import org.agora.occ.dto.auth.response.ProfileResponse;
import org.agora.occ.dto.auth.response.TokenRefreshResponse;
import org.agora.occ.dto.auth.response.TokenResponse;
import org.agora.occ.entity.RefreshTokenEntity;
import org.agora.occ.entity.UserEntity;
import org.agora.occ.exception.ApplicationException;
import org.agora.occ.exception.BadCredentialsException;
import org.agora.occ.repository.RefreshTokenRepository;
import org.agora.occ.repository.UserRepository;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jboss.logging.Logger;
import org.mindrot.jbcrypt.BCrypt;

import jakarta.ws.rs.core.Response;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

/**
 * Service class for handling authentication logic: login, refresh, logout,
 * profile.
 */
@ApplicationScoped
public class AuthService {

        private static final Logger LOG = Logger.getLogger(AuthService.class);

        private static final String ISSUER = "https://occ.agora.org";
        private static final long ACCESS_TOKEN_EXPIRATION_HOURS = 8;
        private static final long REFRESH_TOKEN_EXPIRATION_DAYS = 30;

        private final UserRepository userRepository;
        private final RefreshTokenRepository refreshTokenRepository;
        private final JsonWebToken jwt;

        @Inject
        public AuthService(UserRepository userRepository,
                        RefreshTokenRepository refreshTokenRepository,
                        JsonWebToken jwt) {
                this.userRepository = userRepository;
                this.refreshTokenRepository = refreshTokenRepository;
                this.jwt = jwt;
        }

        /**
         * Authenticates a user and generates access/refresh tokens.
         */
        @Transactional
        public LoginResponse login(LoginRequest request) {
                LOG.debugv("Attempting login for NRP: {0}", request.getNrp());

                LOG.infov("Querying database for NRP: {0}", request.getNrp());
                UserEntity user = userRepository.findByNrp(request.getNrp())
                                .orElseThrow(() -> {
                                        LOG.errorv("User not found in DB for NRP: {0}", request.getNrp());
                                        return new BadCredentialsException();
                                });

                LOG.infov("User found: {0}. Checking password...", user.getFullName());

                boolean passwordMatches = BCrypt.checkpw(request.getPassword(), user.getPassword());

                if (!passwordMatches) {
                        LOG.warnv("Failed login attempt due to password mismatch for NRP: {0}", request.getNrp());
                        throw new BadCredentialsException();
                }

                // Generate tokens
                Instant now = Instant.now();
                Instant accessTokenExpiration = now.plus(ACCESS_TOKEN_EXPIRATION_HOURS, ChronoUnit.HOURS);

                String accessToken = generateAccessToken(user, now, accessTokenExpiration);
                RefreshTokenEntity refreshTokenEntity = createAndSaveRefreshToken(user, now);

                LOG.infov("Successful login for NRP: {0}", request.getNrp());

                return LoginResponse.builder()
                                .tokens(TokenResponse.builder()
                                                .accessToken(accessToken)
                                                .refreshToken(refreshTokenEntity.getTokenStr())
                                                .tokenType("Bearer")
                                                .expiresAt(accessTokenExpiration)
                                                .build())
                                .user(LoginResponse.UserData.builder()
                                                .userId(user.getUserId())
                                                .nrp(user.getNrp())
                                                .fullName(user.getFullName())
                                                .role(user.getRole())
                                                .build())
                                .checkin(LoginResponse.CheckinData.builder()
                                                .isCheckedIn(true) // Placeholder logic, could fetch from another
                                                                   // service
                                                .checkInTime(now)
                                                .build())
                                .build();
        }

        /**
         * Refreshes an access token using a valid refresh token.
         */
        @Transactional
        public TokenRefreshResponse refresh(RefreshTokenRequest request) {
                LOG.debug("Attempting to refresh token");

                RefreshTokenEntity refreshToken = refreshTokenRepository.findByTokenStr(request.getRefreshToken())
                                .orElseThrow(() -> new ApplicationException("Invalid refresh token",
                                                Response.Status.UNAUTHORIZED));

                if (refreshToken.isRevoked() || refreshToken.getExpiresAt().isBefore(Instant.now())) {
                        LOG.warnv("Attempted to use revoked/expired refresh token: {0}", refreshToken.getTokenId());
                        throw new ApplicationException("Refresh token is expired or revoked",
                                        Response.Status.UNAUTHORIZED);
                }

                UserEntity user = refreshToken.getUser();
                Instant now = Instant.now();
                Instant accessTokenExpiration = now.plus(ACCESS_TOKEN_EXPIRATION_HOURS, ChronoUnit.HOURS);

                String newAccessToken = generateAccessToken(user, now, accessTokenExpiration);

                LOG.infov("Successfully refreshed token for User ID: {0}", user.getUserId());

                return TokenRefreshResponse.builder()
                                .tokens(TokenResponse.builder()
                                                .accessToken(newAccessToken)
                                                .tokenType("Bearer")
                                                .expiresAt(accessTokenExpiration)
                                                .build())
                                .build();
        }

        /**
         * Logs out by revoking the refresh token.
         */
        @Transactional
        public void logout(RefreshTokenRequest request) {
                refreshTokenRepository.findByTokenStr(request.getRefreshToken()).ifPresent(token -> {
                        token.setRevoked(true);
                        refreshTokenRepository.persist(token);
                        LOG.infov("Revoked refresh token for User ID: {0}", token.getUser().getUserId());
                });
        }

        /**
         * Retrieves the current user's profile from the JWT token and database.
         */
        public ProfileResponse getProfile() {
                if (jwt == null || jwt.getName() == null) {
                        throw new ApplicationException("Unauthorized", Response.Status.UNAUTHORIZED);
                }

                String nrp = jwt.getName();
                UserEntity user = userRepository.findByNrp(nrp)
                                .orElseThrow(() -> new ApplicationException("User not found",
                                                Response.Status.NOT_FOUND));

                long expTimestamp = jwt.getExpirationTime();
                long iatTimestamp = jwt.getIssuedAtTime();

                return ProfileResponse.builder()
                                .user(ProfileResponse.UserData.builder()
                                                .userId(user.getUserId())
                                                .nrp(user.getNrp())
                                                .fullName(user.getFullName())
                                                .role(user.getRole())
                                                .build())
                                .session(ProfileResponse.SessionData.builder()
                                                .issuedAt(Instant.ofEpochSecond(iatTimestamp))
                                                .expiresAt(Instant.ofEpochSecond(expTimestamp))
                                                .active(true)
                                                .build())
                                .build();
        }

        private String generateAccessToken(UserEntity user, Instant issuedAt, Instant expiresAt) {
                return Jwt.issuer(ISSUER)
                                .upn(user.getNrp())
                                .groups(user.getRole().name())
                                .claim("fullName", user.getFullName())
                                .issuedAt(issuedAt)
                                .expiresAt(expiresAt)
                                .sign();
        }

        private RefreshTokenEntity createAndSaveRefreshToken(UserEntity user, Instant now) {
                RefreshTokenEntity refreshToken = new RefreshTokenEntity();
                refreshToken.setUser(user);
                refreshToken.setTokenStr(UUID.randomUUID().toString()); // Use secure random UUID as token
                refreshToken.setExpiresAt(now.plus(REFRESH_TOKEN_EXPIRATION_DAYS, ChronoUnit.DAYS));
                refreshToken.setRevoked(false);
                refreshTokenRepository.persist(refreshToken);
                return refreshToken;
        }
}
