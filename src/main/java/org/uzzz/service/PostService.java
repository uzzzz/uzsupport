package org.uzzz.service;

import java.util.List;

import org.uzzz.bean.Post;
import org.uzzz.bean.PostAttribute;

public interface PostService {

	List<Long> findAllIds();

	List<String> findAllTitles();

	void delete(long id);

	Post getPost(long id);

	PostAttribute getPostAttribute(long id);
}
