package com.application.events.controllers;

import com.application.events.dto.BandsDto;
import com.application.events.dto.PagedResponseDto;
import com.application.events.exception.ErrorMessage;
import com.application.events.validation.ValidationGroups;
import com.application.events.services.impl.BandsServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/v1/api/bands")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Log
public class BandsController {

    private final BandsServiceImpl bandsService;

    @GetMapping
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Return pageable list of bands", content = { @Content(mediaType = "application/json",
                    schema = @Schema(implementation = BandsDto.class)) })
    })
    @Operation(summary = "List of bands")
    public ResponseEntity<PagedResponseDto<BandsDto>> getAll(@ParameterObject Pageable pageable) {
        PagedResponseDto<BandsDto> bands = bandsService.getAll(pageable);
        return new ResponseEntity<>(bands, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Return a specific band with a specific id", content = { @Content(mediaType = "application/json",
                    schema = @Schema(implementation = BandsDto.class)) }),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Token expired", content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "404", description = "Not Found - Band not found", content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorMessage.class))),
    })
    @Operation(summary = "Get band by id")
    public ResponseEntity<BandsDto> getById(@PathVariable("id") int id) {
        log.info("****** Start to found band with id: " + id + " ******");
        BandsDto band = bandsService.getById(id);
        return new ResponseEntity<>(band, HttpStatus.OK);
    }

    @PostMapping
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Band created successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BandsDto.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request - Some required fields is missing within the body", content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Token expired", content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "409", description = "Conflict - Another band with the same name alredy exist", content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorMessage.class))),
    })
    @Operation(summary = "Create new band")
    public ResponseEntity<BandsDto> create(@Validated(ValidationGroups.OnCreate.class)
                                               @RequestBody BandsDto bandsDto) {
        log.info("****** Start to add new band ******");
        BandsDto band = bandsService.createNewBand(bandsDto);
        return new ResponseEntity<>(band, HttpStatus.CREATED);
    }

    @PatchMapping("/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Band modified successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BandsDto.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request - Some required fields is missing within the body", content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Token expired", content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "404", description = "Not Found - Band to modify not found", content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorMessage.class))),
    })
    @Operation(summary = "Modify a band")
    public ResponseEntity<BandsDto> modify(@PathVariable("id") int id,
                                           @Validated(ValidationGroups.OnUpdate.class)
                                           @RequestBody BandsDto bandsDto) {
        BandsDto modifyBand = bandsService.modifyBand(id, bandsDto);
        return new ResponseEntity<>(modifyBand, HttpStatus.OK);

    }

    @DeleteMapping("/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Band deleted successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BandsDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Token expired", content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "404", description = "Not Found - Band not found", content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorMessage.class))),
    })
    @Operation(summary = "Delete a band")
    public ResponseEntity<Map<String, String>> deleteBand(@PathVariable("id") int id) {
        Map<String, String> response = new HashMap<>();

        bandsService.deleteBand(id);
        response.put("message", "Deleted band with id " + id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
