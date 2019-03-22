package org.uzzz.dao;

import java.util.Collection;
import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.uzzz.bean.WXArticle;
import org.uzzz.bean.WXArticleContent;

public interface WXArticleContentRepository extends PagingAndSortingRepository<WXArticleContent, Long> {

	public WXArticleContent findByArticleAndLevel(WXArticle article, int level);

	public List<WXArticleContent> findByArticle(WXArticle article);

	public List<WXArticleContent> findByLevel(int level);

	public List<WXArticleContent> findByArticleIn(Collection<WXArticle> articles);

}
