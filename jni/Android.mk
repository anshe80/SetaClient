
    LOCAL_PATH := $(call my-dir)

    include $(CLEAR_VARS)

    LOCAL_MODULE    := convert

	LOCAL_SRC_FILES := convert.c bitstream.c fft.c id3tag.c mpglib_interface.c presets.c  quantize.c   reservoir.c tables.c  util.c  VbrTag.c encoder.c  gain_analysis.c lame.c  newmdct.c   psymodel.c quantize_pvt.c set_get.c  takehiro.c vbrquantize.c version.c
	
	LOCAL_LDLIBS += -llog

    include $(BUILD_SHARED_LIBRARY)
    