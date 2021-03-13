package com.signatures.validation.validators;

import com.signatures.validation.constraints.ImageFileConstraint;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ImageFileValidator implements ConstraintValidator<ImageFileConstraint, MultipartFile> {

    @Override
    public void initialize(ImageFileConstraint constraintAnnotation) {
    }

    @Override
    public boolean isValid(MultipartFile multipartFile, ConstraintValidatorContext constraintValidatorContext) {
        return multipartFile.getContentType().split("/")[0].equals("image");
    }
}
