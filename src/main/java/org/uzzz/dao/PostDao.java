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

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.uzzz.bean.Post;

@Repository
public interface PostDao extends JpaRepository<Post, Long> {

}
