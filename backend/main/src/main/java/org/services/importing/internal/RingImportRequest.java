package org.services.importing.internal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.services.DTO.RingDto;

@Getter
@Setter
@NoArgsConstructor
public class RingImportRequest {
    @NotBlank
    private String name;

    @NotNull
    @Positive
    private Integer power;

    @NotNull
    @Positive
    private Long weight;

    public RingDto toServiceDto() {
        return new RingDto(name, power, weight);
    }
}
