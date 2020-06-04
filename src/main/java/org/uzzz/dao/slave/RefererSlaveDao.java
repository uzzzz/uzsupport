package org.uzzz.dao.slave;

import org.springframework.data.jpa.repository.JpaRepository;
import org.uzzz.bean.Referer;

public interface RefererSlaveDao extends JpaRepository<Referer, Long> {

    Referer findByHost(String host);

}
