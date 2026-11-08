package org.controllers.DTO.importing;

import org.models.ImportOperation;

public record GetImportOperationResponse(
        int id,
        String status,
        String username,
        Integer importedCount
) {
    public static GetImportOperationResponse fromModel(ImportOperation operation) {
        return new GetImportOperationResponse(
                operation.getId(),
                operation.getStatus().name(),
                operation.getUser().getUsername(),
                operation.getImportedCount()
        );
    }
}
