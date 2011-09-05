package com.cloudera.training.hadoop.addone;

public class NativeFunctions {

  public static native int addOne(int i);


  static {
    String lib = "AddOneNativeFunctions-";
    String os = System.getProperty("os.name");
    os = os.trim().toLowerCase();
    if(os.equals("linux")) {
      lib += "Linux-";
    } else {
      throw new RuntimeException("Unknown os " + os);
    }
    String bits = System.getProperty("sun.arch.data.model");
    bits = bits.trim().toLowerCase();
    if(bits.equals("32")) {
      lib += "i686";
    } else {
      lib += "x86_64";
    }
    System.loadLibrary(lib);
  }
}

