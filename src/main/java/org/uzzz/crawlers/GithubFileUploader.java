package org.uzzz.crawlers;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.uzzz.bean.Referer;
import org.uzzz.dao.slave.RefererSlaveDao;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;

@Component
public class GithubFileUploader {

    @Autowired
    private RefererSlaveDao refererDao;

    @Autowired
    private RestTemplate rest;

    public String upload(String imageOriginUrl) throws IOException {

        InputStream in = null;
        try {
            URL u = new URL(imageOriginUrl);

            String protocol = u.getProtocol();
            String host = u.getHost();
            String ref = null;
            Referer referer = refererDao.findByHost(host);
            if (referer != null && StringUtils.hasLength(referer.getReferer())) {
                ref = protocol + "://" + referer.getReferer();
            } else {
                ref = protocol + "://" + host;
            }
            HttpURLConnection conn = (HttpURLConnection) u.openConnection();
            conn.setRequestProperty("User-agent","Mozilla/4.0");
            conn.setRequestProperty("referer", ref);
            in = conn.getInputStream();
            byte[] bytes = IOUtils.toByteArray(in);
            String content =  Base64.getEncoder().encodeToString(bytes);
            String filename = DigestUtils.md5DigestAsHex(imageOriginUrl.getBytes()) + System.currentTimeMillis() + ".png";
            String path = host + "/" + filename;
            String token = "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";
            // 用户名、库名、路径
            String url = "https://api.github.com/repos/xxcode/img.ibz.bz/contents/docs/" + path ;
            GithubFileObject githubFileObject = createGithubFileObject(imageOriginUrl, content, "GithubFileUploader", "GithubFileUploader@local.mbp");
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "token " + token);
            HttpEntity<GithubFileObject> entity = new HttpEntity<>(githubFileObject, headers);
            ResponseEntity<String> responseEntity = rest.exchange(url, HttpMethod.PUT, entity, String.class);
            String resp = responseEntity.getBody();
            System.out.println(resp);
            return "https://img.ibz.bz/" + path;
        } catch (IOException e) {
            throw e;
        } finally {
            if(in !=null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private GithubFileObject createGithubFileObject(String message, String content, String name, String email) {
        GithubFileObject object = new GithubFileObject();
        object.message = message;
        object.content = content;
        Committer committer = new Committer();
        committer.name = name;
        committer.email = email;
        object.committer = committer;
        return object;
    }

    class GithubFileObject implements Serializable {
        public String message;
        public String content;
        public Committer committer;
    }

    class Committer implements Serializable{
        public String name;
        public String email;
    }
}