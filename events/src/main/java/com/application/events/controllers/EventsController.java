package com.application.events.controllers;

import com.application.events.dto.BandsDto;
import com.application.events.dto.EventsDto;
import com.application.events.dto.PagedResponseDto;
import com.application.events.enumeration.Role;
import com.application.events.exception.ErrorMessage;
import com.application.events.services.impl.EventsServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/api/events")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Log
public class EventsController {

    private final EventsServiceImpl eventsService;

    @GetMapping
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Return pageable list of events", content = { @Content(mediaType = "application/json",
                    schema = @Schema(implementation = EventsDto.class)) }),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Token expired", content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorMessage.class))),
    })
    @Operation(summary = "List of events")
    public ResponseEntity<?> getAllEvents(
            @RequestParam(required = false) List<Integer> ids,
            @ParameterObject Pageable pageable) {

        if (ids != null && !ids.isEmpty()) {
            List<EventsDto> events = eventsService.findByIds(ids);
            return ResponseEntity.ok(events);
        }

        PagedResponseDto<EventsDto> events = eventsService.getAll(pageable);
        return ResponseEntity.ok(events);
    }

    @GetMapping("/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Return pageable list of events", content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = EventsDto.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Token expired", content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "404", description = "Not Found - Band to modify not found", content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorMessage.class))),
    })
    @Operation(summary = "Search event by id")
    public ResponseEntity<EventsDto> getEventsById(
            @PathVariable("id") int id) {
        log.info("****** search event with id: " + id + " ******");
        EventsDto events = eventsService.getById(id);
        return new ResponseEntity<>(events, HttpStatus.OK);
    }

    @PostMapping
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Event created successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = EventsDto.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request - Some required fields is missing within the body", content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Token expired", content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "404", description = "Not Found - Band not found", content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "409", description = "Conflict - Another event with the same name alredy exist", content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorMessage.class))),
    })
    @Operation(summary = "Create new event")
    public ResponseEntity<EventsDto> createEvent(@Valid @RequestBody EventsDto eventsDto) {
        log.info("start to create new event: " + eventsDto.getTitle());
        EventsDto event = eventsService.createEvent(eventsDto);
        return new ResponseEntity<>(event, HttpStatus.CREATED);
    }

    @PatchMapping("/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Event modified successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BandsDto.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request - Some required fields is missing within the body", content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Token expired", content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "404", description = "Not Found - Event/Band to modify not found", content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorMessage.class))),
    })
    @Operation(summary = "Modify an event")
    public ResponseEntity<EventsDto> modifyEvent(
            @PathVariable("id") int id,
            @Valid @RequestBody EventsDto eventsDto,
            Authentication auth
    ) {
        log.info("start to modify new event: " + eventsDto.getTitle());
        Role role = Role.roleFromString(auth.getAuthorities().iterator().next().getAuthority().substring(5));
        EventsDto event = eventsService.modifyEvent(eventsDto, id, role);
        return new ResponseEntity<>(event, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Event deleted successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BandsDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Token expired", content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "404", description = "Not Found - Event to delete not found", content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorMessage.class))),
    })
    @Operation(summary = "Delete an event")
    public ResponseEntity<Map<String, String>> deleteEvent(@PathVariable("id") int id) {
        Map<String, String> response = new HashMap<>();

        eventsService.deleteBand(id);
        response.put("message", "Deleted event with id " + id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
