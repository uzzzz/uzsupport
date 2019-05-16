package org.uzzz.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.uzzz.bean.User;
import org.uzzz.dao.UserDao;

@Controller
@RequestMapping("once")
public class OnceController {

	@Autowired
	private UserDao userDao;

//	@GetMapping("ava")
//	@ResponseBody
	public String ava() throws IOException {
		List<User> users = userDao.findAll();
		for (User user : users) {
			String ava = user.getAvatar();
			if (ava.startsWith("https://uzstatic.belost.xyz")) {
				ava = ava.substring("https://uzstatic.belost.xyz".length());
				user.setAvatar(ava);
				userDao.save(user);
			}
		}
		return "OK";
	}

}
