package com.signatures.controllers;

import com.signatures.entities.Company;
import com.signatures.exceptions.BadCodeException;
import com.signatures.responses.AuthorizationResponse;
import com.signatures.responses.RefreshResponse;
import com.signatures.services.interfaces.AuthorizationService;
import com.signatures.services.interfaces.TokenService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AuthorizationController {
    private final TokenService<Company> tokenService;
    private final AuthorizationService<Long, Company> authorizationService;

    @GetMapping("")
    @ApiOperation(value = "Get bearer token by company secret code")
    @ApiResponses(value = {
            @ApiResponse(code = 403, message = "wrong code")
    })
    public AuthorizationResponse auth(
            @RequestParam("id") Long id,
            @RequestParam("code") String code
    ) throws BadCodeException {
        Company company = authorizationService.getByIdAndCode(
                id,
                code
        );
        return new AuthorizationResponse(
                tokenService.generateToken(company),
                company.generateResponse()
        );
    }

    @GetMapping("/refresh")
    @ApiOperation(value = "Refresh bearer token")
    @ApiResponses(value = {
            @ApiResponse(code = 401, message = "invalid token"),
            @ApiResponse(code = 408, message = "token is too expired, try to relogin"),
            @ApiResponse(code = 425, message = "token is not expired")
    })
    public RefreshResponse refresh(
            @NotBlank @RequestHeader("Authorization") String token
    ) {
        return new RefreshResponse(
                tokenService.refreshToken(token)
        );
    }
}
