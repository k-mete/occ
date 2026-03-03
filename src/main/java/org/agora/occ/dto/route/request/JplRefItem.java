package org.agora.occ.dto.route.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
public class JplRefItem {

    @JsonProperty("jplId")
    @NotNull(message = "JPL ID is required")
    private UUID jplId;
}
