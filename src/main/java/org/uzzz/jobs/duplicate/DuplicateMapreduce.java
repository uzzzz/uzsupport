package org.uzzz.jobs.duplicate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.io.IntWritable;
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

	public static class DuplicateReducer extends Reducer<Text, LongWritable, Text, IntWritable> {
		@Override
		protected void reduce(Text key, Iterable<LongWritable> values, Context context)
				throws IOException, InterruptedException {
			List<Long> list = new ArrayList<Long>();
			int size = 0;
			for (LongWritable id : values) {
				if (size > 0) {
					list.add(id.get());
				}
				size++;
			}
			if (list.size() > 0) {
				context.write(key, new IntWritable(list.size()));
//				try {
//					SupportApp.context.getBean(AsyncTask.class).deletePost(list);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
			}
		}
	}
}
