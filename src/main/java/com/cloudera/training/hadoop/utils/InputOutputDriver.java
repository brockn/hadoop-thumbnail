package com.cloudera.training.hadoop.utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.util.Tool;

public abstract class InputOutputDriver extends Configured implements Tool {
  protected String input;
  protected String output;
  protected abstract int process(String[] args) throws Exception;
  public int run(String[] args) throws Exception {
    List<String> remaining = new ArrayList<String>();
    boolean deleteOutput = false;
    for (int i = 0; i < args.length; i++) {
      boolean last = i + 1 == args.length;
      String compareArg = args[i].trim().toLowerCase();
      if(compareArg.equals("-input")) {
        if(last) {
          throw new IllegalArgumentException("Argument " + args[i] + " requires value");
        }
        input = args[++i];
      } else if (compareArg.equals("-output")) {
        if(last) {
          throw new IllegalArgumentException("Argument " + args[i] + " requires value");
        }
        output = args[++i];
      } else if (compareArg.equals("-delete")) {
        deleteOutput = true;
      } else {
        remaining.add(args[i]);
      }
    }
    if(input == null) {
      throw new IllegalArgumentException("-input required");
    }
    if(output == null) {
      throw new IllegalArgumentException("-output required");
    }
    if(deleteOutput) {
      Configuration conf = getConf();
      FileSystem fs = FileSystem.get(conf);
      Path outputPath = new Path(output);
      if(fs.exists(outputPath)) {
        fs.delete(outputPath, true);
      }
    }
    return process(remaining.toArray(new String[]{}));
  }
  public String getInput() {
    return input;
  }
  public void setInput(String input) {
    this.input = input;
  }
  public String getOutput() {
    return output;
  }
  public void setOutput(String output) {
    this.output = output;
  }
}
