package com.ak.objectFinder;

import android.content.Context;
import android.os.Build;

import java.util.Locale;

public class TextToSpeechHelper {

    private android.speech.tts.TextToSpeech tts;
    private boolean ready = false;

    public TextToSpeechHelper(Context c) {
        tts = new android.speech.tts.TextToSpeech(c, new android.speech.tts.TextToSpeech.OnInitListener() {
            public void onInit(int status) {
                if (status == android.speech.tts.TextToSpeech.SUCCESS) {
                    ready = true;
                }
            }
        });
    }

    public TextToSpeechHelper(Context c, String initText) {
        tts = new android.speech.tts.TextToSpeech(c, new android.speech.tts.TextToSpeech.OnInitListener() {
            public void onInit(int status) {
                if (status == android.speech.tts.TextToSpeech.SUCCESS) {
                    ready = true;
                    speak(initText);
                }
            }
        });
    }

    public void speak(String speechtext) {
        if (Globals.audioPref && ready) {
            int result = tts.setLanguage(Locale.ENGLISH);
            if (result == android.speech.tts.TextToSpeech.LANG_MISSING_DATA ||
                    result == android.speech.tts.TextToSpeech.LANG_NOT_SUPPORTED) {
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

    public void cancel() {
        if (tts.isSpeaking()) {
            tts.stop();
        }
    }

}
