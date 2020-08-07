/*
 * Copyright (c) 2012 Lasting Software
 *
 * $Header: $
 */

package data.util;

import java.util.Optional;
import java.util.regex.Pattern;

public enum SourceType {

	href, src, file, all("(?:href|src|file)");

	private Pattern pattern;

	private SourceType() {
		this(Optional.empty());
	}

	private SourceType(final String source) {
		this(Optional.ofNullable(source));
	}

	private SourceType(final Optional<String> source) {
		pattern = Pattern.compile(String.format("%s\\s*[\\=\\:]\\s*[\'\"]*([^\'\"\\s*]+)[\'\"]", source.orElse(name())),
				Pattern.CASE_INSENSITIVE);
	}

	public Pattern pattern() {
		return pattern;
	}

	@Override
	public String toString() {
		return this.name();
	}
}
