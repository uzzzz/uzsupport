/*
+--------------------------------------------------------------------------
|   
|   ========================================
|    
|   
|
+---------------------------------------------------------------------------
*/
package org.uzzz.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.uzzz.bean.Post;

@Repository
public interface PostDao extends JpaRepository<Post, Long> {
	@Query("select id from Post")
	List<Long> findAllIds();

	@Query("select title from Post")
	List<String> findAllTitles();
}
