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
import org.uzzz.bean.JavaClass;

@Repository
public interface JavaClassDao extends JpaRepository<JavaClass, Long> {

    public JavaClass findByName(String name);

}
