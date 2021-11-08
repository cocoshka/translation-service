package com.ailleron.translation.controller;

import com.ailleron.translation.service.TranslationService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("register")
public class DictionaryRegistrationController {
  private TranslationService service;

  @Autowired
  public DictionaryRegistrationController(TranslationService service) {
    this.service = service;
  }

  @PostMapping("/dictionaries/{product}/{language}")
  @ApiOperation(value = "Register dictionary",
    produces = "application/json", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @ApiResponses(value = {
    @ApiResponse(code = 200, message = "Successful dictionary registered")
  })
  public void registerDictionary(@RequestPart("file") MultipartFile file,
                                 @PathVariable("product") String product,
                                 @PathVariable("language") String language) {

    log.info("Register dictionary {} in language: {}, for product: {}", file.getOriginalFilename(), language, product);
  }
}
