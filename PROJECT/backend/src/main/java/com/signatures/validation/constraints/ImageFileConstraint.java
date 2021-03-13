package com.signatures.validation.constraints;

import com.signatures.validation.validators.ImageFileValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ImageFileValidator.class)
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ImageFileConstraint {
    String message() default "File is not image";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
