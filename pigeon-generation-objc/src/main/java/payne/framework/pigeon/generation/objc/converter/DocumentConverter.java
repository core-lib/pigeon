package payne.framework.pigeon.generation.objc.converter;

import payne.framework.pigeon.core.Document;

public class DocumentConverter extends ObjectiveCConverter {

	protected boolean supports(Class<?> clazz) {
		return Document.class.isAssignableFrom(clazz);
	}

	@Override
	protected String convert(Class<?> clazz) {
		if (supports(clazz)) {
			return "PFPDocument *";
		}
		return super.convert(clazz);
	}

	@Override
	protected String convert(Class<?> clazz, String name) {
		return name + " ? " + name + ".dictionary : [NSNull null]";
	}

}
