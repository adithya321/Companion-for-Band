package com.pimp.companionforband.fragments.extras;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.SystemClock;
import android.view.KeyEvent;
import android.widget.Toast;

import com.microsoft.band.notifications.MessageFlags;
import com.microsoft.band.tiles.TileButtonEvent;
import com.microsoft.band.tiles.TileEvent;
import com.pimp.companionforband.activities.main.MainActivity;

import org.javia.arity.Symbols;

import java.util.Date;

public class TileEventReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences sp = context.getSharedPreferences("calc", 0);
        SharedPreferences.Editor editor = sp.edit();
        String s = sp.getString("calc", "");
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (intent.getAction().equals(TileEvent.ACTION_TILE_BUTTON_PRESSED)) {
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

                case 100:
                case 101:
                case 102:
                case 103:
                case 104:
                case 105:
                case 106:
                case 107:
                case 108:
                case 109:
                    editor.putString("calc", s + String.valueOf(buttonData.getElementID() % 10));
                    editor.apply();
                    break;

                case 120:
                    editor.putString("calc", s + "+");
                    editor.apply();
                    break;
                case 121:
                    editor.putString("calc", s + "-");
                    editor.apply();
                    break;
                case 122:
                    editor.putString("calc", s + "*");
                    editor.apply();
                    break;
                case 123:
                    editor.putString("calc", s + "/");
                    editor.apply();
                    break;
                case 125:
                    editor.putString("calc", s + "(");
                    editor.apply();
                    break;
                case 126:
                    editor.putString("calc", s + ")");
                    editor.apply();
                    break;
                case 127:
                    editor.putString("calc", s + "^");
                    editor.apply();
                    break;
                case 128:
                    editor.putString("calc", s + ".");
                    editor.apply();
                    break;

                case 124:
                case 129:
                    try {
                        Symbols symbols = new Symbols();
                        MainActivity.client.getNotificationManager()
                                .sendMessage(ExtrasFragment.tileId,
                                        String.valueOf(symbols.eval(sp.getString("calc", ""))),
                                        sp.getString("calc", ""), new Date(),
                                        MessageFlags.SHOW_DIALOG);
                    } catch (Exception e) {
                        Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
                    }
                    editor.putString("calc", "");
                    editor.apply();
                    break;
            }
        }
    }
}