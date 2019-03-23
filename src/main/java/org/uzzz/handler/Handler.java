package org.uzzz.handler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class Handler<T> {

	public static ExecutorService executor = Executors.newFixedThreadPool(64);

	public final Runnable with(final T object) {
		return new Runnable() {
			public void run() {
				Handler.this.run(object);
			}
		};
	}

	public abstract void run(T object);

	public String decodeHTML(String source) {

		if (source == null) {
			return null;
		}

		if (source.contains("&amp;")) {
			return decodeHTML(source.replace("&amp;", "&"));
		} else if (source.contains("\\/")) {
			return decodeHTML(source.replace("\\/", "/"));
		} else {
			return source;
		}

	}

	public String substring(String content, String prefix, String suffix) {
		if (content == null || prefix == null || suffix == null) {
			return "";
		}
		int start = content.indexOf(prefix);
		if (start >= 0) {
			int end = content.indexOf(suffix, start + prefix.length());
			if (end > start) {
				return content.substring(start + prefix.length(), end);
			}
		}
		return "";
	}

}
