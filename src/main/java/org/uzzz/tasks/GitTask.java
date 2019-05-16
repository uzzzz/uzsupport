package org.uzzz.tasks;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class GitTask {

	private static Logger log = LoggerFactory.getLogger(GitTask.class);

	@Value("${git.paths}")
	private String[] gitPaths;

	public void writeGit(long id, String title, String c, String time) {
		try {
			c = URLEncoder.encode(c, "UTF-8");
			c = "{{ \"" + c + "\" | url_decode}}";
		} catch (UnsupportedEncodingException ee) {
			c = "{% raw %} \n" + c + "\n{% endraw %}";
		}
		String content = "---\n" //
				+ "layout: default\n" //
				+ "title: \"" + title.replace("\\", "\\\\").replace("\"", "\\\"") + "\"\n" //
				+ "---\n\n" //
				+ c;

		writeGitForUzzzOrg(id, title, content, time);
	}

	private void writeGitForUzzzOrg(long id, String title, String content, String time) {

		for (String _path : gitPaths) {
			try {
				String path = _path + "/_posts/";
				BufferedWriter writer = new BufferedWriter(
						new OutputStreamWriter(new FileOutputStream(path + time + "-" + id + ".html", false), "UTF-8"));
				writer.write(content);
				writer.close();
			} catch (Exception e) {
				e.printStackTrace();
				log.error(e.getMessage(), e);
			}
		}

	}

}
