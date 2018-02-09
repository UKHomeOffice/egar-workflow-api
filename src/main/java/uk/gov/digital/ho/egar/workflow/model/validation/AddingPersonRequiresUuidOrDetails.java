package uk.gov.digital.ho.egar.workflow.model.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = { AddingNewPersonValidator.class } )
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface AddingPersonRequiresUuidOrDetails {
	String message() default "";
	Class<?>[] groups() default {};
	Class<? extends Payload>[] payload() default {};
}
