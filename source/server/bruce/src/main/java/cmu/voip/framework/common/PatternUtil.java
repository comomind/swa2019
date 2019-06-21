package cmu.voip.framework.common;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatternUtil {
	private static final Pattern ESCAPE_PATTERN = Pattern.compile("(\\.|\\\\|\\[|\\]|\\^|\\$|\\+|\\{|\\}|\\(|\\)|\\|)");

	private static final Pattern ASTERISK_PATTERN = Pattern.compile("\\*");

	private static final Pattern QUESTION_PATTERN = Pattern.compile("\\?");

	private static final Pattern MULTIPLE_PATTERN = Pattern.compile("\\;");

	public static Pattern compileWildcardPattern(String wildcard) {
		return compileWildcardPattern(wildcard, false);
	}

	public static Pattern[] compileWildcardPattern(List<String> wildcard) {
		Pattern[] patterns = new Pattern[wildcard.size()];
		for (int i = 0; i < wildcard.size(); ++i) {
			patterns[i] = compileWildcardPattern(((String) wildcard.get(i)).trim(), false);
		}
		return patterns;
	}

	public static boolean matches(Pattern pattern, String string) {
		return pattern.matcher(string).matches();
	}

	public static boolean matches(Pattern[] patterns, String string) {
		for (int i = 0; i < patterns.length; ++i) {
			if (patterns[i].matcher(string).matches())
				return true;
		}
		return false;
	}

	public static Pattern compileWildcardPattern(String wildcard, boolean ignoreCase) {
		String wildcardLocal = wildcard;
		String wildcardPattern = "";

		Matcher escapeMatcher = ESCAPE_PATTERN.matcher(wildcardLocal);
		wildcardLocal = escapeMatcher.replaceAll("\\\\$1");

		Matcher asteriskMatcher = ASTERISK_PATTERN.matcher(wildcardLocal);
		wildcardLocal = asteriskMatcher.replaceAll("(.*)");

		Matcher questionmarkMatcher = QUESTION_PATTERN.matcher(wildcardLocal);
		wildcardLocal = questionmarkMatcher.replaceAll("(.)");

		Matcher multipleMacher = MULTIPLE_PATTERN.matcher(wildcardLocal);
		wildcardLocal = multipleMacher.replaceAll(")|(");

		wildcardPattern = "(" + wildcardLocal + ")";

		if (!(ignoreCase)) {
			return Pattern.compile(wildcardPattern);
		}
		return Pattern.compile(wildcardPattern, 2);
	}
}
