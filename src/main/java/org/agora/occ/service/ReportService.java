package org.agora.occ.service;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.agora.occ.dto.common.PagedResult;
import org.agora.occ.dto.report.request.ReportRequest;
import org.agora.occ.dto.report.response.ReportResponse;
import org.agora.occ.entity.ReportEntity;
import org.agora.occ.entity.event.ReportCreatedEvent;
import org.agora.occ.enums.ReportFileType;
import org.agora.occ.mapper.ReportMapper;
import org.agora.occ.minio.FileUtils;
import org.agora.occ.minio.MinioException;
import org.agora.occ.minio.MinioService;
import org.agora.occ.repository.ReportRepository;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.multipart.FileUpload;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service for creating, querying, and downloading reports stored in MinIO.
 */
@ApplicationScoped
public class ReportService {

    private static final Logger LOG = Logger.getLogger(ReportService.class);

    private final MinioService minioService;
    private final ReportRepository reportRepository;
    private final ReportMapper reportMapper;
    private final Event<ReportCreatedEvent> reportCreatedEvent;

    @ConfigProperty(name = "app.minio.bucket")
    String bucketName;

    @Inject
    public ReportService(MinioService minioService,
            ReportRepository reportRepository,
            ReportMapper reportMapper,
            Event<ReportCreatedEvent> reportCreatedEvent) {
        this.minioService = minioService;
        this.reportRepository = reportRepository;
        this.reportMapper = reportMapper;
        this.reportCreatedEvent = reportCreatedEvent;
    }

    /**
     * Creates a new report by uploading images to MinIO, persisting the entity,
     * and firing a {@link ReportCreatedEvent} for WebSocket broadcast.
     *
     * @param request the multipart form data
     * @return the created report response
     */
    @Transactional
    public ReportResponse createReport(ReportRequest request) {
        LOG.debugv("Starting report creation. Type: {0}, Title: {1}", request.type, request.title);
        List<String> objectKeys = uploadImages(request);
        ReportEntity entity = persistReport(request, objectKeys);
        LOG.debugv("Report created successfully with id: {0}", entity.getId());

        ReportResponse response = reportMapper.toResponse(entity);
        reportCreatedEvent.fire(new ReportCreatedEvent(
                entity.getId(),
                entity.getType(),
                entity.getTitle(),
                entity.getDescription(),
                response.getFilePaths(),
                entity.getLatitude(),
                entity.getLongitude(),
                entity.getTrainId(),
                entity.getJplId(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getIsRead()));

        return response;
    }

    /**
     * Retrieves all reports with optional filtering and pagination.
     *
     * @param page      0-indexed page number
     * @param size      page size
     * @param type      optional report type name filter
     * @param jplName   optional JPL name substring filter
     * @param trainName optional train name substring filter
     * @param date      optional date string filter (maps to createdAt)
     * @param jplId     optional exact JPL ID filter
     * @param trainId   optional exact train ID filter
     * @return paged list of report responses
     */
    public PagedResult<ReportResponse> getAllReports(int page, int size,
            String type, String jplName,
            String trainName, String date,
            UUID jplId, UUID trainId) {
        LOG.debugv("Fetching paginated reports. Page: {0}, Size: {1}", page, size);
        PanacheQuery<ReportEntity> query = reportRepository.findReports(type, jplName, trainName, date, jplId, trainId);
        List<ReportResponse> data = query.page(page, size).list().stream()
                .map(reportMapper::toResponse)
                .collect(Collectors.toList());
        return new PagedResult<>(data, page, size, query.count());
    }

    /**
     * Retrieves a single report by its ID.
     *
     * @param id the report UUID
     * @return the report response, or {@code null} if not found
     */
    public ReportResponse getReportById(UUID id) {
        ReportEntity report = reportRepository.findById(id);
        if (report == null)
            return null;
        return reportMapper.toResponse(report);
    }

    /**
     * Downloads a report file from MinIO using the provided object key or URL.
     *
     * @param objectKey the MinIO object key or full public URL
     * @return the file data as an {@link InputStream}
     */
    public InputStream getReportFile(String objectKey) {
        if (!objectKey.contains("/") && !objectKey.startsWith("http")) {
            ReportEntity report = reportRepository.findByFileName(objectKey);
            if (report != null && report.getFilePaths() != null) {
                for (String path : report.getFilePaths().split(",")) {
                    if (path.endsWith(objectKey)) {
                        objectKey = path.trim();
                        break;
                    }
                }
            }
        }
        objectKey = stripToObjectKey(objectKey);
        return minioService.getObject(objectKey);
    }

    /**
     * Marks the given list of report IDs as read and returns the updated responses.
     *
     * @param ids the list of report IDs to mark as read
     * @return the updated report responses
     */
    @Transactional
    public List<ReportResponse> markAsRead(List<UUID> ids) {
        List<ReportEntity> reports = reportRepository.list("id in ?1", ids);
        reports.forEach(report -> report.setIsRead(true));
        return reports.stream().map(reportMapper::toResponse).collect(Collectors.toList());
    }

    /**
     * Uploads all images from the request to MinIO and returns their object keys.
     *
     * @param request the multipart form containing the image uploads
     * @return list of MinIO object keys (paths)
     */
    private List<String> uploadImages(ReportRequest request) {
        List<String> objectKeys = new ArrayList<>();
        if (request.images == null || request.images.isEmpty())
            return objectKeys;

        String folderPath = buildFolderPath(request);
        String uploadPrefix = UUID.randomUUID() + "-";
        for (int i = 0; i < request.images.size(); i++) {
            FileUpload file = request.images.get(i);
            String objectName = folderPath + FileUtils.generateImageObjectName(uploadPrefix + i + "-", file);
            LOG.debugv("Uploading file: {0}", objectName);
            try {
                FileUtils.uploadFile(minioService, objectName, file);
                objectKeys.add(objectName);
            } catch (Exception e) {
                LOG.error("Failed to upload file to MinIO", e);
                throw new MinioException("Failed to upload file: " + e.getMessage(), e);
            }
        }
        return objectKeys;
    }

    /**
     * Determines the MinIO folder path based on report type and associated entity
     * ID.
     *
     * @param request the multipart form
     * @return the folder path string
     */
    private String buildFolderPath(ReportRequest request) {
        if (ReportFileType.LOCO.equals(request.type) && request.trainId != null) {
            return "loco/" + request.trainId + "/";
        }
        if (ReportFileType.JPL.equals(request.type) && request.jplId != null) {
            return "jpl/" + request.jplId + "/";
        }
        return "";
    }

    /**
     * Persists the report entity using the mapper.
     *
     * @param request    the multipart form request
     * @param objectKeys the uploaded MinIO object keys
     * @return the persisted entity
     */
    private ReportEntity persistReport(ReportRequest request, List<String> objectKeys) {
        ReportEntity entity = reportMapper.toEntity(request, objectKeys);
        reportRepository.persist(entity);
        return entity;
    }

    /**
     * Extracts the MinIO object key from a full public URL.
     * Handles both path-style and virtual-host-style URLs.
     *
     * @param url the full URL or plain object key
     * @return the relative MinIO object key
     */
    private String stripToObjectKey(String url) {
        if (!url.startsWith("http"))
            return url;
        try {
            java.net.URL parsed = new java.net.URL(url);
            String path = parsed.getPath();
            if (path.startsWith("/"))
                path = path.substring(1);
            if (path.startsWith(bucketName + "/")) {
                return path.substring(bucketName.length() + 1);
            }
            int firstSlash = path.indexOf('/');
            if (firstSlash != -1 && path.substring(0, firstSlash).equals(bucketName)) {
                return path.substring(firstSlash + 1);
            }
            return path;
        } catch (Exception e) {
            LOG.errorv("Failed to parse object key from URL: {0}", url);
            return url;
        }
    }
}
