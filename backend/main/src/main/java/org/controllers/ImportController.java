package org.controllers;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.EntityPart;
import jakarta.ws.rs.core.MediaType;
import org.controllers.DTO.importing.ImportObjectsResponse;
import org.exceptions.BadRequestException;
import org.jwtProcessing.filter.JwtSecured;
import org.services.ImportService;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Function;

@Path("/imports")
@Consumes(MediaType.MULTIPART_FORM_DATA)
@Produces(MediaType.APPLICATION_JSON)
@JwtSecured
public class ImportController {
    @Inject
    private ImportService importService;

    @POST
    @Path("/book-creatures")
    public ImportObjectsResponse importBookCreatures(@FormParam("file") EntityPart file) {
        return importFile(file, importService::importBookCreatures);
    }

    @POST
    @Path("/coordinates")
    public ImportObjectsResponse importCoordinates(@FormParam("file") EntityPart file) {
        return importFile(file, importService::importCoordinates);
    }

    @POST
    @Path("/magic-cities")
    public ImportObjectsResponse importMagicCities(@FormParam("file") EntityPart file) {
        return importFile(file, importService::importMagicCities);
    }

    @POST
    @Path("/rings")
    public ImportObjectsResponse importRings(@FormParam("file") EntityPart file) {
        return importFile(file, importService::importRings);
    }

    private ImportObjectsResponse importFile(
            EntityPart file,
            Function<InputStream, Integer> importer
    ) {
        if (file == null) {
            throw new BadRequestException("The YAML file is required");
        }

        try (InputStream input = file.getContent()) {
            return new ImportObjectsResponse(importer.apply(input));
        } catch (IOException exception) {
            throw new BadRequestException("Unable to read the YAML file");
        }
    }
}
