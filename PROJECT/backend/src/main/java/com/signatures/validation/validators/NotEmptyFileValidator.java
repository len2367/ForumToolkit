package com.signatures.validation.validators;

import com.signatures.validation.constraints.NotEmptyFileConstraint;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class NotEmptyFileValidator implements ConstraintValidator<NotEmptyFileConstraint, MultipartFile> {
    @Override
    public void initialize(NotEmptyFileConstraint constraintAnnotation) {
    }

    @Override
    public boolean isValid(MultipartFile multipartFile, ConstraintValidatorContext constraintValidatorContext) {
        return !multipartFile.isEmpty();
    }
}
