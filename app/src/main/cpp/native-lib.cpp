//
// Created by Anisha Inas Izdihar on 2019-11-29.
//

#include <jni.h>
#include <string>

extern "C" JNIEXPORT jstring JNICALL
Java_id_ac_ui_cs_mobileprogramming_anisha_1inas_wacana_ui_tripDetail_TripDetailViewModel_getTripDateString(
        JNIEnv *env,
        jobject, jstring firstString, jstring secondString) {
    char returnString[50];
    const char *fS = env->GetStringUTFChars(firstString, NULL);
    const char *sS = env->GetStringUTFChars(secondString, NULL);

    strcpy(returnString, fS);
    if (strlen(sS) > 0) {
        strcat(returnString, " - ");
        strcat(returnString, sS);
    }

    env->ReleaseStringUTFChars(firstString, fS);
    env->ReleaseStringUTFChars(secondString, sS);

    return env->NewStringUTF(returnString);
}

extern "C" JNIEXPORT jstring JNICALL
Java_id_ac_ui_cs_mobileprogramming_anisha_1inas_wacana_Repository_getApiKey(
        JNIEnv *env) {
    return env->NewStringUTF("0d41bfc96c89d3302775ec6fa537e07e");
}