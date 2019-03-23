package org.uzzz.handler;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ProxyData implements Serializable {

	private static final long serialVersionUID = -4093058913722194077L;

	private String host;
	private String path;
	private String contentType;
	private String source;
}
