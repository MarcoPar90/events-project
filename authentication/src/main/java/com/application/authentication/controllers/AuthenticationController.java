package com.application.authentication.controllers;

import com.application.authentication.dao.response.JwtAuthenticationRefreshResponse;
import com.application.authentication.exception.ErrorMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.application.authentication.dao.request.SignUpRequest;
import com.application.authentication.dao.request.SigninRequest;
import com.application.authentication.dao.response.JwtAuthenticationResponse;
import com.application.authentication.services.AuthenticationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuthAuthentication")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/signup")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User signup seccessfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SigninRequest.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request - Some required fields is missing within the body", content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "409", description = "Conflict - User alredy exist", content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorMessage.class))),
    })
    @Operation(summary = "Signup request")
    public ResponseEntity<JwtAuthenticationResponse> signup(@RequestBody SignUpRequest request) {
        return ResponseEntity.ok(authenticationService.signup(request));
    }

    @PostMapping("/signin")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User signin seccessfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SigninRequest.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request - Some required fields is missing within the body", content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorMessage.class))),
    })
    @Operation(summary = "Signin request")
    public ResponseEntity<JwtAuthenticationResponse> signin(@RequestBody SigninRequest request) {
        return ResponseEntity.ok(authenticationService.signin(request));
    }

    @PostMapping("/refresh")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Token refresh seccessfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SigninRequest.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request - Some required fields is missing within the body", content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Token expired", content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorMessage.class))),
    })
    @Operation(summary = "Generate new token")
    public ResponseEntity<JwtAuthenticationRefreshResponse> refresh(@RequestBody JwtAuthenticationResponse refreshToken) {
        return ResponseEntity.ok(authenticationService.refresh(refreshToken));
    }
}
