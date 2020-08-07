package data.util;

import java.util.regex.MatchResult;

public class MyMatchResult implements MatchResult {

	private MatchResult matchResult;

	public MyMatchResult(MatchResult matchResult) {
		this.matchResult = matchResult;
	}

	@Override
	public int start() {
		return this.matchResult.start();
	}

	@Override
	public int start(int group) {
		return this.matchResult.start(group);
	}

	@Override
	public int end() {
		return this.matchResult.end();
	}

	@Override
	public int end(int group) {
		return this.matchResult.end(group);
	}

	@Override
	public String group() {
		return this.matchResult.group();
	}

	@Override
	public String group(int group) {
		return this.matchResult.group(group);
	}

	@Override
	public int groupCount() {
		return this.matchResult.groupCount();
	}

	public String[] map() {
		var s = new String[groupCount() + 1];
		s[0] = group();
		for (int i = 1; i <= groupCount(); i++) {
			s[i] = group(i);
		}
		return s;
	}
}
