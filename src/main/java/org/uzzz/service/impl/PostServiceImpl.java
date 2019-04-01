
package org.uzzz.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.uzzz.bean.Post;
import org.uzzz.bean.PostAttribute;
import org.uzzz.dao.PostAttributeDao;
import org.uzzz.dao.PostDao;
import org.uzzz.service.PostService;

@Service
public class PostServiceImpl implements PostService {

	@Autowired
	private PostDao postDao;

	@Autowired
	private PostAttributeDao postAttributeDao;

	@Override
	public List<Long> findAllIds() {
		return postDao.findAllIds();
	}

	@Override
	public List<String> findAllTitles() {
		return postDao.findAllTitles();
	}

	@Override
	@Transactional
	public void delete(long id) {
		postDao.delete(id);
		postAttributeDao.delete(id);
	}

	@Override
	public Post getPost(long id) {
		return postDao.findOne(id);
	}

	@Override
	public PostAttribute getPostAttribute(long id) {
		return postAttributeDao.findOne(id);
	}
}
