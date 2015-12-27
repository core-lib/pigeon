package payne.framework.pigeon.core.validation;

import java.lang.reflect.Method;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidationException;
import javax.validation.ValidatorFactory;
import javax.validation.executable.ExecutableValidator;

import payne.framework.pigeon.core.Interceptor;
import payne.framework.pigeon.core.Invocation;

public class ValidationInterceptor implements Interceptor {
	private ValidatorFactory validatorFactory;

	public ValidationInterceptor() {
		super();
		this.validatorFactory = Validation.buildDefaultValidatorFactory();
	}

	public ValidationInterceptor(ValidatorFactory validatorFactory) {
		super();
		this.validatorFactory = validatorFactory;
	}

	public Object intercept(Invocation invocation) throws Exception {
		Object target = invocation.getImplementation();
		Method method = invocation.getMethod();
		Object[] parameters = invocation.getArguments();

		// 校验参数
		ExecutableValidator validator = validatorFactory.getValidator().forExecutables();
		Set<ConstraintViolation<Object>> violations = validator.validateParameters(target, method, parameters != null ? parameters : new Object[0]);

		// 如果校验失败
		if (violations != null && !violations.isEmpty()) {
			StringBuilder message = new StringBuilder();
			for (ConstraintViolation<Object> violation : violations) {
				message.append(violation.getPropertyPath()).append(" : ").append(violation.getMessage()).append(" ; ");
			}
			throw new ValidationException(message.toString());
		}

		Object result = invocation.invoke();

		// 校验返回值
		violations = validator.validateReturnValue(target, method, result);

		// 如果校验失败
		if (violations != null && !violations.isEmpty()) {
			StringBuilder message = new StringBuilder();
			for (ConstraintViolation<Object> violation : violations) {
				message.append(violation.getPropertyPath()).append(" : ").append(violation.getMessage()).append(" ; ");
			}
			throw new ValidationException(message.toString());
		}

		return result;
	}

	public ValidatorFactory getValidatorFactory() {
		return validatorFactory;
	}

	public void setValidatorFactory(ValidatorFactory validatorFactory) {
		this.validatorFactory = validatorFactory;
	}

}
