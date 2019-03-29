package org.uzzz.service;

import java.util.List;

public interface PostService {
	List<Long> findAllIds();

	List<String> findAllTitles();
}
