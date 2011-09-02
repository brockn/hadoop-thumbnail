package com.cloudera.training.hadoop.io;

import java.io.File;
import java.io.FileOutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.util.ToolRunner;

import com.cloudera.training.hadoop.utils.IgnoreHiddenPathFilter;
import com.cloudera.training.hadoop.utils.InputOutputDriver;
import  org.apache.hadoop.io.IOUtils;

public class CopyDirFromSequenceFile extends InputOutputDriver {
  public static final Log LOG = LogFactory.getLog(CopyDirFromSequenceFile.class);
  public static void main(String[] args) throws Exception {
    int rc = ToolRunner.run(new CopyDirFromSequenceFile(), args);
    System.exit(rc);

  }

  @Override
  public int process(String[] args) throws Exception {
    Configuration conf = getConf();
    FileSystem fs = FileSystem.get(conf);
    FileStatus[] outputFiles = fs.globStatus(new Path(input, "*"), new IgnoreHiddenPathFilter());
    if(outputFiles != null && outputFiles.length > 0) {
      for (int i = 0; i < outputFiles.length; i++) {
        SequenceFile.Reader reader = new SequenceFile.Reader(fs, outputFiles[i].getPath(), conf);
        try {
          Text key = new Text();
          BytesWritable value = new BytesWritable();
          while (reader.next(key, value)) {
            String fileName = key.toString();
            File outFile = new File(output + File.separator + fileName);
            FileOutputStream out = new FileOutputStream(outFile);
            try {
              out.write(value.getBytes(), 0 , value.getLength());
            } finally {
              IOUtils.cleanup(LOG, out);
            }
            key = new Text();
            value = new BytesWritable();
          }
        } finally {
          IOUtils.cleanup(LOG, reader);
        }
      }
    }
    return 0;
  }
}
