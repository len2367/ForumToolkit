package com.signatures.controllers;

import com.signatures.entities.Company;
import com.signatures.entities.Document;
import com.signatures.responses.DocumentResponse;
import com.signatures.services.interfaces.TokenService;
import com.signatures.services.interfaces.data.DocumentService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Validated
@RestController
@RequestMapping("/documents")
@AllArgsConstructor
public class DocumentController {
    private final TokenService<Company> tokenService;
    private final DocumentService<Long, Document, Long> documentService;

    @GetMapping
    @ApiOperation(value = "Get all available documents")
    @ApiResponses(value = {
            @ApiResponse(code = 401, message = "invalid token"),
            @ApiResponse(code = 403, message = "token expired")
    })
    public List<DocumentResponse> getAll(
            @NotBlank @RequestHeader("Authorization") String token,
            @Min(value = 1L, message = "The value must be more than 0") @RequestParam(name = "limit", required = false) Integer limit,
            @Min(value = 0L, message = "The value must be positive") @RequestParam(name = "offset", required = false) Integer offset
    ) {
        return documentService.getDocumentsByCompanyId(
                tokenService.getByToken(token).getId(),
                (limit != null && offset != null) ? PageRequest.of(offset, limit, Sort.by("id")) : null
        )
                .stream()
                .map(Document::generateResponse)
                .collect(Collectors.toList());
    }

    @GetMapping("/download/{documentId}")
    @ApiOperation(value = "Download document by id")
    @ApiResponses(value = {
            @ApiResponse(code = 401, message = "invalid token"),
            @ApiResponse(code = 403, message = "token expired"),
            @ApiResponse(code = 404, message = "document not found")
    })
    public byte[] download(
            @NotBlank @RequestHeader("Authorization") String token,
            @PathVariable("documentId") Long documentId
    ) throws IOException {
        return documentService.getDocumentBytesById(
                documentId,
                tokenService.getByToken(token).getId()
        );
    }
}
