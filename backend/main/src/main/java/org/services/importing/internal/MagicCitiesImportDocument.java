package org.services.importing.internal;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class MagicCitiesImportDocument {
    @Valid
    @NotEmpty(message = "The magicCities list must not be empty")
    private List<MagicCityImportRequest> magicCities = new ArrayList<>();
}
