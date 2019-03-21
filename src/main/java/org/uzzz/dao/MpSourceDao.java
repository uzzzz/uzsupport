package org.uzzz.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.uzzz.bean.MpSource;

@Repository
public interface MpSourceDao extends JpaRepository<MpSource, Long> {
}
