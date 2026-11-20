package org.services.importing.internal;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.services.DTO.CoordinatesDto;

@Getter
@Setter
@NoArgsConstructor
public class CoordinatesImportRequest {
    @NotNull
    @DecimalMin(value = "-126", inclusive = false)
    private Double x;

    @NotNull
    private Integer y;

    public CoordinatesDto toServiceDto() {
        return new CoordinatesDto(null, x, y);
    }
}
