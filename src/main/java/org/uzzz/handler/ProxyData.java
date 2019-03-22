package org.uzzz.handler;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@Data
@AllArgsConstructor
@ToString
public class ProxyData {
	private String host;
	private String path;
	private String contentType;
	private String source;
}
