
package org.uzzz.utils;

public interface RandomStringGenerator {

	int getMinLength();

	int getMaxLength();

	String getNewString();

	byte[] getNewStringAsBytes();
}
