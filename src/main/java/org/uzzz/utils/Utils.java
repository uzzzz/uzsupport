package org.uzzz.utils;

import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

	public static String getMatcher(String regex, String source) {
		Pattern pattern = Pattern.compile(regex); // 匹配规则
		Matcher matcher = pattern.matcher(source); // 这个是被测试的内容
		return matcher.find() ? matcher.group(1) : "";
	}

	public static <T> T tryGet(Supplier<T> action, T defaultValue) {
		try {
			return action.get();
		} catch (Exception e) {
			return defaultValue;
		}
	}

	public static void tryCatch(Runnable action) {
		try {
			action.run();
		} catch (Exception e) {
		}
	}
}
