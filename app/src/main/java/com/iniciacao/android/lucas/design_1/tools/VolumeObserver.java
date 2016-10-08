package com.iniciacao.android.lucas.design_1.tools;

import android.content.Context;
import android.database.ContentObserver;
import android.media.AudioManager;
import android.os.Handler;
import android.widget.Toast;

/**
 * Created by chendehua on 16/10/8.
 */

public class VolumeObserver extends ContentObserver {

    private Context context;
    private int lastVolume;

    /**
     * Creates a content observer.
     *
     * @param handler The handler to run {@link #onChange} on, or null if none.
     */
    public VolumeObserver(Context context, Handler handler) {
        super(handler);
        this.context = context;

        AudioManager audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        lastVolume = audio.getStreamVolume(AudioManager.STREAM_MUSIC);
        audio.setStreamVolume(AudioManager.STREAM_MUSIC, audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);

        AudioManager audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        int currentVolume = audio.getStreamVolume(AudioManager.STREAM_MUSIC);

        int delta = lastVolume - currentVolume;

        if (delta != 0) {
            lastVolume = currentVolume;
            volumeChanged();
        }
    }

    public void volumeChanged() {
        AudioManager mAudioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
    }
}
