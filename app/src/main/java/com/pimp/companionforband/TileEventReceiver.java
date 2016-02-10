package com.pimp.companionforband;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.SystemClock;
import android.view.KeyEvent;

import com.microsoft.band.tiles.TileButtonEvent;
import com.microsoft.band.tiles.TileEvent;

public class TileEventReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (intent.getAction() == TileEvent.ACTION_TILE_BUTTON_PRESSED) {
            TileButtonEvent buttonData = intent.getParcelableExtra(TileEvent.TILE_EVENT_DATA);

            long eventtime = SystemClock.uptimeMillis() - 1;
            KeyEvent downEvent, upEvent;
            switch (buttonData.getElementID()) {
                case 11:
                case 12:
                    downEvent = new KeyEvent(eventtime, eventtime, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE, 0);
                    audioManager.dispatchMediaKeyEvent(downEvent);

                    eventtime++;
                    upEvent = new KeyEvent(eventtime, eventtime, KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE, 0);
                    audioManager.dispatchMediaKeyEvent(upEvent);
                    break;

                case 21:
                    downEvent = new KeyEvent(eventtime, eventtime, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PREVIOUS, 0);
                    audioManager.dispatchMediaKeyEvent(downEvent);

                    eventtime++;
                    upEvent = new KeyEvent(eventtime, eventtime, KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_PREVIOUS, 0);
                    audioManager.dispatchMediaKeyEvent(upEvent);
                    break;
                case 22:
                    downEvent = new KeyEvent(eventtime, eventtime, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_NEXT, 0);
                    audioManager.dispatchMediaKeyEvent(downEvent);

                    eventtime++;
                    upEvent = new KeyEvent(eventtime, eventtime, KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_NEXT, 0);
                    audioManager.dispatchMediaKeyEvent(upEvent);
                    break;

                case 31:
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                            audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) + 1,
                            AudioManager.FLAG_SHOW_UI);
                    break;
                case 32:
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                            audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) - 1,
                            AudioManager.FLAG_SHOW_UI);
                    break;

                case 91:
                case 92:
                case 93:
                    Intent i = new Intent(context, CameraActivity.class);
                    i.setAction(intent.getAction());
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    i.putExtra(TileEvent.TILE_EVENT_DATA, intent.getParcelableExtra(TileEvent.TILE_EVENT_DATA));
                    switch (buttonData.getElementID()) {
                        case 91:
                            i.putExtra("new intent", "TileEventReceiver");
                            break;
                        case 92:
                            i.putExtra("new intent", "SwitchCamera");
                            break;
                        case 93:
                            i.putExtra("new intent", "FlashMode");
                            break;

                    }
                    context.startActivity(i);
                    break;
            }
        }
    }
}