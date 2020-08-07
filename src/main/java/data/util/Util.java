package data.util;

import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Stream;

final public class Util {

	public Stream<String> result(final String value, final Pattern pattern, int group) {
		return matchResult(value, pattern).map(c -> c.group(group));
	}

	public Stream<String> result(final String value, final Pattern pattern) {
		return result(value, pattern, 1);
	}

	public Stream<MyMatchResult> matchResult(final String value, final Pattern pattern) {
		return pattern.matcher(value).results().map(MyMatchResult::new);
	}

	public Optional<MyMatchResult> matchFirst(final String value, final Pattern pattern) {
		return matchResult(value, pattern).findFirst();
	}

	public Optional<String> first(final String value, final Pattern pattern) {
		return result(value, pattern).findFirst();
	}

	public Optional<String> first(final String value, final Pattern pattern, int group) {
		return result(value, pattern, group).findFirst();
	}

	public Optional<MyMatchResult> matchLast(final String value, final Pattern pattern) {
		return matchResult(value, pattern).reduce((f, s) -> s);
	}

	public Optional<String> last(final String value, final Pattern pattern) {
		return result(value, pattern).reduce((f, s) -> s);
	}

	public Optional<String> last(final String value, final Pattern pattern, int group) {
		return result(value, pattern, group).reduce((f, s) -> s);
	}

	public Stream<String> result(final String value, final String regexp) {
		return result(value, Pattern.compile(regexp));
	}

	public Stream<MyMatchResult> matchResult(final String value, final String regexp) {
		return matchResult(value, Pattern.compile(regexp));
	}

	public Stream<String> result(final String value, final String regexp, int group) {
		return result(value, Pattern.compile(regexp), group);
	}

	public Optional<MyMatchResult> matchFirst(final String value, final String regexp) {
		return matchFirst(value, Pattern.compile(regexp));
	}

	public Optional<String> first(final String value, final String regexp) {
		return first(value, Pattern.compile(regexp));
	}

	public Optional<String> first(final String value, final String regexp, int group) {
		return first(value, Pattern.compile(regexp), group);
	}

	public Optional<MyMatchResult> matchLast(final String value, final String regexp) {
		return matchLast(value, Pattern.compile(regexp));
	}

	public Optional<String> last(final String value, final String regexp) {
		return last(value, Pattern.compile(regexp));
	}

	public Optional<String> last(final String value, final String regexp, int group) {
		return last(value, Pattern.compile(regexp), group);
	}
}
