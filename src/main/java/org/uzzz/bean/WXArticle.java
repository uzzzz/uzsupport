package org.uzzz.bean;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "wx_article")
public class WXArticle implements Serializable {

	private static final long serialVersionUID = 2499480505722781215L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private long id;

	@Column(name = "biz")
	private String biz;

	@Column(name = "mid")
	private String mid;

	@Column(name = "idx")
	private int idx;

	@Column(name = "sn")
	private String sn;

	@Column(name = "author")
	private String author;

	@Column(name = "content")
	private String content;

	@Column(name = "content_url", length = 2048)
	private String contentUrl;

	@Column(name = "cover", length = 2048)
	private String cover;

	@Column(name = "digest", length = 2048)
	private String digest;

	@Column(name = "source_url", length = 1024)
	private String sourceUrl;

	@Column(name = "title")
	private String title;

	@Column(name = "description")
	private String description;

	@Column(name = "datetime")
	private Date datetime;

	@Column(name = "multi")
	private int multi;
}
