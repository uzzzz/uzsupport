package org.uzzz.jobs.duplicate;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.db.DBConfiguration;
import org.apache.hadoop.mapreduce.lib.db.DBInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.uzzz.jobs.BaseJob;

@Component
public class DuplicateJob extends BaseJob {

	@Value("${hadoop.duplicate.path}")
	private String duplicateOutputPath;

	public boolean duplicate() throws Exception {

		Configuration conf = new Configuration();
		// 删除旧的历史文件
		FileSystem fs = FileSystem.get(conf);
		Path f = new Path(duplicateOutputPath);
		if (fs.exists(f)) {
			fs.delete(f, true);
		}

		DBConfiguration.configureDB(conf, dbdriver, dburl, dbuser, dbpass);
		Job job = Job.getInstance(conf, "uzblog.duplicate");

		job.setJarByClass(DuplicateMapreduce.class);

		job.setMapperClass(DuplicateMapreduce.DuplicateMapper.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(LongWritable.class);

		job.setReducerClass(DuplicateMapreduce.DuplicateReducer.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);

		job.setInputFormatClass(DBInputFormat.class);
		String[] input_fields = { "id", "title" };
		DBInputFormat.setInput(job, DuplicateRecord.class, "mto_posts", null, "id", input_fields);

		FileOutputFormat.setOutputPath(job, new Path(duplicateOutputPath));

		return job.waitForCompletion(true);
	}
}
