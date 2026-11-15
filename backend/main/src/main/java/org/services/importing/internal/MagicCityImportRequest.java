package org.services.importing.internal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.controllers.DTO.BookCreatureType;
import org.exceptions.BadRequestException;
import org.services.DTO.MagicCityDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

@Getter
@Setter
@NoArgsConstructor
public class MagicCityImportRequest {
    @NotBlank
    private String name;

    @NotNull
    @Positive
    private Long area;

    @NotNull
    @Positive
    private Integer population;

    private String establishmentDate;

    @NotNull
    private BookCreatureType governor;

    @NotNull
    private Boolean capital;

    @NotNull
    @Positive
    private Integer populationDensity;

    public MagicCityDto toServiceDto() {
        return new MagicCityDto(
                name,
                area,
                population,
                parseEstablishmentDate(),
                org.services.DTO.BookCreatureType.valueOf(governor.name()),
                capital,
                populationDensity
        );
    }

    private LocalDateTime parseEstablishmentDate() {
        if (establishmentDate == null) {
            return null;
        }

        try {
            return LocalDateTime.parse(establishmentDate);
        } catch (DateTimeParseException exception) {
            throw new BadRequestException(
                    "establishmentDate must be ISO local date-time"
            );
        }
    }
}
