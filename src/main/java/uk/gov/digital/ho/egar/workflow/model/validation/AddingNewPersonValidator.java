package uk.gov.digital.ho.egar.workflow.model.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import uk.gov.digital.ho.egar.workflow.model.rest.PersonWithId;

public class AddingNewPersonValidator implements ConstraintValidator<AddingPersonRequiresUuidOrDetails, PersonWithId>{

	@Override
	public void initialize(AddingPersonRequiresUuidOrDetails requiredField) {
		
	}

	@Override
	public boolean isValid(PersonWithId person, ConstraintValidatorContext ctx) {
		if (person == null)
            // Nothing so this is ok
            return true;
		
		boolean isOk = true;
		if(person.getPersonId() != null && person.getPersonDetails() != null) isOk = false;
		
		boolean isDefaultMessage = "".equals(ctx
                .getDefaultConstraintMessageTemplate());
		
		 if (!isOk && isDefaultMessage) {
             ctx.disableDefaultConstraintViolation();
             ctx
                     .buildConstraintViolationWithTemplate("Person must only have one: UUID or Details")
                     .addBeanNode()
                     .addConstraintViolation();
         }
		return isOk;
	}

}
