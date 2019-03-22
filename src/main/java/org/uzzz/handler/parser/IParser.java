package org.uzzz.handler.parser;

import java.util.Map;

import org.jsoup.nodes.Element;
import org.uzzz.bean.Parser;
import org.uzzz.compiler.JavaStringCompiler;

public interface IParser {

	public Element parse(Element source);

	public static class ParserFactory {

		public static IParser get(Parser p) throws Exception {
			String name = p.getName();
			String source = p.getSource();
			JavaStringCompiler compiler = new JavaStringCompiler();
			Map<String, byte[]> results = compiler.compile(name + ".java", source);
			Class<?> clazz = compiler.loadClass(name, results);
			return (IParser) clazz.newInstance();
		}
	}

}
