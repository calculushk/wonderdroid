LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_ARM_MODE := arm
LOCAL_LDLIBS := -llog
LOCAL_CFLAGS   = -std=c99 -ftree-vectorizer-verbose=1 -ftree-vectorize -finline-functions -ffast-math -O3
LOCAL_MODULE    := wonderswan
LOCAL_SRC_FILES := blip/Blip_Buffer.cpp wswan/sound.cpp wswan/tcache.c wswan/rtc.c wswan/gfx.c  wswan/memory.c wswan/eeprom.c wswan/interrupt.c wswan/v30mz.c wswan/jni.c


include $(BUILD_SHARED_LIBRARY)

ifeq ($(TARGET_ARCH_ABI),armeabi-v7a)	
	include $(CLEAR_VARS)
     	LOCAL_LDLIBS := -llog
	LOCAL_CFLAGS   = -std=c99 -ftree-vectorizer-verbose=1 -ftree-vectorize -finline-functions -ffast-math -O3
	LOCAL_MODULE    := wonderswan-neon
	LOCAL_SRC_FILES := blip/Blip_Buffer.cpp wswan/sound.cpp wswan/tcache.c wswan/rtc.c wswan/gfx.c  wswan/memory.c wswan/eeprom.c wswan/interrupt.c wswan/v30mz.c wswan/jni.c
      	LOCAL_ARM_NEON  := true
      	include $(BUILD_SHARED_LIBRARY)
endif # TARGET_ARCH_ABI == armeabi-v7a

include $(CLEAR_VARS)
LOCAL_MODULE := cpufeat
LOCAL_SRC_FILES := android/cpufeat.c
LOCAL_STATIC_LIBRARIES := cpufeatures
include $(BUILD_SHARED_LIBRARY)
$(call import-module,android/cpufeatures)

