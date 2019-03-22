package org.uzzz.bean;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "parser")
public class Parser implements Serializable {

	private static final long serialVersionUID = 6526511061157056975L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private long id;

	@Column(name = "name", nullable = false, unique = true)
	private String name;

	@Lob
	@Column(name = "source")
	private String source;

	// 帐号主体
	@Column(name = "biz", nullable = false, unique = true)
	private String biz;

	/**
	 * 0: 未启用 <br />
	 * 1: 启用
	 */
	@Column(name = "status", columnDefinition = "INT default 0")
	private int status = 0;

}
