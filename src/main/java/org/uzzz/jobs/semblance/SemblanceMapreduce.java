package org.uzzz.jobs.semblance;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;

public class SemblanceMapreduce {

	public static class SemblanceMapper extends Mapper<LongWritable, SemblanceRecord, LongWritable, SemblanceRecord> {
		@Override
		protected void map(LongWritable key, SemblanceRecord value, Context context)
				throws IOException, InterruptedException {
			context.write(new LongWritable(value.id), value);
		}
	}

	public static class SemblanceReducer extends Reducer<LongWritable, SemblanceRecord, LongWritable, SemblanceRecord> {
		@Override
		protected void reduce(LongWritable key, Iterable<SemblanceRecord> values, Context context)
				throws IOException, InterruptedException {
			for (SemblanceRecord sr : values) {
				context.write(new LongWritable(sr.getId()), sr);
			}
		}
	}
}
