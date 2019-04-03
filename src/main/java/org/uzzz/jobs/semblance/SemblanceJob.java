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

	public SemblanceRecord similar(long id) {
		File file = new File(semblanceOutputPath);
		return readSemblanceRecord(id, file);
	}

	private SemblanceRecord readSemblanceRecord(long id, File file) {

		if (file == null) {
			return null;
		}

		if (file.isFile()) {
			return readSemblanceRecord(id, new Path(file.getAbsolutePath()));
		} else if (file.isDirectory()) {
			File[] children = file.listFiles();
			for (File child : children) {
				SemblanceRecord sr = readSemblanceRecord(id, child);
				if (sr != null) {
					return sr;
				}
			}
		}
		return null;
	}

	private SemblanceRecord readSemblanceRecord(long id, Path path) {
		SequenceFile.Reader reader = null;
		try {
			Configuration conf = new Configuration();
			FileSystem fs = FileSystem.get(conf);
			reader = new SequenceFile.Reader(fs, path, conf);
			LongWritable key = new LongWritable();
			SemblanceRecord value = new SemblanceRecord();
			while (reader.next(key, value)) {
				if (key.get() == id) {
					return value;
				}
			}
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} finally {
			try {
				reader.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
