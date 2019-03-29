
package org.uzzz.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.uzzz.dao.PostDao;
import org.uzzz.service.PostService;

@Service
public class PostServiceImpl implements PostService {

	@Autowired
	private PostDao postDao;

	@Override
	public List<Long> findAllIds() {
		return postDao.findAllIds();
	}

	@Override
	public List<String> findAllTitles() {
		return postDao.findAllTitles();
	}
}
