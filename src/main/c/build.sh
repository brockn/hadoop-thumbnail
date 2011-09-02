#!/bin/bash
LIB="libNativeFunctions-$(uname -s)-$(uname -m).so"
CFLAGS="-I$JAVA_HOME/include"
if [[ $(uname -s) == "Linux" ]]
then
    CFLAGS="$CFLAGS -I$JAVA_HOME/include/linux"
fi
SRC="com_cloudera_training_hadoop_image_NativeFunctions.c"
bash -x -c "gcc $SRC -Wall -fPIC -shared -Wl,-soname,$LIB -lWand -lMagick -o $LIB" || exit $?