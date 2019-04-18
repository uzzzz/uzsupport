package org.uzzz.jobs.semblance;

import java.io.File;
import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.db.DBConfiguration;
import org.apache.hadoop.mapreduce.lib.db.DBInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.uzzz.SimHash;
import org.uzzz.jobs.BaseJob;

@Component
public class SemblanceJob extends BaseJob {

	@Value("${hadoop.semblance.path}")
	private String semblanceOutputPath;

	public boolean semblance() throws Exception {

		Configuration conf = new Configuration();
		// 删除旧的历史文件
		FileSystem fs = FileSystem.get(conf);
		Path f = new Path(semblanceOutputPath);
		if (fs.exists(f)) {
			fs.delete(f, true);
		}

		DBConfiguration.configureDB(conf, dbdriver, dburl, dbuser, dbpass);
		Job job = Job.getInstance(conf, "uzshare.semblance");

		job.setJarByClass(SemblanceMapreduce.class);

		job.setMapperClass(SemblanceMapreduce.SemblanceMapper.class);
		job.setMapOutputKeyClass(LongWritable.class);
		job.setMapOutputValueClass(SemblanceRecord.class);

		job.setReducerClass(SemblanceMapreduce.SemblanceReducer.class);
		job.setOutputKeyClass(LongWritable.class);
		job.setOutputValueClass(SemblanceRecord.class);

		job.setInputFormatClass(DBInputFormat.class);
		String sql = "select mto_posts.id, mto_posts.title, mto_posts_attribute.content" //
				+ "	from mto_posts" //
				+ " left join mto_posts_attribute" //
				+ "	on mto_posts.id = mto_posts_attribute.id";
		DBInputFormat.setInput(job, SemblanceRecord.class, sql, "select count(1) from mto_posts");

		job.setOutputFormatClass(SequenceFileOutputFormat.class);
		FileOutputFormat.setOutputPath(job, new Path(semblanceOutputPath));

		return job.waitForCompletion(true);
	}

	public void writePost(long id, String title, String c) {
		SequenceFile.Writer writer = null;
		try {
			Configuration conf = new Configuration();
			FileSystem fs = FileSystem.get(conf);
			Path path = new Path(semblanceOutputPath + id);
			writer = new SequenceFile.Writer(fs, conf, path, LongWritable.class, SemblanceRecord.class);
			LongWritable key = new LongWritable(id);
			SemblanceRecord value = new SemblanceRecord();
			value.setId(id);
			value.setTitle(title);
			value.setContent(c);
			writer.append(key, value);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				writer.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public boolean similar(String title, String content) {
		File file = new File(semblanceOutputPath);
		return similarSemblanceRecord(title, content, file);
	}

	private boolean similarSemblanceRecord(String title, String content, File file) {

		if (file == null) {
			return false;
		}

		if (file.isFile()) {
			return similarSemblanceRecord(title, content, new Path(file.getAbsolutePath()));
		} else if (file.isDirectory()) {
			File[] children = file.listFiles();
			for (File child : children) {
				boolean b = similarSemblanceRecord(title, content, child);
				if (b) {
					return b;
				}
			}
		}
		return false;
	}

	private boolean similarSemblanceRecord(String title, String content, Path path) {
		SequenceFile.Reader reader = null;
		try {
			Configuration conf = new Configuration();
			FileSystem fs = FileSystem.get(conf);
			reader = new SequenceFile.Reader(fs, path, conf);
			LongWritable key = new LongWritable();
			SemblanceRecord value = new SemblanceRecord();
			while (reader.next(key, value)) {
				if (compare(title, content, value)) {
					return true;
				}
			}
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				reader.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	// true:相似；false:不相似
	private boolean compare(String title, String content, SemblanceRecord sr) {

//		long a = System.currentTimeMillis();

		SimHash title1hash = new SimHash(title, 64);
		SimHash title2hash = new SimHash(sr.getTitle(), 64);
//		SimHash content1hash = new SimHash(content, 64);
//		SimHash content2hash = new SimHash(sr.getContent(), 64);
		int titleHamming = title1hash.hammingDistance(title2hash);
//		double titleSemblance = title1hash.getSemblance(title2hash);
//		int contentHamming = content1hash.hammingDistance(content2hash);
//		double contentSemblance = content1hash.getSemblance(content2hash);

//		long b = System.currentTimeMillis();
//		StringBuffer sb = new StringBuffer("\n");
//		sb.append("耗时:").append(b - a).append("ms\n") //
//				.append("id1:").append(title).append("\n") //
//				.append("id2:").append(sr.getTitle()).append("\n") //
//				.append("标题-海明距离是:").append(titleHamming).append("\n") //
//				.append("标题-文本相似度:").append(titleSemblance).append("\n")//
//				.append("内容-海明距离是:").append(contentHamming).append("\n") //
//				.append("内容-文本相似度:").append(contentSemblance).append("\n");
//		System.out.println(sb.toString());

		return titleHamming == 0; // || contentHamming == 0;
	}
}
