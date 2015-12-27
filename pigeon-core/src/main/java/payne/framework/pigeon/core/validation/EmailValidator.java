package payne.framework.pigeon.core.validation;

import java.util.regex.Pattern;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class EmailValidator implements ConstraintValidator<Email, CharSequence> {
	private Pattern pattern;

	public void initialize(Email constraintAnnotation) {
		pattern = Pattern.compile("^[a-zA-Z0-9._%+-]+@(?!.*\\.\\..*)[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}$");
	}

	public boolean isValid(CharSequence value, ConstraintValidatorContext context) {
		return value == null || pattern.matcher(value).matches();
	}

}
