package org.uzzz.post.sort;

import java.io.IOException;

import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.RecordWriter;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.springframework.data.redis.core.ListOperations;
import org.uzzz.RedisService;
import org.uzzz.SupportApp;

public class SortedOutputFormat extends FileOutputFormat<PostRecord, NullWritable> {

	@Override
	public RecordWriter<PostRecord, NullWritable> getRecordWriter(TaskAttemptContext job)
			throws IOException, InterruptedException {
		return new RedisRecordWriter(SupportApp.redisService());
	}

	protected static class RedisRecordWriter extends RecordWriter<PostRecord, NullWritable> {

		private RedisService<PostRecord> redisService;

		public RedisRecordWriter(RedisService<PostRecord> redisService) {
			this.redisService = redisService;
		}

		@Override
		public void write(PostRecord key, NullWritable value) throws IOException, InterruptedException {
			ListOperations<String, PostRecord> ops = (ListOperations<String, PostRecord>) redisService.opsForList();
			ops.rightPush("sorted_posts", key);
		}

		@Override
		public void close(TaskAttemptContext context) throws IOException, InterruptedException {
		}
	}
}