package com.signatures.controllers;

import com.signatures.entities.Company;
import com.signatures.exceptions.CompanyNotFoundException;
import com.signatures.responses.CompanyResponse;
import com.signatures.services.interfaces.TokenService;
import com.signatures.services.interfaces.data.CompanyService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/company")
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CompanyController {
    private final TokenService<Company> tokenService;
    private final CompanyService companyService;

    @GetMapping
    @ApiOperation(value = "Get company info by token")
    @ApiResponses(value = {
            @ApiResponse(code = 401, message = "invalid token"),
            @ApiResponse(code = 403, message = "token expired"),
            @ApiResponse(code = 404, message = "company not found")
    })
    public CompanyResponse company(@RequestHeader("Authorization") String token) {
        return companyService.findById(
                tokenService.getByToken(token).getId()
        )
                .orElseThrow(CompanyNotFoundException::new)
                .generateResponse();
    }
}
