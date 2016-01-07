package payne.framework.pigeon.generation.objc.converter;

import java.util.Date;

public class DateConverter extends ObjectiveCConverter {

	@Override
	protected boolean supports(Class<?> clazz) {
		return Date.class.isAssignableFrom(clazz);
	}

	@Override
	protected String convert(Class<?> clazz) {
		if (supports(clazz)) {
			return "NSDate *";
		}
		return super.convert(clazz);
	}

	@Override
	public String convert(Class<?> clazz, String name) {
		if (supports(clazz)) {
			return name + " ? @(" + name + ".timeIntervalSince1970 * 1000) : [NSNull null]";
		}
		throw new IllegalArgumentException("can not convert class " + clazz + " by using converter " + this);
	}

}
