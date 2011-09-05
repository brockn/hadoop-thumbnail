package com.cloudera.training.hadoop.image;


public class NativeFunctions {

  public static byte[] thumbnailMapper(byte[] image, int start, int count) {
    return thumbnailMapper0(image, start, count);
  }
  protected static native byte[] thumbnailMapper0(byte[] image, int start, int count);

  
  static {
    String lib = "NativeFunctions-";
    String os = System.getProperty("os.name");
    if(os == null) {
      throw new NullPointerException("System property os.name");
    }
    os = os.trim().toLowerCase();
    String bits = System.getProperty("sun.arch.data.model");
    if(bits == null) {
      throw new NullPointerException("System property sun.arch.data.model");
    }
    bits = bits.trim().toLowerCase();
    if(os.equals("linux")) {
      lib += "Linux-";
      if(bits.equals("32")) {
        lib += "i686";
      } else {
        lib += "x86_64";
      }
    } else if(os.equals("mac os x")) {
      lib += "Darwin-i386";
    } else {
      throw new RuntimeException("Unknown os " + os);
    }
    System.loadLibrary(lib);
  }

  public static void main(String[] args) {

    for(Object key : System.getProperties().keySet()) {
      String s = key.toString() + " => " + System.getProperty(key.toString());
      System.out.println(s);
    }
  }
}

