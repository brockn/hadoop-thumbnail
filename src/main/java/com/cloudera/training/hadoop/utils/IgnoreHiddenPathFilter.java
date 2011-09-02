package com.cloudera.training.hadoop.utils;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.PathFilter;

public class IgnoreHiddenPathFilter implements PathFilter {
  public boolean accept(Path p){
    String name = p.getName();
    return !name.startsWith("_") && !name.startsWith(".");
  }

}
