package org.uzzz.dao;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;
import org.uzzz.bean.Parser;

public interface ParserRepository extends PagingAndSortingRepository<Parser, Long> {

	public Parser findByName(String name);

	public Parser findByBiz(String biz);

	@Modifying(clearAutomatically = true)
	@Transactional
	@Query("update Parser p set p.status = ?2 where p.id = ?1")
	public int enable(long id, int status);

}
