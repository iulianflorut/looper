package data.looper;

import java.util.function.Supplier;
import java.util.stream.IntStream;

import data.util.Util;

public class Nothing {

	private static Supplier<Util> util = Util::new;

	@Execute(false)
	protected static void crestinOrtodox(Filter f) {

		IntStream.range(1, 31)
				.forEach(i -> f.filterForEach("https://www.crestinortodox.ro/predici-audio-mp3/pagina-" + i + "/",
						"/(?<g1>[a-z-]+)/(?<g2>[a-z-]+-[0-9]+)[.]html$",
						t -> f.filterForEach(t, "/audio/.+(?<g3>\\.[a-zA-Z0-9]+)",
								k -> f.download(k, "media/predici/#g1#", "#g2##g3#"))));
	}

	@Execute(false)
	protected static void test(Filter f) {

		IntStream.range(1, 98)
				.forEach(i -> f.filterForEach(
						String.format("https://www.primiiani.ro/planse-de-colorat/%s?page=%d", "insecte", i),
						"/planse-de-colorat/(?<name>[a-z-]+)/[a-z0-9-]+", t -> f.filterForEach(t,
								"/drawing/(?<g1>[a-z0-9-]+\\.jpe?g)", c -> f.download("drawing/#name#", "#g1#", c))));
	}

	@Execute(false)
	protected static void povesti(Filter f) {

		f.filterForEach("https://archive.org/details/5SaptaminiInBalon_201306", "/(?<g1>[^/]+\\.ogg)",
				t -> f.download(t, "povesti/", "#g1#"));
	}

	@Execute(false)
	protected static void povesti1(Filter f) {

		IntStream.range(1, 100).forEach(i -> f.filterForEach(
				"https://archive.org/search.php?query=%28language%3Arum+OR+language%3A%22Romanian%22%29&and%5B%5D=mediatype%3A%22audio%22&and%5B%5D=subject%3A%22poveste%22&page="
						+ i,
				"/details/[^/]+$",
				t -> f.filterFirst(t, "/(?<g1>[^/]+\\.mp3)", c -> f.download(c, "povesti/", "#g1#"))));
	}

	@Execute(false)
	protected static void channels(Filter f) {

		var u = util.get();

		String channel = "<channel id=\"%s\" type=\"0\" btype=\"0\" language=\"ro\">\r\n"
				+ "			<name>%s</name>\r\n" + "			<status>2</status>\r\n"
				+ "			<region en=\"\"/>\r\n" + "			<class en=\"Filme\">0</class>\r\n"
				+ "			<user_count>0</user_count>\r\n" + "			<sn>0</sn>\r\n"
				+ "			<visit_count>0</visit_count>\r\n" + "			<start_from/>\r\n"
				+ "			<stream_type/>\r\n" + "			<kbps>0</kbps>\r\n" + "			<qs>0</qs>\r\n"
				+ "			<qc>0</qc>\r\n" + "			<sop_address>\r\n" + "				<item>%s</item>\r\n"
				+ "			</sop_address>\r\n" + "			<description>%s</description>\r\n" + "		</channel>";

		f.filter("https://mywebtv.info", "/(?<g1>[a-z-0-9]+)$", cc -> {
			cc.distinct().sorted().forEachOrdered(p -> {

				var last = p.split("/")[p.split("/").length - 1];
				f.filterForEach(p, c -> c.startsWith("sop:"), c -> {

					u.first(c, "/([0-9]+)$")
							.ifPresent(id -> System.out.println(String.format(channel, id, last, c, last)));
				});
			});
		});
	}
}
