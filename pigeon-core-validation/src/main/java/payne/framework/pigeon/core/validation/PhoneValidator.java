package payne.framework.pigeon.core.validation;

import java.util.regex.Pattern;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PhoneValidator implements ConstraintValidator<Phone, CharSequence> {
	private Pattern pattern;

	public void initialize(Phone constraintAnnotation) {
		pattern = Pattern.compile("(^(\\d{3,4}-)?\\d{7,8})$|(13[0-9]{9})");
	}

	public boolean isValid(CharSequence value, ConstraintValidatorContext context) {
		return value == null || pattern.matcher(value).matches();
	}
}
