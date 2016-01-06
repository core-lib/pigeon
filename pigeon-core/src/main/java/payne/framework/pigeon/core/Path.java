package payne.framework.pigeon.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import payne.framework.pigeon.core.annotation.Accept.Mode;

public class Path implements Serializable {
	private static final long serialVersionUID = 4174725239805062242L;
	public static final Pattern PATTERN = Pattern.compile("\\{(?:(\\w+)\\:)?(.*?)\\}");

	private final String definition;
	private final Mode mode;
	private final String expression;
	private final Pattern pattern;
	private final List<String> variables;

	public Path(String definition, Mode mode) {
		super();
		this.definition = definition;
		this.mode = mode;
		// 处理路径变量和查询参数问题,将路径变量拿出来作为请求参数的一部分交给解析器解析
		List<String> variables = new ArrayList<String>();
		Matcher matcher = PATTERN.matcher(definition);
		String regex = definition;
		while (matcher.find()) {
			String name = matcher.group(1);
			String regular = matcher.group(2);
			// 如果group(1) == null 的话其实整个都是名称 例如 /{page}/{size} 所以应该匹配所有字符
			regex = regex.replace(matcher.group(), "(" + (name != null ? regular : "[^/]*") + ")");
			variables.add(name != null ? name : regular);
		}
		this.pattern = Pattern.compile(regex);
		this.expression = regex;
		this.variables = Collections.unmodifiableList(variables);
	}

	public String getDefinition() {
		return definition;
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

	public List<String> getVariables() {
		return variables;
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
