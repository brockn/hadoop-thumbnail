#include <jni.h>

#include "com_cloudera_training_hadoop_addone_NativeFunctions.h"

JNIEXPORT jint JNICALL Java_com_cloudera_training_hadoop_addone_NativeFunctions_addOne
  (JNIEnv *env, jclass class, jint i) {
    return i + 1;
}


