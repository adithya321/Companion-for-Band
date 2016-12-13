/*
 * Companion for Band
 * Copyright (C) 2016  Adithya J
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.pimp.companionforband.fragments.theme;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.graphics.Palette;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.microsoft.band.BandException;
import com.microsoft.band.BandTheme;
import com.pimp.companionforband.R;
import com.pimp.companionforband.activities.main.MainActivity;
import com.pimp.companionforband.utils.band.BandUtils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class ThemeFragment extends Fragment {
    int base, highlight, lowlight, secondaryText, highContrast, muted;
    Button btnBase, btnHighlight, btnLowlight, btnSecondaryText, btnHighContrast, btnMuted;
    BitmapDrawable bitmapDrawable;
    Palette.PaletteAsyncListener paletteListener = new Palette.PaletteAsyncListener() {
        public void onGenerated(Palette palette) {
            int def = Color.WHITE;

            SharedPreferences sharedPreferences = getContext().getSharedPreferences("MyPrefs", 0);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("base", palette.getVibrantColor(def));
            editor.putInt("highLight", palette.getLightVibrantColor(def));
            editor.putInt("lowLight", palette.getDarkVibrantColor(def));
            editor.putInt("secondaryText", palette.getMutedColor(def));
            editor.putInt("highContrast", palette.getLightMutedColor(def));
            editor.putInt("muted", palette.getDarkMutedColor(def));
            editor.apply();

            btnBase.setBackgroundColor(palette.getVibrantColor(def));
            btnHighlight.setBackgroundColor(palette.getLightVibrantColor(def));
            btnLowlight.setBackgroundColor(palette.getDarkVibrantColor(def));
            btnSecondaryText.setBackgroundColor(palette.getMutedColor(def));
            btnHighContrast.setBackgroundColor(palette.getLightMutedColor(def));
            btnMuted.setBackgroundColor(palette.getDarkMutedColor(def));
        }
    };
    private ImageView imageView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_theme, container, false);
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        imageView = (ImageView) view.findViewById(R.id.selected_me_tile_image_view);

        SharedPreferences settings = getActivity().getSharedPreferences("MyPrefs", 0);
        Drawable meTileDrawable = null;
        String encoded = settings.getString("me_tile_image", "null");
        if (!encoded.equals("null")) {
            byte[] imageAsBytes = Base64.decode(encoded.getBytes(), Base64.DEFAULT);
            meTileDrawable = new BitmapDrawable(getResources(), BitmapFactory
                    .decodeByteArray(imageAsBytes, 0, imageAsBytes.length));
        }
        if (meTileDrawable != null)
            imageView.setImageDrawable(meTileDrawable);

        Button btnUpdateMe, btnPickMe, btnUpdateTheme, btnGetMeTile, btnGetTheme, btnGetColors;
        btnPickMe = (Button) view.findViewById(R.id.pick_me_tile_button);
        btnPickMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

        btnBase = (Button) view.findViewById(R.id.base);
        btnHighlight = (Button) view.findViewById(R.id.highlight);
        btnLowlight = (Button) view.findViewById(R.id.lowlight);
        btnSecondaryText = (Button) view.findViewById(R.id.secondaryText);
        btnHighContrast = (Button) view.findViewById(R.id.highContrast);
        btnMuted = (Button) view.findViewById(R.id.muted);

        btnBase.setBackgroundColor(Color.parseColor(String.format("#%06X", (0xFFFFFF & base))));
        btnHighlight.setBackgroundColor(Color.parseColor(String.format("#%06X", (0xFFFFFF & highlight))));
        btnLowlight.setBackgroundColor(Color.parseColor(String.format("#%06X", (0xFFFFFF & lowlight))));
        btnSecondaryText.setBackgroundColor(Color.parseColor(String.format("#%06X", (0xFFFFFF & secondaryText))));
        btnHighContrast.setBackgroundColor(Color.parseColor(String.format("#%06X", (0xFFFFFF & highContrast))));
        btnMuted.setBackgroundColor(Color.parseColor(String.format("#%06X", (0xFFFFFF & muted))));

        btnGetColors = (Button) view.findViewById(R.id.get_colors_button);
        btnGetColors.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bitmapDrawable = (BitmapDrawable) imageView.getDrawable();
                if (bitmapDrawable.getBitmap() != null && !bitmapDrawable.getBitmap().isRecycled()) {
                    Palette.from(bitmapDrawable.getBitmap()).generate(paletteListener);
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 9) {
            try {
                final Uri imageUri = data.getData();
                final InputStream imageStream = getActivity().getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                if (MainActivity.band2)
                    imageView.setImageBitmap(Bitmap.createScaledBitmap(selectedImage, 310, 128, false));
                else
                    imageView.setImageBitmap(Bitmap.createScaledBitmap(selectedImage, 310, 102, false));
            } catch (Exception e) {
                //
            }
        }
    }

    private class getMeTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                if (BandUtils.getConnectedBandClient()) {
                    MainActivity.appendToUI(getString(R.string.band_grabbing_info), "Style.INFO");
                    final Bitmap bitmap = MainActivity.client.getPersonalizationManager().getMeTileImage().await();

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            imageView.setImageBitmap(bitmap);
                            MainActivity.appendToUI(getString(R.string.band_done), "Style.CONFIRM");
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                            byte[] b = baos.toByteArray();
                            String encoded = Base64.encodeToString(b, Base64.DEFAULT);
                            SharedPreferences sharedPreferences = getContext().getSharedPreferences("MyPrefs", 0);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("me_tile_image", encoded);
                            editor.apply();
                        }
                    });
                }
            } catch (BandException exception) {
                BandUtils.handleBandException(exception);
            } catch (Exception e) {
                MainActivity.appendToUI(e.toString(), "Style.ALERT");
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
                if (BandUtils.getConnectedBandClient()) {
                    MainActivity.appendToUI(getString(R.string.band_grabbing_info), "Style.INFO");
                    return MainActivity.client.getPersonalizationManager().getTheme().await();
                }
            } catch (BandException exception) {
                BandUtils.handleBandException(exception);
            } catch (Exception e) {
                MainActivity.appendToUI(e.toString(), "Style.ALERT");
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
            MainActivity.appendToUI(getString(R.string.band_done), "Style.CONFIRM");
        }
    }

    private class appTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                if (BandUtils.getConnectedBandClient()) {
                    MainActivity.appendToUI(getString(R.string.me_tile_updating), "Style.INFO");

                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inScaled = false;
                    options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                    Bitmap image;
                    int hardwareVersion = Integer.parseInt(MainActivity.client.getHardwareVersion().await());
                    if (hardwareVersion >= 20) {
                        image = Bitmap.createScaledBitmap(bitmapDrawable.getBitmap(), 310, 128, false);
                    } else {
                        image = Bitmap.createScaledBitmap(bitmapDrawable.getBitmap(), 310, 102, false);
                    }

                    MainActivity.client.getPersonalizationManager().setMeTileImage(image).await();
                    MainActivity.appendToUI(getString(R.string.me_tile_updated), "Style.CONFIRM");
                } else {
                    MainActivity.appendToUI(getString(R.string.band_not_found), "Style.ALERT");
                }

            } catch (BandException e) {
                BandUtils.handleBandException(e);
            } catch (Exception e) {
                MainActivity.appendToUI(e.getMessage(), "Style.ALERT");
            }
            return null;
        }

    }

    private class themeTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                if (BandUtils.getConnectedBandClient()) {
                    MainActivity.appendToUI(getString(R.string.theme_updating), "Style.INFO");

                    SharedPreferences sharedPreferences = getContext().getSharedPreferences("MyPrefs", 0);

                    base = sharedPreferences.getInt("base", -16777216);
                    highlight = sharedPreferences.getInt("highLight", -16777216);
                    lowlight = sharedPreferences.getInt("lowLight", -16777216);
                    secondaryText = sharedPreferences.getInt("secondaryText", -16777216);
                    highContrast = sharedPreferences.getInt("highContrast", -16777216);
                    muted = sharedPreferences.getInt("muted", -16777216);

                    try {
                        MainActivity.client.getPersonalizationManager().setTheme(new
                                BandTheme(base, highlight, lowlight, secondaryText, highContrast,
                                muted)).await();
                    } catch (InterruptedException e) {
                        MainActivity.appendToUI(e.toString(), "Style.ALERT");
                    } catch (BandException e) {
                        BandUtils.handleBandException(e);
                    }
                    MainActivity.appendToUI(getString(R.string.theme_updated), "Style.CONFIRM");
                } else {
                    MainActivity.appendToUI(getString(R.string.band_not_found), "Style.ALERT");
                }

            } catch (BandException e) {
                BandUtils.handleBandException(e);
            } catch (Exception e) {
                MainActivity.appendToUI(e.getMessage(), "Style.ALERT");
            }
            return null;
        }
    }
}
