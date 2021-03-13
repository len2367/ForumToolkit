package com.signatures.validation.constraints;

import com.signatures.validation.validators.NotEmptyFileValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = NotEmptyFileValidator.class)
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface NotEmptyFileConstraint {
    String message() default "File is empty";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
