package com.ailleron.translation.controller;

import com.ailleron.translation.api.dto.DictionariesDTO;
import com.ailleron.translation.api.dto.DictionaryVersionsDTO;
import com.ailleron.translation.service.DictionaryService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("dictionaries")
public class DictionaryController {
  private final DictionaryService service;

  @Autowired
  public DictionaryController(DictionaryService service) {
    this.service = service;
  }

  @GetMapping
  @ApiOperation(value = "Get dictionaries",
    produces = "application/json", response = DictionariesDTO.class)
  @ApiResponses(value = {
    @ApiResponse(code = 200, message = "Dictionary instances")
  })
  public ResponseEntity<DictionariesDTO> getDictionaries(@RequestParam(value = "product", required = false) List<String> product,
                                                         @RequestParam(value = "language", required = false) List<String> language) {

    return ResponseEntity.ok(service.getDictionaries(language, product));
  }

  @GetMapping("/versions")
  @ApiOperation(value = "Get dictionary versions",
    produces = "application/json", response = DictionaryVersionsDTO.class)
  @ApiResponses(value = {
    @ApiResponse(code = 200, message = "Dictionary instances")
  })
  public ResponseEntity<DictionaryVersionsDTO> getDictionaryVersions(
    @RequestParam(value = "product", required = false) List<String> product,
    @RequestParam(value = "language", required = false) List<String> language) {

    return ResponseEntity.ok(service.getVersions(language, product));
  }

  @GetMapping("{product}/label")
  @ApiOperation(value = "Get dictionary labels",
    produces = "application/json", response = DictionaryVersionsDTO.class)
  @ApiResponses(value = {
    @ApiResponse(code = 200, message = "Dictionary instances")
  })
  public ResponseEntity<DictionaryVersionsDTO> getDictionaryLabels(@PathVariable(value = "product") String product,
                                                             @RequestParam(value = "label", required = false) List<String> label,
                                                             @RequestParam(value = "language", required = false) List<String> language) {

    return ResponseEntity.ok(service.getLabels(product, language, label));
  }

}
