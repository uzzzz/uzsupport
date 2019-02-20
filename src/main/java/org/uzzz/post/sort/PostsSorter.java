package org.uzzz.post.sort;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;

public class PostsSorter {

	public static class PostsMapper extends Mapper<LongWritable, PostRecord, LongWritable, PostRecord> {
		@Override
		protected void map(LongWritable key, PostRecord value, Context context)
				throws IOException, InterruptedException {
			context.write(new LongWritable(value.getId()), value);
		}
	}

	public static class PostsReducer extends Reducer<LongWritable, PostRecord, PostRecord, NullWritable> {
		@Override
		protected void reduce(LongWritable key, Iterable<PostRecord> values, Context context)
				throws IOException, InterruptedException {
			for (PostRecord v : values) {
				context.write(v, NullWritable.get());
			}
		}
	}
}
