package org.controllers;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.EntityPart;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.core.Context;
import org.controllers.DTO.importing.GetImportOperationResponse;
import org.controllers.DTO.importing.ImportObjectsResponse;
import org.exceptions.BadRequestException;
import org.jwtProcessing.filter.JwtSecured;
import org.services.ImportService;
import org.services.ImportHistoryService;
import org.jwtProcessing.security.JwtPrincipal;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Function;
import java.util.List;

@Path("/imports")
@Consumes(MediaType.MULTIPART_FORM_DATA)
@Produces(MediaType.APPLICATION_JSON)
@JwtSecured
public class ImportController {
    @Inject
    private ImportService importService;
    @Inject
    private ImportHistoryService importHistoryService;

    @POST
    @Path("/book-creatures")
    public ImportObjectsResponse importBookCreatures(
            @FormParam("file") EntityPart file,
            @Context SecurityContext securityContext
    ) {
        return importFile(file, securityContext, importService::importBookCreatures);
    }

    @POST
    @Path("/coordinates")
    public ImportObjectsResponse importCoordinates(
            @FormParam("file") EntityPart file,
            @Context SecurityContext securityContext
    ) {
        return importFile(file, securityContext, importService::importCoordinates);
    }

    @POST
    @Path("/magic-cities")
    public ImportObjectsResponse importMagicCities(
            @FormParam("file") EntityPart file,
            @Context SecurityContext securityContext
    ) {
        return importFile(file, securityContext, importService::importMagicCities);
    }

    @POST
    @Path("/rings")
    public ImportObjectsResponse importRings(
            @FormParam("file") EntityPart file,
            @Context SecurityContext securityContext
    ) {
        return importFile(file, securityContext, importService::importRings);
    }

    @GET
    @Path("/history")
    @Consumes(MediaType.APPLICATION_JSON)
    public List<GetImportOperationResponse> findHistory(
            @Context SecurityContext securityContext
    ) {
        return importHistoryService.findAll(
                        currentUserId(securityContext),
                        securityContext.isUserInRole("ADMIN")
                ).stream()
                .map(GetImportOperationResponse::fromModel)
                .toList();
    }

    private ImportObjectsResponse importFile(
            EntityPart file,
            SecurityContext securityContext,
            Function<InputStream, Integer> importer
    ) {
        if (file == null) {
            throw new BadRequestException("The YAML file is required");
        }

        int operationId = importHistoryService.start(currentUserId(securityContext)).getId();

        try (InputStream input = file.getContent()) {
            int importedCount = importer.apply(input);
            importHistoryService.markSuccessful(operationId, importedCount);
            return new ImportObjectsResponse(operationId, importedCount);
        } catch (IOException exception) {
            importHistoryService.markFailed(operationId);
            throw new BadRequestException("Unable to read the YAML file");
        } catch (RuntimeException exception) {
            importHistoryService.markFailed(operationId);
            throw exception;
        }
    }

    private int currentUserId(SecurityContext securityContext) {
        return ((JwtPrincipal) securityContext.getUserPrincipal()).getUid();
    }
}
