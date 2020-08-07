package data.looper;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;

import data.util.Progress;
import data.util.SourceType;
import data.util.URLReader;
import data.util.Util;

public class Filter {

	private static final Pattern group_pattern = Pattern.compile("\\(\\?<([a-zA-Z][a-zA-Z0-9]*)>");
	private static final Pattern group_name = Pattern.compile("#([a-zA-Z0-9]+)#");
	private final static Pattern filePattern = Pattern.compile("/(([^/]+)\\.([a-zA-Z0-9]*))$");
	private final static Pattern folderPattern = Pattern.compile("(/([^/]+)/)");

	private Supplier<Util> util = Util::new;

	private Map<String, String> groups = new HashMap<>();

	private Set<String> links = new HashSet<String>();

	void filter(final String target, final Predicate<String> p, final Consumer<Stream<String>> c) {

		URLReader.connect(target).ifPresent(con -> {

			try (var in = Optional.of(con.getInputStream()).map(InputStreamReader::new).map(BufferedReader::new)
					.get()) {

				c.accept(in.lines().flatMap(line -> util.get().result(line, SourceType.all.pattern()))
						.map(x -> getTarget(con, x)).filter(Optional::isPresent).map(Optional::get).filter(p)
						.filter(Predicate.not(links::contains)).peek(System.out::println).peek(links::add));
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}

	void filter(final String url, final String regexp, final Consumer<Stream<String>> c) {
		filter(url, Pattern.compile(bindGroups(regexp)), c);
	}

	void filter(final String url, final Pattern pattern, final Consumer<Stream<String>> c) {
		filter(url, pattern.asPredicate(), k -> c.accept(k.peek(x -> findGroups(x, pattern))));
	}

	public void filterFirst(final String url, final Pattern pattern, final Consumer<String> c) {
		filter(url, pattern, cc -> cc.findFirst().ifPresent(c));
	}

	public void filterFirst(final String url, final String regexp, final Consumer<String> c) {
		filter(url, regexp, cc -> cc.findFirst().ifPresent(c));
	}

	public void filterFirst(final String url, final Predicate<String> p, final Consumer<String> c) {
		filter(url, p, cc -> cc.findFirst().ifPresent(c));
	}

	public void filterForEach(final String target, final Predicate<String> p, final Consumer<String> c) {
		filter(target, p, cc -> cc.forEach(c));
	}

	public void filterForEach(final String url, final Pattern pattern, final Consumer<String> c) {
		filter(url, pattern, cc -> cc.forEach(c));
	}

	public void filterForEach(final String url, final String regexp, final Consumer<String> c) {
		filter(url, regexp, cc -> cc.forEach(c));
	}

	private void download(final String url, Optional<String> root, Optional<String> file) {

		var t = new Progress();

		var fileName = URLDecoder.decode(bindGroups(file.orElse(util.get().last(url, filePattern).orElse(url))),
				StandardCharsets.UTF_8);
		var folderName = URLDecoder.decode(
				bindGroups(root.orElse(
						util.get().last(url, folderPattern).orElse(url).replace('/', File.separatorChar).substring(1))),
				StandardCharsets.UTF_8);

		Optional.of(Paths.get(folderName + File.separatorChar + fileName).toFile()).filter(Predicate.not(File::exists))
				.ifPresent(f -> {
					try {
						t.start();
						FileUtils.copyURLToFile(new URL(url), f, 5000, 5000);
					} catch (IOException e) {
						e.printStackTrace();
					} finally {
						t.shutbown();
					}
				});
	}

	public void download(final String url, final String root, final String fileName) {
		download(url, Optional.ofNullable(root), Optional.ofNullable(fileName));
	}

	public void download(final String url, final String root) {
		download(url, Optional.ofNullable(root), Optional.empty());
	}

	public void download(final String url) {
		download(url, Optional.empty(), Optional.empty());
	}

	public void test(final String url, final Predicate<String> predicate) {
		filterForEach(url, predicate, System.out::println);
	}

	public void test(final String url, final String regexp) {
		test(url, Pattern.compile(bindGroups(regexp)).asPredicate());
	}

	public void test(final String url) {
		test(url, x -> true);
	}

	private String getTargetFromParameter(final String url) {
		return Stream.of(url).filter(x -> x.contains("&")).flatMap(x -> Stream.of(x.split("&"))).map(x -> x.split("="))
				.filter(x -> x.length > 1).map(x -> x[1]).filter(x -> x.startsWith("http")).findFirst().orElse(url);
	}

	private Optional<String> getTarget(final URLConnection conn, final String url) {

		try {
			return Optional.of(conn.getURL().toURI()
					.resolve(getTargetFromParameter(URLDecoder.decode(url, StandardCharsets.UTF_8))).toString());
		} catch (IllegalArgumentException | URISyntaxException e) {
			return Optional.empty();
		}
	}

	private void findGroups(String value, Pattern pattern) {

		var namedGroups = util.get().result(pattern.toString(), group_pattern, 1).collect(Collectors.toSet());

		if (namedGroups.isEmpty())
			return;

		var matcher = pattern.matcher(value);

		if (matcher.find()) {
			namedGroups.forEach(x -> groups.put(x, matcher.group(x)));
		}
	}

	public String bindGroups(String value) {

		var matcher = group_name.matcher(value);

		var sb = new StringBuilder();

		while (matcher.find()) {

			var group = matcher.group(1);

			if (groups.keySet().contains(group)) {
				matcher.appendReplacement(sb, group(group));
			}
		}

		matcher.appendTail(sb);

		return sb.toString();
	}

	public String group(String name) {
		return groups.get(name);
	}
}
