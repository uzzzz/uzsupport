/*
+--------------------------------------------------------------------------
|   
|   ========================================
|    
|   
|
+---------------------------------------------------------------------------
*/
package org.uzzz.bean;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 内容表
 */
@Data
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "mp_source")
public class MpSource implements Serializable {

	private static final long serialVersionUID = 3792607302376117438L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(name = "host", length = 255)
	private String host;

	@Column(name = "path", length = 2048)
	private String path;

	@Column(name = "content_type", length = 255)
	private String contentType;

	@Column(name = "source", columnDefinition = "TEXT")
	private String source;

}