package com.cloudera.training.hadoop.image;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapred.SequenceFileInputFormat;
import org.apache.hadoop.mapred.SequenceFileOutputFormat;
import org.apache.hadoop.util.ToolRunner;

import com.cloudera.training.hadoop.utils.InputOutputDriver;

public class ThumbnailMapReduce extends InputOutputDriver {
  protected static final String NAME = ThumbnailMapReduce.class.getSimpleName();

  public static class InnerMapper extends MapReduceBase implements
  Mapper<Text, BytesWritable, Text, BytesWritable> {
    public void map(Text key, BytesWritable value,
        OutputCollector<Text, BytesWritable> output,
        Reporter reporter) throws IOException {
      byte[] result = NativeFunctions.thumbnailMapper(value.getBytes(), 0, value.getLength());
      if (null != result) {
        // reusing writable results in more expensive copy
        output.collect(key, new BytesWritable(result));
      } else {
        reporter.getCounter(NAME, "InvalidOutput").increment(1);
      }
    }
  }

  public static void main(String[] args) throws Exception {
    int rc = ToolRunner.run(new ThumbnailMapReduce(), args);
    System.exit(rc);
  }

  @Override
  public int process(String[] args) throws Exception {
    Configuration conf = getConf();
    JobConf job = new JobConf(conf);
    job.setJarByClass(ThumbnailMapReduce.class);
    job.setMapperClass(InnerMapper.class);
    job.setOutputKeyClass(Text.class);
    job.setOutputValueClass(BytesWritable.class);
    job.setNumReduceTasks(0);
    job.setInputFormat(SequenceFileInputFormat.class);
    job.setOutputFormat(SequenceFileOutputFormat.class);
    FileInputFormat.addInputPath(job, new Path(input));
    FileOutputFormat.setOutputPath(job, new Path(output));
    JobClient.runJob(job);
    return 0;
  }
}
