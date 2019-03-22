package org.uzzz.bean;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "wx_article_content")
public class WXArticleContent implements Serializable {

	private static final long serialVersionUID = 109455637610040965L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private long id;

	@ManyToOne
	@JoinColumn(name = "wx_article_id", nullable = false)
	private WXArticle article;

	@Column(name = "url", length = 1024)
	private String url;

	@Column(name = "title", length = 1024)
	private String title;

	// 完整的html源代码
	@Lob
	@Column(name = "source")
	private String source;

	@Column(name = "level")
	private int level = 0;

}
