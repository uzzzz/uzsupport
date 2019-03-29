package org.uzzz.post.duplicate;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;

public class DuplicateMapreduce {

	public static class DuplicateMapper extends Mapper<LongWritable, DuplicateRecord, Text, LongWritable> {
		@Override
		protected void map(LongWritable key, DuplicateRecord value, Context context)
				throws IOException, InterruptedException {
			context.write(new Text(value.title), new LongWritable(value.id));
		}
	}

	public static class DuplicateReducer extends Reducer<Text, LongWritable, Text, Text> {
		@Override
		protected void reduce(Text key, Iterable<LongWritable> values, Context context)
				throws IOException, InterruptedException {
			StringBuffer sb = new StringBuffer();
			int size = 0;
			for (LongWritable id : values) {
				size++;
				sb.append(id.toString()).append(",");
			}
			sb.deleteCharAt(sb.length() - 1);
			if (size > 10) {
				context.write(key, new Text(sb.toString()));
			}
		}
	}
}
