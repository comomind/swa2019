#ifndef _Included_voip_native_util_H
#define _Included_voip_native_util_H

#include <jni.h>
#include <android/log.h>

#ifdef __cplusplus
extern "C" {
#endif

#define   LOG_TAG_D        "VoipNativeD"
#define   LOG_TAG_I        "VoipNativeI"

#ifdef NDEBUG
//#define LOGD(...)
//#define LOGI(...)
//#define LOGPERF(...)
#define LOGD(...)     __android_log_print(ANDROID_LOG_DEBUG,  LOG_TAG_D,    __VA_ARGS__)
#define LOGI(...)     __android_log_print(ANDROID_LOG_DEBUG,  LOG_TAG_I,    __VA_ARGS__)
#define LOGPERF(...)  __android_log_print(ANDROID_LOG_INFO,   LOG_TAG_PERF, __VA_ARGS__)
#else
#define LOGD(...)     __android_log_print(ANDROID_LOG_DEBUG,  LOG_TAG_D,    __VA_ARGS__)
#define LOGI(...)     __android_log_print(ANDROID_LOG_DEBUG,  LOG_TAG_I,    __VA_ARGS__)
#define LOGPERF(...)  __android_log_print(ANDROID_LOG_INFO,   LOG_TAG_PERF, __VA_ARGS__)
#endif

#ifdef __cplusplus
}
#endif

#endif // _Included_voip_native_util_H
