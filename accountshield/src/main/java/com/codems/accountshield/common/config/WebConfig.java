package com.codems.accountshield.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.config.annotation.ApiVersionConfigurer;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.codems.accountshield.common.constants.ApplicationConstants;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private static final String DEFAULT_VERSION = ApplicationConstants.API_VERSION;
    private static final String[] SUPPORTED_VERSIONS = {"1.0", "2.0", "3.0"};

    @Override
    public void configureApiVersioning(ApiVersionConfigurer configurer) {
        configurer
                .useMediaTypeParameter(MediaType.parseMediaType("application/vnd.devlab.dev+json"), "v")
                .addSupportedVersions(SUPPORTED_VERSIONS)
                .setDefaultVersion(DEFAULT_VERSION);
    }

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        configurer.addPathPrefix(ApplicationConstants.API_PATH_PREFIX,
                controllerType -> controllerType.getPackageName().startsWith("com.codems.accountshield"));
    }
}
