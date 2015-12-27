package payne.framework.pigeon.core.formatting;

import payne.framework.pigeon.core.Pigeons;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.AnnotatedClass;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

public class DefaultJacksonAnnotationIntrospector extends JacksonAnnotationIntrospector {
	private static final long serialVersionUID = 1795964095927132694L;
	private SimpleFilterProvider filters = new SimpleFilterProvider();

	public DefaultJacksonAnnotationIntrospector(ObjectMapper mapper) {
		mapper.setSerializationInclusion(Include.NON_EMPTY);
		mapper.setFilters(filters);
	}

	public Object findFilterId(Annotated annotated) {
		// 如果有JsonFilter注解那么 transient字段将交给定义的JsonFilter处理
		Object id = super.findFilterId(annotated);
		if (id != null) {
			return id;
		}
		// 如果没有 而且是个类 那么可以忽略transient字段
		if (annotated instanceof AnnotatedClass) {
			try {
				filters.findPropertyFilter(annotated.getName(), null);
			} catch (Throwable e) {
				filters.addFilter(annotated.getName(), SimpleBeanPropertyFilter.serializeAllExcept(Pigeons.getTransientProperties((Class<?>) annotated.getAnnotated())));
			}
			return annotated.getName();
		} else {
			return null;
		}
	}

}