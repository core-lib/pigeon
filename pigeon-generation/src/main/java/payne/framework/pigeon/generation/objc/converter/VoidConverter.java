package payne.framework.pigeon.generation.objc.converter;


public class VoidConverter extends ObjectiveCConverter {

	@Override
	protected boolean supports(Class<?> clazz) {
		return clazz == Void.TYPE;
	}

	@Override
	protected String convert(Class<?> clazz) {
		if (supports(clazz)) {
			return "void";
		}
		return super.convert(clazz);
	}

}
