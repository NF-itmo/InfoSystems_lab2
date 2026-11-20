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
public class BookCreatureImportDocument {
    @Valid
    @NotEmpty(message = "The bookCreatures list must not be empty")
    private List<BookCreatureImportRequest> bookCreatures = new ArrayList<>();
}
