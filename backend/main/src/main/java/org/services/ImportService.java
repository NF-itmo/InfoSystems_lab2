package org.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.events.DataChangePublisher;
import org.exceptions.BadRequestException;
import org.models.BookCreature;
import org.models.Coordinate;
import org.models.MagicCity;
import org.models.Ring;
import org.repository.BookCreatureRepository;
import org.repository.CoordinateRepository;
import org.repository.MagicCityRepository;
import org.repository.RingRepository;
import org.services.DTO.BookCreatureDto;
import org.services.DTO.MagicCityDto;
import org.services.DTO.RingDto;
import org.services.importing.YamlDocumentParser;
import org.services.importing.internal.*;

import java.io.InputStream;
import java.util.List;
import java.util.Objects;

@ApplicationScoped
public class ImportService {
    @Inject private YamlDocumentParser yamlDocumentParser;
    @Inject private BookCreatureRepository bookCreatureRepository;
    @Inject private CoordinateRepository coordinateRepository;
    @Inject private MagicCityRepository magicCityRepository;
    @Inject private RingRepository ringRepository;
    @Inject private DataChangePublisher dataChangePublisher;

    @Transactional
    public int importBookCreatures(InputStream input) {
        BookCreatureImportDocument document = yamlDocumentParser.parse(
                input,
                BookCreatureImportDocument.class
        );
        List<BookCreatureDto> bookCreatures = document.getBookCreatures().stream()
                .map(BookCreatureImportRequest::toServiceDto)
                .toList();

        for (BookCreatureDto dto : bookCreatures) {
            Coordinate coordinates = coordinateRepository.insert(dto.getCoordinates().toModel());
            MagicCity creatureLocation = resolveMagicCity(dto.getCreatureLocation());
            Ring ring = resolveRing(dto.getRing());

            BookCreature bookCreature = dto.toModel();
            bookCreature.setCoordinates(coordinates);
            bookCreature.setCreatureLocationName(creatureLocation);
            bookCreature.setRingName(ring);
            bookCreatureRepository.insert(bookCreature);
        }

        dataChangePublisher.publish();
        return bookCreatures.size();
    }

    @Transactional
    public int importCoordinates(InputStream input) {
        CoordinatesImportDocument document = yamlDocumentParser.parse(
                input,
                CoordinatesImportDocument.class
        );

        document.getCoordinates().forEach(value ->
                coordinateRepository.insert(value.toServiceDto().toModel())
        );

        dataChangePublisher.publish();
        return document.getCoordinates().size();
    }

    @Transactional
    public int importMagicCities(InputStream input) {
        MagicCitiesImportDocument document = yamlDocumentParser.parse(
                input,
                MagicCitiesImportDocument.class
        );

        document.getMagicCities().forEach(value -> {
            MagicCity city = value.toServiceDto().toModel();

            if (magicCityRepository.findById(city.getName()).isPresent()) {
                throw new BadRequestException("A magic city with this name already exists");
            }

            magicCityRepository.insert(city);
        });

        dataChangePublisher.publish();
        return document.getMagicCities().size();
    }

    @Transactional
    public int importRings(InputStream input) {
        RingsImportDocument document = yamlDocumentParser.parse(input, RingsImportDocument.class);

        document.getRings().forEach(value -> {
            Ring ring = value.toServiceDto().toModel();

            if (ringRepository.findById(ring.getName()).isPresent()) {
                throw new BadRequestException("A ring with this name already exists");
            }

            ringRepository.insert(ring);
        });

        dataChangePublisher.publish();
        return document.getRings().size();
    }

    private MagicCity resolveMagicCity(MagicCityDto dto) {
        if (dto == null) {
            return null;
        }

        return magicCityRepository.findById(dto.getName())
                .map(existing -> {
                    if (!isSameMagicCity(existing, dto)) {
                        throw new BadRequestException(
                                "A magic city with this name already exists with different values"
                        );
                    }

                    return existing;
                })
                .orElseGet(() -> magicCityRepository.insert(dto.toModel()));
    }

    private Ring resolveRing(RingDto dto) {
        if (dto == null) {
            return null;
        }

        return ringRepository.findById(dto.getName())
                .map(existing -> {
                    if (!isSameRing(existing, dto)) {
                        throw new BadRequestException(
                                "A ring with this name already exists with different values"
                        );
                    }

                    return existing;
                })
                .orElseGet(() -> ringRepository.insert(dto.toModel()));
    }

    private boolean isSameMagicCity(MagicCity existing, MagicCityDto imported) {
        return Objects.equals(existing.getArea(), imported.getArea())
                && Objects.equals(existing.getPopulation(), imported.getPopulation())
                && Objects.equals(existing.getEstablishmentDate(), imported.getEstablishmentDate())
                && existing.getGovernor().name().equals(imported.getGovernor().name())
                && Objects.equals(existing.getCapital(), imported.getCapital())
                && Objects.equals(existing.getPopulationDensity(), imported.getPopulationDensity());
    }

    private boolean isSameRing(Ring existing, RingDto imported) {
        return Objects.equals(existing.getPower(), imported.getPower())
                && Objects.equals(existing.getWeight(), imported.getWeight());
    }
}
