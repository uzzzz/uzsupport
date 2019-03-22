package org.uzzz.handler.parser;

import org.jsoup.nodes.Element;
import org.springframework.util.StringUtils;

public class DefaultParser implements IParser {

	public Element parse(Element source) {
		source.select("img.__bg_gif").parents().parents().remove();
		source.select("br").remove();
		for (Element e : source.select("p, section")) {
			if (StringUtils.isEmpty(e.html())) {
				e.remove();
			}
		}

		for (Element e : source.select("h1, h2, section")) {
			String style = e.attr("style");
			if (StringUtils.hasText(style)) {
				e.attr("style", style.replace("margin-top", "margin-top-unused").replace("margin-bottom",
						"margin-bottom-unused"));
			}

		}
		return source;
	}

}
