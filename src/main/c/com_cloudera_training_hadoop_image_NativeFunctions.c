#include <jni.h>
#include <string.h>
#include <sys/types.h>
#include <sys/ipc.h>
#include <sys/shm.h>
#include <sys/mman.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <unistd.h>
#include <wand/MagickWand.h>


#include "com_cloudera_training_hadoop_image_NativeFunctions.h"


typedef struct {
	char* source;
	int sourceStart;
	int sourceLength;
	char* destination;
	int destinationLength;
	MagickWand* magickWand;
} image_t;

void thumbnailMapper(image_t* image) {
  image->destinationLength = -1;	
  MagickBooleanType status;
  MagickWand* magick_wand;
  /*
  Read an image.
  */
  MagickWandGenesis();
  magick_wand = NewMagickWand();
  image->magickWand = magick_wand;
  status = MagickReadImageBlob(magick_wand, image->source + image->sourceStart, image->sourceLength);
  if (status == MagickFalse) {
		char *description;
		ExceptionType severity;
		description = MagickGetException(magick_wand, &severity);
		fprintf(stderr,"MagickReadImageBlob returned false: %s %s %lu %s\n",GetMagickModule(),description);
		MagickRelinquishMemory(description);
    description = NULL;
	} else {		
		/*
		 Turn the images into a thumbnail sequence.
		 */
		MagickResetIterator(magick_wand);
		while (MagickNextImage(magick_wand) != MagickFalse)
			MagickResizeImage(magick_wand, 106, 80, LanczosFilter, 1.0);
		/*
		 Write the image then destroy it.
		 */
		MagickSetImageFormat(magick_wand, "JPEG");
		size_t size;
		image->destination = (char*)MagickGetImageBlob(magick_wand, &size);
		if(image->destination == NULL) {
			fprintf(stderr,"MagickGetImageBlob returned null - maybe OOM\n");
		} else {
			image->destinationLength = (int)size;
		}
	}
}

void cleanup(image_t* image) {
    if(image->destination != NULL && image->destinationLength > 0) {
        MagickRelinquishMemory(image->destination);
        image->destinationLength = -1;
    }
    if(image->magickWand != NULL) {
        DestroyMagickWand(image->magickWand);
        image->magickWand = NULL;
    }
}
void cleanupJava(JNIEnv* env, image_t* image, jbyteArray* value, jboolean isCopy) {
    if(image->source != NULL && isCopy) {
        (*env)->ReleaseByteArrayElements(env, *value, (jbyte*)image->source, JNI_ABORT);
        image->source = NULL;
    }
    cleanup(image);
}

JNIEXPORT jbyteArray JNICALL Java_com_cloudera_training_hadoop_image_NativeFunctions_thumbnailMapper0
  (JNIEnv* env, jclass class, jbyteArray value, jint valueStart, jint valueLength) {
    jbyteArray result;
    image_t image;
    jboolean isCopy;
    image.source = (char*) (*env)->GetByteArrayElements(env, value, &isCopy);
    if(image.source == NULL) {
        return NULL;
    }
    image.sourceStart = valueStart;
    image.sourceLength = valueLength;
    thumbnailMapper(&image);
    if(image.destinationLength <= 0) {
        cleanupJava(env, &image, &value, isCopy);
        return NULL;
    }
    result = (*env)->NewByteArray(env, image.destinationLength);
    if(result == NULL) {
        cleanupJava(env, &image, &value, isCopy);
        return NULL;
    }
    (*env)->SetByteArrayRegion(env, result, 0, image.destinationLength, (jbyte*)image.destination);
    cleanupJava(env, &image, &value, isCopy);
    return result;
}
