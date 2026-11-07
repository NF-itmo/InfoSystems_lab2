package org.services.importing;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.exceptions.BadRequestException;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.error.YAMLException;

import java.io.InputStream;
import java.util.Set;

@ApplicationScoped
public class YamlDocumentParser {
    @Inject
    private Validator validator;

    public <T> T parse(InputStream input, Class<T> documentType) {
        T document;

        try {
            document = newYamlParser(documentType).load(input);
        } catch (YAMLException exception) {
            throw new BadRequestException("Invalid YAML file");
        }

        if (document == null) {
            throw new BadRequestException("The YAML file must not be empty");
        }

        validate(document);
        return document;
    }

    private Yaml newYamlParser(Class<?> documentType) {
        LoaderOptions options = new LoaderOptions();
        options.setAllowDuplicateKeys(false);

        return new Yaml(new Constructor(documentType, options));
    }

    private void validate(Object document) {
        Set<ConstraintViolation<Object>> violations = validator.validate(document);

        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }
}
