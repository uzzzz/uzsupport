package org.uzzz.jobs.sort;

import java.io.IOException;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;

public class SortMapreduce {

	public static class PostsMapper extends Mapper<LongWritable, SortRecord, DoubleWritable, SortRecord> {
		@Override
		protected void map(LongWritable key, SortRecord value, Context context)
				throws IOException, InterruptedException {
			context.write(new DoubleWritable(value.score), value);
		}
	}

	public static class PostsReducer extends Reducer<DoubleWritable, SortRecord, SortRecord, NullWritable> {
		@Override
		protected void reduce(DoubleWritable key, Iterable<SortRecord> values, Context context)
				throws IOException, InterruptedException {
			for (SortRecord v : values) {
				context.write(v, NullWritable.get());
			}
		}
	}
}
