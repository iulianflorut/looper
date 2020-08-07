package data.util;

import java.io.IOException;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.net.URLConnection;
import java.util.Optional;

public class URLReader {

	private static String cookie;

	public static void login(final String username, final String password) {

		Authenticator.setDefault(new Authenticator() {

			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password.toCharArray());
			}
		});
	}

	public static Optional<URLConnection> connect(final String target) {
		try {
			Optional<URLConnection> conn = Optional.of(new URL(target).openConnection());
			conn.ifPresent(c -> {
				if (cookie != null) {
					c.setRequestProperty("cookie", cookie);
				}
				try {
					c.connect();
				} catch (IOException e) {
					e.printStackTrace();
				}	
				readCookies(c);
			});
			return conn;

		} catch (IOException e) {
			e.printStackTrace();
		}
		return Optional.empty();
	}

	public static void readCookies(URLConnection conn) {
		String headerName = null;
		for (var i = 1; (headerName = conn.getHeaderFieldKey(i)) != null; i++) {
			if (headerName.toLowerCase().equals("set-cookie")) {
				cookie = conn.getHeaderField(i).split(";")[0];
			}
		}
	}

}