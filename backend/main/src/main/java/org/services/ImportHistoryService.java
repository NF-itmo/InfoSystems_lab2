package org.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.models.ImportOperation;
import org.models.ImportOperationStatus;
import org.models.User;
import org.repository.ImportOperationRepository;
import org.repository.UserRepository;

import java.util.List;

@ApplicationScoped
public class ImportHistoryService {
    @Inject private ImportOperationRepository importOperationRepository;
    @Inject private UserRepository userRepository;

    @Transactional
    public ImportOperation start(int userId) {
        User user = userRepository.findById(userId)
                .orElseThrow();

        return importOperationRepository.insert(
                ImportOperation.builder()
                        .user(user)
                        .status(ImportOperationStatus.IN_PROGRESS)
                        .build()
        );
    }

    @Transactional
    public void markSuccessful(int operationId, int importedCount) {
        ImportOperation operation = importOperationRepository.findById(operationId)
                .orElseThrow();

        operation.setStatus(ImportOperationStatus.SUCCESS);
        operation.setImportedCount(importedCount);
        importOperationRepository.update(operation);
    }

    @Transactional
    public void markFailed(int operationId) {
        ImportOperation operation = importOperationRepository.findById(operationId)
                .orElseThrow();

        operation.setStatus(ImportOperationStatus.FAILED);
        importOperationRepository.update(operation);
    }

    @Transactional
    public List<ImportOperation> findAll(int userId, boolean isAdmin) {
        if (isAdmin) {
            return importOperationRepository.findAllDetailed();
        }

        return importOperationRepository.findAllDetailedByUserId(userId);
    }
}
