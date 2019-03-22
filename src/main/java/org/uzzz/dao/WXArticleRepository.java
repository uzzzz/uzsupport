package org.uzzz.dao;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.uzzz.bean.WXArticle;

public interface WXArticleRepository extends PagingAndSortingRepository<WXArticle, Long> {

	public WXArticle findByBizAndMidAndIdx(String biz, String mid, int idx);
}
