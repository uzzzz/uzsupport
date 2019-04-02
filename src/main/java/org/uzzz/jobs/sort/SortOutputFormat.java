package org.uzzz.jobs.sort;

import java.io.IOException;

import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.RecordWriter;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.springframework.data.redis.core.ListOperations;
import org.uzzz.RedisService;
import org.uzzz.SupportApp;

public class SortOutputFormat extends FileOutputFormat<SortRecord, NullWritable> {

	@Override
	public RecordWriter<SortRecord, NullWritable> getRecordWriter(TaskAttemptContext job)
			throws IOException, InterruptedException {
		return new RedisRecordWriter(SupportApp.redisService());
	}

	protected static class RedisRecordWriter extends RecordWriter<SortRecord, NullWritable> {

		private RedisService<SortRecord> redisService;

		public RedisRecordWriter(RedisService<SortRecord> redisService) {
			this.redisService = redisService;
		}

		@Override
		public void write(SortRecord key, NullWritable value) throws IOException, InterruptedException {
			ListOperations<String, SortRecord> ops = (ListOperations<String, SortRecord>) redisService.opsForList();
			ops.rightPush("sorted_posts", key);
		}

		@Override
		public void close(TaskAttemptContext context) throws IOException, InterruptedException {
		}
	}
}