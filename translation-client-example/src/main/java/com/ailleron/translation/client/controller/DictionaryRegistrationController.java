package com.ailleron.translation.client.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("register")
@RequiredArgsConstructor
public class DictionaryRegistrationController {
  @Value("${translation.service.url}")
  private String translationServiceURL;

  private final ResourceLoader resourceLoader;

  private static final String TRANSLATIONS_DIR = "classpath:translations/**";

  @PostMapping
  @ApiOperation(value = "Register all dictionary in translation-service",
    produces = "application/json", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @ApiResponses(value = {
    @ApiResponse(code = 200, message = "Successful dictionary registered")
  })
  public void registerDictionary() {
    log.info("Starting dictionary registration");
    try {
      List<ResourceFile> resourceFiles = Arrays.stream(getResourceFiles(TRANSLATIONS_DIR))
        .map(ResourceFile::new)
        .collect(Collectors.toList());
      resourceFiles.forEach(this::sendFile);
    } catch (Exception e) {
      log.error("Error while trying to register dictionaries", e);
    }
    log.info("Stopping dictionary registration!");

  }

  private Resource[] getResourceFiles(String path) throws IOException {
    ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(resourceLoader);
    return resolver.getResources(path);
  }

  private void sendFile(ResourceFile resourceFiles) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.MULTIPART_FORM_DATA);
    Map<String, Object> urlParams = new HashMap<>();
    urlParams.put("product", "a");
    resourceFiles.getLanguage().ifPresent(value -> urlParams.put("language", value));
    MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
    body.add("file", resourceFiles.getResource());
    String url = UriComponentsBuilder.fromUriString(translationServiceURL).uriVariables(urlParams).toUriString();
    HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
    RestTemplate restTemplate = new RestTemplate();
    ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);
    log.info("Response code: " + response.getStatusCode());

  }

}
