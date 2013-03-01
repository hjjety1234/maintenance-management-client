LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

# This is the target being built.
LOCAL_MODULE:= libBsdiff


# All of the source files that we will compile.
LOCAL_SRC_FILES:= com_wondertek_video_appmanager_AppManager.c \
				  bzlib.c \
  				  blocksort.c \
  				  compress.c \
  				  crctable.c \
  				  decompress.c \
  				  huffman.c \
  				  randtable.c \
  				  bzip2.c \
  				    	
# No static libraries.
# LOCAL_STATIC_LIBRARIES := libbz


# Also need the JNI headers.
LOCAL_C_INCLUDES += \
	D:/Android/android-ndk-r8b/platforms/android-8/arch-arm/usr/include \
	$(JNI_H_INCLUDE) 

# No special compiler flags.
LOCAL_CFLAGS +=

include $(BUILD_SHARED_LIBRARY)