package payne.framework.pigeon.server;

import java.util.regex.Pattern;

import payne.framework.pigeon.core.annotation.Accept.Mode;

public class Path {
	private final Mode mode;
	private final String expression;
	private final Pattern pattern;

	public Path(Mode mode, Pattern pattern) {
		super();
		this.mode = mode;
		this.pattern = pattern;
		this.expression = pattern.pattern();
	}

	public Mode getMode() {
		return mode;
	}

	public String getExpression() {
		return expression;
	}

	public Pattern getPattern() {
		return pattern;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((expression == null) ? 0 : expression.hashCode());
		result = prime * result + ((mode == null) ? 0 : mode.hashCode());
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
		Path other = (Path) obj;
		if (expression == null) {
			if (other.expression != null)
				return false;
		} else if (!expression.equals(other.expression))
			return false;
		if (mode != other.mode)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return mode + " " + expression;
	}

}
