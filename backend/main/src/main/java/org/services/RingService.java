package org.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.events.DataChangePublisher;
import org.exceptions.ConflictException;
import org.models.Ring;
import org.repository.RingRepository;
import org.repository.RingTransactionRepository;
import org.services.DTO.RingDto;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class RingService {
    @Inject private RingRepository ringRepository;
    @Inject private RingTransactionRepository ringTransactionRepository;
    @Inject private DataChangePublisher dataChangePublisher;

    @Transactional
    public RingDto create(RingDto dto) {
        ringTransactionRepository.useSerializableIsolation();
        ensureNameAvailable(dto.getName());
        RingDto createdRing = RingDto.fromModel(
                ringRepository.insert(dto.toModel())
        );
        dataChangePublisher.publish();
        return createdRing;
    }

    public List<RingDto> findAll() {
        return ringRepository.findAll()
                .map(RingDto::fromModel)
                .toList();
    }

    public Optional<RingDto> findByName(String name) {
        return ringRepository.findById(name)
                .map(RingDto::fromModel);
    }

    @Transactional
    public Optional<Ring> findByNameIgnoringCase(String name) {
        return ringRepository.findByNameIgnoringCase(name);
    }

    @Transactional
    public void ensureNameAvailable(String name) {
        if (findByNameIgnoringCase(name).isPresent()) {
            throw new ConflictException("A ring with this name already exists");
        }
    }

    @Transactional
    public Optional<RingDto> update(String name, RingDto dto) {
        return ringRepository.findById(name)
                .map(existing -> {
                    dto.setName(name);
                    RingDto updatedRing = RingDto.fromModel(
                            ringRepository.update(dto.toModel())
                    );
                    dataChangePublisher.publish();
                    return updatedRing;
                });
    }

    @Transactional
    public boolean delete(String name) {
        if (ringRepository.findById(name).isEmpty()) {
            return false;
        }

        ringRepository.deleteById(name);
        dataChangePublisher.publish();
        return true;
    }
}
