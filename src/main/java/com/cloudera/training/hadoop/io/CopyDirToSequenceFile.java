package com.cloudera.training.hadoop.io;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.util.ToolRunner;

import com.cloudera.training.hadoop.utils.InputOutputDriver;


public class CopyDirToSequenceFile extends InputOutputDriver {
  public static final Log LOG = LogFactory.getLog(CopyDirToSequenceFile.class);
  public static void main(String[] args) throws Exception {
    int rc = ToolRunner.run(new CopyDirToSequenceFile(), args);
    System.exit(rc);

  }

  @Override
  public int process(String[] args) throws Exception {
    Configuration conf = getConf();
    FileSystem fs = FileSystem.get(conf);
    SequenceFile.Writer writer = null;
    try {
      Text key = null;
      BytesWritable value = null;
      File inputDir = new File(input);
      File[] inputFiles = inputDir.listFiles();
      if(inputFiles != null && inputFiles.length > 0) {
        writer = SequenceFile.createWriter(fs, conf,
            new Path(output), Text.class, BytesWritable.class);
        ByteArrayOutputStream out = new ByteArrayOutputStream(4096);
        for (int i = 0; i < inputFiles.length; i++) {
          out.reset();
          FileInputStream in = new FileInputStream(inputFiles[i]);
          try {
            IOUtils.copyBytes(in, out, conf, false);
          } finally {
            IOUtils.cleanup(LOG, in, out);
          }
          String fileName = inputFiles[i].getName();
          key = new Text(fileName);
          value = new BytesWritable(out.toByteArray());
          writer.append(key, value);
        }
      }
    } finally {
      IOUtils.cleanup(LOG, writer);
    }
    return 0;
  }
}
