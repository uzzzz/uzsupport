package org.uzzz.post.sort;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.db.DBConfiguration;
import org.apache.hadoop.mapreduce.lib.db.DBInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.uzzz.post.sort.PostsSorter.PostsMapper;
import org.uzzz.post.sort.PostsSorter.PostsReducer;

public class Jobs {

	private static String sorterOutputPath = "/tmp/hadoop/output/sorter.out/";

	private static Job sort() throws Exception {
		Configuration conf = new Configuration();

		// 删除旧的历史文件
		FileSystem fs = FileSystem.get(conf);
		Path f = new Path(sorterOutputPath);
		if (fs.exists(f)) {
			fs.delete(f, true);
		}

		DBConfiguration.configureDB(conf, "com.mysql.jdbc.Driver",
				"jdbc:mysql://127.0.0.1:3306/uzblog?useSSL=false&characterEncoding=utf8", "root", "Abc1234567!");

		Job job = Job.getInstance(conf, "uzblog.sort");

		job.setJarByClass(PostsSorter.class);

		job.setMapperClass(PostsMapper.class);
		job.setMapOutputKeyClass(DoubleWritable.class);
		job.setMapOutputValueClass(PostRecord.class);

		job.setSortComparatorClass(SortComparator.class);

		job.setReducerClass(PostsReducer.class);
		job.setOutputKeyClass(PostRecord.class);
		job.setOutputValueClass(NullWritable.class);

		job.setInputFormatClass(DBInputFormat.class);
		String[] input_fields = { "id", "views", "favors", "comments", "created" };
		DBInputFormat.setInput(job, PostRecord.class, "mto_posts", null, "id", input_fields);

		job.setOutputFormatClass(SortedOutputFormat.class);
		FileOutputFormat.setOutputPath(job, new Path(sorterOutputPath));
		return job;
	}

	public static boolean start() throws Exception {
		return sort().waitForCompletion(true);
	}
}
