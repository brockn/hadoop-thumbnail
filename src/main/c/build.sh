#!/bin/bash
SYSTEM=$(uname -s)
LIB="libImageNativeFunctions-${SYSTEM}-$(uname -m)"
CFLAGS="$(Wand-config --cflags) -I$JAVA_HOME/include"
LDFLAGS="-L/opt/local/lib"
if [[ $SYSTEM == "Linux" ]]
then
  CFLAGS="$CFLAGS -I$JAVA_HOME/include/linux"
  LDFLAGS="$LDFLAGS -lWand -lMagick"
  LIB="${LIB}.so"
elif [[ $SYSTEM == "Darwin" ]]
then
  LDFLAGS="$LDFLAGS -lMagickWand -lMagickCore -dynamiclib -framework JavaVM"
  LIB="${LIB}.jnilib"
else
  echo "Unknown system, hacking required" 1>&2
  exit 1
fi
SRC="com_cloudera_training_hadoop_image_NativeFunctions.c"
set -x
gcc $SRC -Wall -fPIC $CFLAGS -shared $LDFLAGS -o $LIB
