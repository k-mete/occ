package org.agora.occ.dto.train.response;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.agora.occ.enums.ActiveStatus;
import org.agora.occ.enums.TransportCategory;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RegisterForReflection
public class TrainResponse {

    private UUID id;
    private String trainName;
    private String trainCode;
    private String trainNetworkIp;
    private ActiveStatus trainStatus;
    private Boolean trainOnline;
    private TransportCategory category;
    private Double trainLastKnownLatitude;
    private Double trainLastKnownLongitude;
    private UUID routeId;
    private Instant createdAt;
    private Instant updatedAt;
}
