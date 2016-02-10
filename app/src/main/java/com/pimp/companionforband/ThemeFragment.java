package com.pimp.companionforband;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.microsoft.band.BandClient;
import com.microsoft.band.BandClientManager;
import com.microsoft.band.BandException;
import com.microsoft.band.BandInfo;
import com.microsoft.band.BandTheme;
import com.microsoft.band.ConnectionState;

import java.io.FileNotFoundException;
import java.io.InputStream;

import de.keyboardsurfer.android.widget.crouton.Configuration;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class ThemeFragment extends Fragment {
    private static int REQUEST_PICTURE = 1;
    private static int REQUEST_CROP_PICTURE = 2;
    int base, highlight, lowlight, secondaryText, highContrast, muted;
    BitmapDrawable bitmapDrawable;
    boolean band2 = true;
    private BandClient client = null;
    private ImageView imageView;
    private Button btnUpdateMe, btnPickMe, btnUpdateTheme, btnGetMeTile, btnGetTheme;

    public static ThemeFragment newInstance() {
        return new ThemeFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_theme, container, false);
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        imageView = (ImageView) view.findViewById(R.id.selected_me_tile_image_view);

        btnPickMe = (Button) view.findViewById(R.id.pick_me_tile_button);
        btnPickMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new pickTask().execute();
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 9);
            }
        });

        btnGetMeTile = (Button) view.findViewById(R.id.get_me_tile_button);
        btnGetMeTile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new getMeTask().execute();
            }
        });

        btnUpdateMe = (Button) view.findViewById(R.id.update_me_tile_button);
        btnUpdateMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bitmapDrawable = (BitmapDrawable) imageView.getDrawable();
                new appTask().execute();
            }
        });

        SharedPreferences sharedPreferences = getContext().getSharedPreferences("MyPrefs", 0);

        base = sharedPreferences.getInt("base", -16777216);
        highlight = sharedPreferences.getInt("highLight", -16777216);
        lowlight = sharedPreferences.getInt("lowLight", -16777216);
        secondaryText = sharedPreferences.getInt("secondaryText", -16777216);
        highContrast = sharedPreferences.getInt("highContrast", -16777216);
        muted = sharedPreferences.getInt("muted", -16777216);

        btnGetTheme = (Button) view.findViewById(R.id.get_theme_button);
        btnGetTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new getThemeTask().execute(view);
            }
        });

        btnUpdateTheme = (Button) view.findViewById(R.id.update_theme_button);
        btnUpdateTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new themeTask().execute();
            }
        });

        view.findViewById(R.id.base)
                .setBackgroundColor(Color.parseColor(String.format("#%06X", (0xFFFFFF & base))));
        view.findViewById(R.id.highlight)
                .setBackgroundColor(Color.parseColor(String.format("#%06X", (0xFFFFFF & highlight))));
        view.findViewById(R.id.lowlight)
                .setBackgroundColor(Color.parseColor(String.format("#%06X", (0xFFFFFF & lowlight))));
        view.findViewById(R.id.secondaryText)
                .setBackgroundColor(Color.parseColor(String.format("#%06X", (0xFFFFFF & secondaryText))));
        view.findViewById(R.id.highContrast)
                .setBackgroundColor(Color.parseColor(String.format("#%06X", (0xFFFFFF & highContrast))));
        view.findViewById(R.id.muted)
                .setBackgroundColor(Color.parseColor(String.format("#%06X", (0xFFFFFF & muted))));

        new task().execute();
    }

    @Override
    public void onDestroy() {
        if (client != null) {
            try {
                client.disconnect().await();
            } catch (InterruptedException e) {
                // Do nothing as this is happening during destroy
            } catch (BandException e) {
                // Do nothing as this is happening during destroy
            }
        }
        super.onDestroy();
    }

    private void appendToUI(final String string, final Style style) {
        try {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Crouton.cancelAllCroutons();
                    if (getView() != null) {
                        Crouton crouton = Crouton.makeText(getActivity(), string,
                                style, (ViewGroup) getView().findViewById(R.id.theme));
                        Configuration configuration = new Configuration.Builder()
                                .setDuration(Configuration.DURATION_INFINITE)
                                .setInAnimation(R.anim.fade_in)
                                .build();
                        crouton.setConfiguration(configuration);
                        crouton.show();
                    }
                }
            });
        } catch (Exception e) {
            //
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 9) {
            try {
                final Uri imageUri = data.getData();
                final InputStream imageStream = getActivity().getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                if (band2)
                    imageView.setImageBitmap(Bitmap.createScaledBitmap(selectedImage, 310, 128, false));
                else
                    imageView.setImageBitmap(Bitmap.createScaledBitmap(selectedImage, 310, 102, false));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean getConnectedBandClient() throws InterruptedException, BandException {
        if (client == null) {
            BandInfo[] devices = BandClientManager.getInstance().getPairedBands();
            if (devices.length == 0) {
                appendToUI(getString(R.string.band_not_paired), Style.ALERT);
                return false;
            }
            client = BandClientManager.getInstance().create(getActivity(), devices[0]);
        } else if (ConnectionState.CONNECTED == client.getConnectionState()) {
            return true;
        }

        appendToUI(getString(R.string.band_connecting), Style.INFO);
        return ConnectionState.CONNECTED == client.connect().await();
    }

    private void handleBandException(BandException e) {
        String exceptionMessage;
        switch (e.getErrorType()) {
            case DEVICE_ERROR:
                exceptionMessage = getString(R.string.band_not_found);
                break;
            case UNSUPPORTED_SDK_VERSION_ERROR:
                exceptionMessage = getString(R.string.band_unsupported_sdk);
                break;
            case SERVICE_ERROR:
                exceptionMessage = getString(R.string.band_service_unavailable);
                break;
            case BAND_FULL_ERROR:
                exceptionMessage = getString(R.string.band_full);
                break;
            default:
                exceptionMessage = getString(R.string.band_unknown_error) + " : " + e.getMessage();
                break;
        }
        appendToUI(exceptionMessage, Style.ALERT);
    }

    private class pickTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                if (getConnectedBandClient()) {
                    appendToUI(getString(R.string.band_connected), Style.CONFIRM);
                    band2 = Integer.parseInt(client.getHardwareVersion().await()) >= 20;
                }
            } catch (Exception exception) {
                //
            }
            return null;
        }
    }

    private class getMeTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                if (getConnectedBandClient()) {
                    appendToUI(getString(R.string.band_grabbing_info), Style.INFO);
                    final Bitmap bitmap = client.getPersonalizationManager().getMeTileImage().await();

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            imageView.setImageBitmap(bitmap);
                            appendToUI(getString(R.string.band_done), Style.CONFIRM);
                        }
                    });
                }
            } catch (BandException exception) {
                handleBandException(exception);
            } catch (Exception e) {
                //
            }
            return null;
        }
    }

    private class getThemeTask extends AsyncTask<View, Void, BandTheme> {
        View view;

        @Override
        protected BandTheme doInBackground(final View... params) {
            view = params[0];
            try {
                if (getConnectedBandClient()) {
                    appendToUI(getString(R.string.band_grabbing_info), Style.INFO);
                    return client.getPersonalizationManager().getTheme().await();
                }
            } catch (BandException exception) {
                handleBandException(exception);
            } catch (Exception e) {
                //
            }
            return null;
        }

        @Override
        protected void onPostExecute(BandTheme bandTheme) {
            SharedPreferences settings = getActivity().getSharedPreferences("MyPrefs", 0);
            SharedPreferences.Editor editor = settings.edit();

            if (bandTheme != null) {
                base = bandTheme.getBaseColor();
                highlight = bandTheme.getHighlightColor();
                lowlight = bandTheme.getLowlightColor();
                secondaryText = bandTheme.getSecondaryTextColor();
                highContrast = bandTheme.getHighContrastColor();
                muted = bandTheme.getMutedColor();

                editor.putInt("base", base);
                editor.putInt("highLight", highlight);
                editor.putInt("lowLight", lowlight);
                editor.putInt("secondaryText", secondaryText);
                editor.putInt("highContrast", highContrast);
                editor.putInt("muted", muted);
                editor.apply();
            }

            view.findViewById(R.id.base)
                    .setBackgroundColor(Color.parseColor(String.format("#%06X", (0xFFFFFF & base))));
            view.findViewById(R.id.highlight)
                    .setBackgroundColor(Color.parseColor(String.format("#%06X", (0xFFFFFF & highlight))));
            view.findViewById(R.id.lowlight)
                    .setBackgroundColor(Color.parseColor(String.format("#%06X", (0xFFFFFF & lowlight))));
            view.findViewById(R.id.secondaryText)
                    .setBackgroundColor(Color.parseColor(String.format("#%06X", (0xFFFFFF & secondaryText))));
            view.findViewById(R.id.highContrast)
                    .setBackgroundColor(Color.parseColor(String.format("#%06X", (0xFFFFFF & highContrast))));
            view.findViewById(R.id.muted)
                    .setBackgroundColor(Color.parseColor(String.format("#%06X", (0xFFFFFF & muted))));
            appendToUI(getString(R.string.band_done), Style.CONFIRM);
        }
    }

    private class task extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                SharedPreferences settings = getContext().getSharedPreferences("MyPrefs", 0);
                SharedPreferences.Editor editor = settings.edit();
                if (getConnectedBandClient()) {
                    band2 = Integer.parseInt(client.getHardwareVersion().await()) >= 20;
                    appendToUI(getString(R.string.band_connected), Style.CONFIRM);
                } else {
                    appendToUI(getString(R.string.band_not_found), Style.ALERT);
                }
                editor.putBoolean("band2", band2);
                editor.apply();
            } catch (BandException e) {
                handleBandException(e);
            } catch (Exception e) {
                appendToUI(e.getMessage(), Style.ALERT);
            }
            return null;
        }
    }

    private class appTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                if (getConnectedBandClient()) {
                    appendToUI(getString(R.string.band_connected), Style.CONFIRM);
                    appendToUI(getString(R.string.me_tile_updating), Style.INFO);

                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inScaled = false;
                    options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                    Bitmap image;
                    int hardwareVersion = Integer.parseInt(client.getHardwareVersion().await());
                    if (hardwareVersion >= 20) {
                        image = Bitmap.createScaledBitmap(bitmapDrawable.getBitmap(), 310, 128, false);
                    } else {
                        image = Bitmap.createScaledBitmap(bitmapDrawable.getBitmap(), 310, 102, false);
                    }

                    client.getPersonalizationManager().setMeTileImage(image).await();
                    appendToUI(getString(R.string.me_tile_updated), Style.CONFIRM);
                } else {
                    appendToUI(getString(R.string.band_not_found), Style.ALERT);
                }

            } catch (BandException e) {
                handleBandException(e);
            } catch (Exception e) {
                appendToUI(e.getMessage(), Style.ALERT);
            }
            return null;
        }

    }

    private class themeTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                if (getConnectedBandClient()) {
                    appendToUI(getString(R.string.band_connected), Style.CONFIRM);
                    appendToUI(getString(R.string.theme_updating), Style.INFO);

                    SharedPreferences sharedPreferences = getContext().getSharedPreferences("MyPrefs", 0);

                    base = sharedPreferences.getInt("base", -16777216);
                    highlight = sharedPreferences.getInt("highLight", -16777216);
                    lowlight = sharedPreferences.getInt("lowLight", -16777216);
                    secondaryText = sharedPreferences.getInt("secondaryText", -16777216);
                    highContrast = sharedPreferences.getInt("highContrast", -16777216);
                    muted = sharedPreferences.getInt("muted", -16777216);

                    try {
                        client.getPersonalizationManager().setTheme(new
                                BandTheme(base, highlight, lowlight, secondaryText, highContrast,
                                muted)).await();
                    } catch (InterruptedException e) {
                        //
                    } catch (BandException e) {
                        handleBandException(e);
                    }
                    appendToUI(getString(R.string.theme_updated), Style.CONFIRM);
                } else {
                    appendToUI(getString(R.string.band_not_found), Style.ALERT);
                }

            } catch (BandException e) {
                handleBandException(e);
            } catch (Exception e) {
                appendToUI(e.getMessage(), Style.ALERT);
            }
            return null;
        }
    }
}