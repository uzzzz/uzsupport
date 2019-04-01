package org.uzzz.jobs.sort;

import java.io.IOException;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;

public class PostsSorter {

	public static class PostsMapper extends Mapper<LongWritable, PostRecord, DoubleWritable, PostRecord> {
		@Override
		protected void map(LongWritable key, PostRecord value, Context context)
				throws IOException, InterruptedException {
			context.write(new DoubleWritable(value.score), value);
		}
	}

	public static class PostsReducer extends Reducer<DoubleWritable, PostRecord, PostRecord, NullWritable> {
		@Override
		protected void reduce(DoubleWritable key, Iterable<PostRecord> values, Context context)
				throws IOException, InterruptedException {
			for (PostRecord v : values) {
				context.write(v, NullWritable.get());
			}
		}
	}
}
