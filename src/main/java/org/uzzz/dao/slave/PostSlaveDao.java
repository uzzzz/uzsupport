/*
+--------------------------------------------------------------------------
|   
|   ========================================
|    
|   
|
+---------------------------------------------------------------------------
*/
package org.uzzz.dao.slave;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.uzzz.dao.PostDao;

@Repository
public interface PostSlaveDao extends PostDao {

	@Query("select id from Post")
	List<Long> findAllIds();

	@Query("select title from Post")
	List<String> findAllTitles();
	
	boolean existsByTitle(String title);

}
