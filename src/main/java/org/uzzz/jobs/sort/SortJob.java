package org.uzzz.jobs.sort;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.db.DBConfiguration;
import org.apache.hadoop.mapreduce.lib.db.DBInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.uzzz.jobs.BaseJob;
import org.uzzz.jobs.sort.PostsSorter.PostsMapper;
import org.uzzz.jobs.sort.PostsSorter.PostsReducer;

@Component
public class SortJob extends BaseJob {

	@Value("${hadoop.sort.path}")
	private String sortOutputPath;

	public boolean sort() throws Exception {

		Configuration conf = new Configuration();
		// 删除旧的历史文件
		FileSystem fs = FileSystem.get(conf);
		Path f = new Path(sortOutputPath);
		if (fs.exists(f)) {
			fs.delete(f, true);
		}

		DBConfiguration.configureDB(conf, dbdriver, dburl, dbuser, dbpass);
		Job job = Job.getInstance(conf, "uzblog.sort");

		job.setJarByClass(PostsSorter.class);

		job.setMapperClass(PostsMapper.class);
		job.setMapOutputKeyClass(DoubleWritable.class);
		job.setMapOutputValueClass(PostRecord.class);

		job.setSortComparatorClass(PostRecord.ScoreComparator.class);

		job.setReducerClass(PostsReducer.class);
		job.setOutputKeyClass(PostRecord.class);
		job.setOutputValueClass(NullWritable.class);

		job.setInputFormatClass(DBInputFormat.class);
		String[] input_fields = { "id", "views", "favors", "comments", "created" };
		DBInputFormat.setInput(job, PostRecord.class, "mto_posts", null, "id", input_fields);

		job.setOutputFormatClass(SortedOutputFormat.class);
		FileOutputFormat.setOutputPath(job, new Path(sortOutputPath));

		return job.waitForCompletion(true);
	}

}
