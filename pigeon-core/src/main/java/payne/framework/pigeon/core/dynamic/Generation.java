package payne.framework.pigeon.core.dynamic;

public class Generation {
	private final String name;
	private final byte[] bytes;

	public Generation(String name, byte[] bytes) {
		super();
		this.name = name;
		this.bytes = bytes;
	}

	public String getName() {
		return name;
	}

	public byte[] getBytes() {
		return bytes;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Generation other = (Generation) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Generation [name=" + name + "]";
	}

}
