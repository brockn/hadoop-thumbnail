#!/bin/bash
set -e
test -d target/run && rm -rf target/run
mkdir target/run
mvn package
cp target/hadoop-thumbnail-1.0-SNAPSHOT.jar target/run
cp src/main/c/* target/run
cp -R sample-images target/run/large
cd target/run
./build.sh

hadoop fs -rmr img/ 2>/dev/null || true
hadoop fs -mkdir img/
test -d thumbnails && rm -rf thumbnails
mkdir thumbnails

hadoop jar hadoop-thumbnail-*.jar com.cloudera.training.hadoop.io.CopyDirToSequenceFile \
  -input large -output img/input

hadoop jar hadoop-thumbnail-*.jar com.cloudera.training.hadoop.image.ThumbnailMapReduce \
  -files libNativeFunctions-$(uname -s)-$(uname -m).so \
  -input img/input \
  -output img/output \
  -delete

hadoop jar hadoop-thumbnail-*.jar com.cloudera.training.hadoop.io.CopyDirFromSequenceFile \
  -input img/output \
  -output thumbnails

echo "Success! Thumbnails should be in target/run/thumbnails/"
