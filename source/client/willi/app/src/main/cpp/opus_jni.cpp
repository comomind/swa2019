/*
 * Copyright (C) 2009 The Sipdroid Open Source Project
 *
 * This file is part of Sipdroid (http://www.sipdroid.org)
 *
 * Sipdroid is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * This source code is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this source code; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

#include <stdlib.h>
#include <stdio.h>
#include <fcntl.h>
#include <unistd.h>
#include <memory.h>
#include <ctype.h>
#include <jni.h>
#include <android/log.h>

#include "opus.h"
#include "util.h"

#define LOG_TAG "OpusNative" // text for log tag

#undef DEBUG_OPUS

static int codec_open = 0;

static OpusEncoder *encoder;
static OpusDecoder *decoder;

extern "C"
JNIEXPORT jint JNICALL
Java_com_lg_sixsenses_willi_codec_audio_OpusCodec_opusOpenNative(JNIEnv *env, jclass type,
    jint sampleRate, jint numberOfChannels) {

  int err;

  if (codec_open != 0) {
    codec_open++;
    return (jint) 0;
  }

  // create encoder
  encoder = opus_encoder_create(sampleRate, numberOfChannels, OPUS_APPLICATION_AUDIO, &err);
  if (err < 0) {
    LOGD("failed to create encoder: %d", err);
    return -1;
  }

//  err = opus_encoder_ctl(encoder, OPUS_SET_BITRATE(BITRATE));
//  if (err < 0) {
//    LOGD("failed to set bitrate: %d", err);
//    return -1;
//  }

  // create decoder
  decoder = opus_decoder_create(sampleRate, numberOfChannels, &err);
  if (err < 0) {
    LOGD("failed to create decoder: %d", err);
    return -1;
  }

  codec_open++;
  return (jint)0;
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_lg_sixsenses_willi_codec_audio_OpusCodec_opusDecodeNative(JNIEnv *env, jclass type,
                                                            jbyteArray input_,
                                                            jint frameSize,
                                                            jbyteArray output_) {

  jint inputArraySize = env->GetArrayLength(input_);

  jbyte* encodedData = env->GetByteArrayElements(input_, 0);
  jbyte* decodedData = env->GetByteArrayElements(output_, 0);
  int samples = opus_decode(decoder, (const unsigned char *) encodedData, inputArraySize,
                            (opus_int16 *) decodedData, frameSize, 0);
  env->ReleaseByteArrayElements(input_, encodedData, JNI_ABORT);
  env->ReleaseByteArrayElements(output_, decodedData,0);

  return samples;
}


extern "C"
JNIEXPORT jint JNICALL
Java_com_lg_sixsenses_willi_codec_audio_OpusCodec_opusEncodeNative(JNIEnv *env, jclass type,
                                                            jbyteArray input_,
                                                            jint frameSize,
                                                            jbyteArray output_) {
  jint outputArraySize = env->GetArrayLength(output_);

  jbyte* audioSignal = env->GetByteArrayElements(input_, 0);
  jbyte* encodedSignal = env->GetByteArrayElements(output_, 0);

  if (((unsigned long)audioSignal) % 2) {
    // Unaligned...
    LOGD("OpusCodec unaligned error");
    return OPUS_BAD_ARG;
  }

  int dataArraySize = opus_encode(encoder, (const opus_int16 *) audioSignal, frameSize,
                                  (unsigned char *) encodedSignal, outputArraySize);

  env->ReleaseByteArrayElements(input_, audioSignal, JNI_ABORT);
  env->ReleaseByteArrayElements(output_,encodedSignal,0);

  return dataArraySize;
}


extern "C"
JNIEXPORT void JNICALL
Java_com_lg_sixsenses_willi_codec_audio_OpusCodec_opusCloseNative(JNIEnv *env, jclass type) {
  if (--codec_open != 0)
    return;

  opus_encoder_destroy(encoder);
  opus_decoder_destroy(decoder);
}