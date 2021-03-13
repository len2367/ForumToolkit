package com.signatures.controllers;

import com.signatures.entities.Company;
import com.signatures.entities.Document;
import com.signatures.entities.Signature;
import com.signatures.exceptions.DocumentNotFoundException;
import com.signatures.services.interfaces.TokenService;
import com.signatures.services.interfaces.data.DocumentService;
import com.signatures.services.interfaces.data.SignatureService;
import com.signatures.validation.constraints.ImageFileConstraint;
import com.signatures.validation.constraints.NotEmptyFileConstraint;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import java.io.IOException;

@Validated
@RestController
@RequestMapping("/signatures")
@AllArgsConstructor
public class SignatureController {
    private final TokenService<Company> tokenService;
    private final SignatureService<Long, Signature> signatureService;
    private final DocumentService<Long, Document, Long> documentService;

    // TODO: написать проверку на png тип
    @PostMapping("/{documentId}")
    @ApiOperation(value = "Upload new signature")
    @ApiResponses(value = {
            @ApiResponse(code = 401, message = "invalid token"),
            @ApiResponse(code = 403, message = "token expired"),
            @ApiResponse(code = 404, message = "document not found")
    })
    public HttpStatus upload(
            @NotBlank @RequestHeader("Authorization") String token,
            @PathVariable("documentId") Long documentId,
            @NotBlank @RequestParam("fio") String fio,
            @ImageFileConstraint @NotEmptyFileConstraint @RequestParam("file") MultipartFile file
    ) throws IOException {
        if (!documentService.hasCompanyAccessToDocument(
                tokenService.getByToken(token).getId(),
                documentId
        )) throw new DocumentNotFoundException();

        signatureService.saveToDisk(
                signatureService.save(
                        new Signature(
                                fio,
                                new Document(documentId)
                        )
                ).getDiskName(),
                file
        );

        return HttpStatus.OK;
    }
}
