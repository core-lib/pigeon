package payne.framework.pigeon.core.observation;

import java.io.Serializable;
import java.util.Date;

public class Event implements Serializable {
	private static final long serialVersionUID = 2154989989079491745L;

	private final String name;
	private final Object source;
	private final Date time = new Date();
	private final Object data;

	public Event(String name, Object source, Object data) {
		super();
		this.name = name;
		this.source = source;
		this.data = data;
	}

	public String getName() {
		return name;
	}

	public Object getSource() {
		return source;
	}

	public Date getTime() {
		return time;
	}

	public Object getData() {
		return data;
	}

	@Override
	public String toString() {
		return "Event [name=" + name + ", source=" + source + ", time=" + time + ", data=" + data + "]";
	}

}
