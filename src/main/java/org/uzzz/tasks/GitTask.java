package org.uzzz.tasks;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class GitTask {

	private static Logger log = LoggerFactory.getLogger(GitTask.class);

	@Value("${uzzz.path}")
	private String uzzzPath;

	@Value("${uzzz.org.path}")
	private String uzzzOrgPath;

	public void writeGit(long id, String title, String c, String time) {
		try {
			c = URLEncoder.encode(c, "UTF-8");
			c = "{{ \"" + c + "\" | url_decode}}";
		} catch (UnsupportedEncodingException ee) {
			c = "{% raw %} \n" + c + "\n{% endraw %}";
		}
		String content = "---\n" //
				+ "layout: default\n" //
				+ "title: " + title + "\n" //
				+ "---\n\n" //
				+ c;

		// writeGitForUzzz(id, title, content, time);
		writeGitForUzzzOrg(id, title, content, time);
	}

	private void writeGitForUzzz(long id, String title, String content, String time) {

		try { // write ibz.bz
			String path = uzzzPath + "/_posts/";
			BufferedWriter writer = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(path + time + "-" + id + ".html", false), "UTF-8"));
			writer.write(content);
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
	}

	private void writeGitForUzzzOrg(long id, String title, String content, String time) {

		try { // write uzzz.org
			String path = uzzzOrgPath + "/_posts/";
			BufferedWriter writer = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(path + time + "-" + id + ".html", false), "UTF-8"));
			writer.write(content);
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
	}

	public void commitAndPushGit() {
		// commitAndPushGitForUzzz();
		// commitAndPushGitForUzzzOrg();
	}

	private void commitAndPushGitForUzzz() {
		try {
			long start = System.currentTimeMillis();

			File dir = new File(uzzzPath);
			String[] cmd = new String[] { "/bin/sh", "-c",
					"git add . && git commit -a -m \"crawler\" && git pull && git push origin master" };
			Process process = Runtime.getRuntime().exec(cmd, null, dir);

			// 记录dos命令的返回信息
			StringBuffer resStr = new StringBuffer();
			InputStream in = process.getInputStream();
			Reader reader = new InputStreamReader(in);
			BufferedReader bReader = new BufferedReader(reader);
			for (String res = ""; (res = bReader.readLine()) != null;) {
				resStr.append(res + "\n");
			}
			bReader.close();
			reader.close();

			// 记录dos命令的返回错误信息
			StringBuffer errorStr = new StringBuffer();
			InputStream errorIn = process.getErrorStream();
			Reader errorReader = new InputStreamReader(errorIn);
			BufferedReader eReader = new BufferedReader(errorReader);
			for (String res = ""; (res = eReader.readLine()) != null;) {
				errorStr.append(res + "\n");
			}
			eReader.close();
			errorReader.close();

			process.getOutputStream().close(); // 不要忘记了一定要关

			long end = System.currentTimeMillis();

			log.error("git OK:" + (end - start) + "ms\nsuccess:" + resStr.toString() + "\nerror:" + errorStr);
		} catch (IOException ee) {
			log.error(ee.getMessage() + "\n");
		}
	}

	private void commitAndPushGitForUzzzOrg() {
		try {
			long start = System.currentTimeMillis();

			File dir = new File(uzzzOrgPath);
			String[] cmd = new String[] { "/bin/sh", "-c",
					"/usr/local/rvm/gems/ruby-2.4.5/bin/jekyll build --destination docs --incremental && git add . && git commit -a -m \"deploy\" && git pull && git push origin master" };
			Process process = Runtime.getRuntime().exec(cmd, null, dir);

			// 记录dos命令的返回信息
			StringBuffer resStr = new StringBuffer();
			InputStream in = process.getInputStream();
			Reader reader = new InputStreamReader(in);
			BufferedReader bReader = new BufferedReader(reader);
			for (String res = ""; (res = bReader.readLine()) != null;) {
				resStr.append(res + "\n");
			}
			bReader.close();
			reader.close();

			// 记录dos命令的返回错误信息
			StringBuffer errorStr = new StringBuffer();
			InputStream errorIn = process.getErrorStream();
			Reader errorReader = new InputStreamReader(errorIn);
			BufferedReader eReader = new BufferedReader(errorReader);
			for (String res = ""; (res = eReader.readLine()) != null;) {
				errorStr.append(res + "\n");
			}
			eReader.close();
			errorReader.close();

			process.getOutputStream().close(); // 不要忘记了一定要关

			long end = System.currentTimeMillis();

			log.error("git OK:" + (end - start) + "ms\nsuccess:" + resStr.toString() + "\nerror:" + errorStr);
		} catch (IOException ee) {
			log.error(ee.getMessage());
		}
	}
}
