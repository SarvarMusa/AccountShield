package com.codems.accountshield.common.config;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.media.StringSchema;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.codems.accountshield.common.constants.ApplicationConstants;


@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "AccountShield API",
                version = "1.0",
                description = "Secure user account management REST API. "
                        + "Register, login, manage your profile, and (as admin) manage all users.",
                contact = @Contact(name = "DevLab")
        ),
        security = @SecurityRequirement(name = "bearerAuth")
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER,
        description = "Provide a JWT access token obtained from /api/auth/login with API version 1.0"
)
public class OpenApiConfig {

    private static final String VERSION_PARAMETER = "v";
    private static final String BASE_MEDIA_TYPE = "application/vnd.devlab.dev+json";
    private static final String VERSIONED_MEDIA_TYPE = BASE_MEDIA_TYPE + ";" + VERSION_PARAMETER + "="
            + ApplicationConstants.API_VERSION;

    @Bean
    OpenApiCustomizer versionedContentNegotiationCustomizer() {
        return openApi -> openApi.getPaths().values().forEach(pathItem ->
                pathItem.readOperations().forEach(operation -> {
                    addAcceptVersionHeader(operation);
                    addVersionedRequestContent(operation);
                    addVersionedResponseContent(operation);
                })
        );
    }

    private void addAcceptVersionHeader(Operation operation) {
        if (operation.getParameters() == null) {
            operation.setParameters(new ArrayList<>());
        }

        boolean alreadyExists = operation.getParameters().stream()
                .anyMatch(parameter -> "Accept".equalsIgnoreCase(parameter.getName()));
        if (alreadyExists) {
            return;
        }

        operation.addParametersItem(new Parameter()
                .in("header")
                .name("Accept")
                .required(false)
                .description("API version for content negotiation")
                .schema(new StringSchema()._default(VERSIONED_MEDIA_TYPE).example(VERSIONED_MEDIA_TYPE)));
    }

    private void addVersionedRequestContent(Operation operation) {
        if (operation.getRequestBody() == null || operation.getRequestBody().getContent() == null) {
            return;
        }

        operation.getRequestBody().setContent(versionedContent(operation.getRequestBody().getContent()));
    }

    private void addVersionedResponseContent(Operation operation) {
        ApiResponses responses = operation.getResponses();
        if (responses == null) {
            return;
        }

        for (ApiResponse response : responses.values()) {
            if (response.getContent() != null) {
                response.setContent(versionedContent(response.getContent()));
            }
        }
    }

    private Content versionedContent(Content originalContent) {
        if (originalContent.containsKey(VERSIONED_MEDIA_TYPE)) {
            return originalContent;
        }

        Content content = new Content();
        MediaType mediaType = originalContent.get(org.springframework.http.MediaType.APPLICATION_JSON_VALUE);
        if (mediaType == null && !originalContent.isEmpty()) {
            mediaType = originalContent.values().iterator().next();
        }
        if (mediaType != null) {
            content.addMediaType(VERSIONED_MEDIA_TYPE, mediaType);
        }

        for (Map.Entry<String, MediaType> entry : new LinkedHashMap<>(originalContent).entrySet()) {
            content.addMediaType(entry.getKey(), entry.getValue());
        }
        return content;
    }
}
