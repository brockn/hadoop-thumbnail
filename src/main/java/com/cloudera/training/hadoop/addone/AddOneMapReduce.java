package com.cloudera.training.hadoop.addone;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class AddOneMapReduce extends Configured implements Tool {
  
  protected static final String NAME = AddOneMapReduce.class.getSimpleName();

  public static class InnerMapper extends MapReduceBase implements
  Mapper<LongWritable, Text, IntWritable, NullWritable> {
    IntWritable outputValue = new IntWritable();
    NullWritable NULL = NullWritable.get();
    public void map(LongWritable key, Text value,
        OutputCollector<IntWritable, NullWritable> output,
        Reporter reporter) throws IOException {
      try {
        int i = Integer.parseInt(value.toString());
        outputValue.set(NativeFunctions.addOne(i));
        output.collect(outputValue, NULL);
      } catch (NumberFormatException e) {
        reporter.getCounter("AddOne", "BadInput").increment(1);
      }
    }
  }

  public static void main(String[] args) throws Exception {
    int rc = ToolRunner.run(new AddOneMapReduce(), args);
    System.exit(rc);
  }

  @Override
  public int run(String[] args) throws Exception {
    if(args.length != 2) {
      throw new Exception(NAME + " input output");
    }
    String input = args[0];
    String output = args[1];
    Configuration conf = getConf();
    JobConf job = new JobConf(conf);
    job.setJarByClass(AddOneMapReduce.class);
    job.setMapperClass(InnerMapper.class);
    job.setOutputKeyClass(IntWritable.class);
    job.setOutputValueClass(NullWritable.class);
    job.setNumReduceTasks(0);
    FileInputFormat.addInputPath(job, new Path(input));
    FileOutputFormat.setOutputPath(job, new Path(output));
    JobClient.runJob(job);
    return 0;
  }
}
