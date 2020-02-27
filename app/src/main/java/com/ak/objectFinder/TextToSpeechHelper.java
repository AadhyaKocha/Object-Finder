package com.ak.objectFinder;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import java.util.Locale;

public class TextToSpeechHelper {

    public static android.speech.tts.TextToSpeech tts;

    public static void speak(Context context, String speechtext) {

        if (Globals.audioPref) {

            tts = new android.speech.tts.TextToSpeech(context, new android.speech.tts.TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    if (status == android.speech.tts.TextToSpeech.SUCCESS) {
                        int result = tts.setLanguage(Locale.ENGLISH);
                        if (result == android.speech.tts.TextToSpeech.LANG_MISSING_DATA ||
                                result == android.speech.tts.TextToSpeech.LANG_NOT_SUPPORTED) {
                            Log.d("audioError", "This language is not supported");
                        } else {
                            tts.setPitch(0.6f);
                            tts.setSpeechRate(1.0f);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                                tts.speak(speechtext, android.speech.tts.TextToSpeech.QUEUE_FLUSH, null, null);
                            else
                                tts.speak(speechtext, android.speech.tts.TextToSpeech.QUEUE_FLUSH, null);
                        }
                    }
                }
            });
        }
    }
}
