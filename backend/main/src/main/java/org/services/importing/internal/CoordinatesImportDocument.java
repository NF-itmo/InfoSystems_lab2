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
public class CoordinatesImportDocument {
    @Valid
    @NotEmpty(message = "The coordinates list must not be empty")
    private List<CoordinatesImportRequest> coordinates = new ArrayList<>();
}
