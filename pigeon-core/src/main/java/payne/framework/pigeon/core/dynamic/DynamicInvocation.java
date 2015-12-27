package payne.framework.pigeon.core.dynamic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import payne.framework.pigeon.core.Invocation;

import com.googlecode.openbeans.Introspector;
import com.googlecode.openbeans.PropertyDescriptor;

public abstract class DynamicInvocation implements Serializable {
	private static final long serialVersionUID = -2197208924027329658L;

	private Map<String, String> properties = new TreeMap<String, String>();
	private String path;

	protected DynamicInvocation() {

	}

	protected DynamicInvocation(Invocation invocation) throws Exception {
		this.properties = invocation.getProperties();
		this.path = invocation.getPath();
		for (int i = 0; invocation.getArguments() != null && i < invocation.getArguments().length; i++) {
			if (invocation.getArguments()[i] == null) {
				continue;
			}
			PropertyDescriptor descriptor = new PropertyDescriptor("argument" + i, this.getClass());
			descriptor.getWriteMethod().invoke(this, invocation.getArguments()[i]);
		}
		if (invocation.getResult() != null) {
			PropertyDescriptor descriptor = new PropertyDescriptor("result", this.getClass());
			descriptor.getWriteMethod().invoke(this, invocation.getResult());
		}
	}

	public Invocation toInvocation() throws Exception {
		Invocation invocation = new Invocation();
		invocation.setProperties(properties);
		invocation.setPath(path);
		List<Object> arguments = new ArrayList<Object>();
		Object result = null;
		PropertyDescriptor[] descriptors = Introspector.getBeanInfo(this.getClass()).getPropertyDescriptors();
		for (PropertyDescriptor descriptor : descriptors) {
			if (descriptor.getName().matches("argument\\d+")) {
				int index = Integer.valueOf(descriptor.getName().substring("argument".length()));
				arguments.add(index, descriptor.getReadMethod().invoke(this));
			}
			if (descriptor.getName().equals("result")) {
				result = descriptor.getReadMethod().invoke(this);
			}
		}
		invocation.setArguments(arguments.toArray());
		invocation.setResult(result);
		return invocation;
	}

	public Map<String, String> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, String> properties) {
		this.properties = properties;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

}
