package org.services.importing.internal;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.controllers.DTO.BookCreatureType;
import org.services.DTO.BookCreatureDto;

@Getter
@Setter
@NoArgsConstructor
public class BookCreatureImportRequest {
    @NotBlank
    private String name;

    @Valid
    @NotNull
    private CoordinatesImportRequest coordinates;

    @NotNull
    @Positive
    private Integer age;

    @NotNull
    private BookCreatureType creatureType;

    @Valid
    private MagicCityImportRequest creatureLocation;

    @NotNull
    @Positive
    private Float attackLevel;

    @NotNull
    @Positive
    private Double defenseLevel;

    @Valid
    private RingImportRequest ring;

    public BookCreatureDto toServiceDto() {
        return BookCreatureDto.builder()
                .name(name)
                .coordinates(coordinates.toServiceDto())
                .age(age)
                .creatureType(org.services.DTO.BookCreatureType.valueOf(creatureType.name()))
                .creatureLocation(
                        creatureLocation == null
                                ? null
                                : creatureLocation.toServiceDto()
                )
                .attackLevel(attackLevel)
                .defenseLevel(defenseLevel)
                .ring(ring == null ? null : ring.toServiceDto())
                .build();
    }
}
