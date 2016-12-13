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

package com.pimp.companionforband.fragments.extras;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.microsoft.band.BandException;
import com.microsoft.band.BandIOException;
import com.microsoft.band.notifications.MessageFlags;
import com.microsoft.band.notifications.VibrationType;
import com.microsoft.band.tiles.BandTile;
import com.microsoft.band.tiles.pages.Barcode;
import com.microsoft.band.tiles.pages.BarcodeData;
import com.microsoft.band.tiles.pages.BarcodeType;
import com.microsoft.band.tiles.pages.FilledButtonData;
import com.microsoft.band.tiles.pages.FilledPanel;
import com.microsoft.band.tiles.pages.FlowPanel;
import com.microsoft.band.tiles.pages.FlowPanelOrientation;
import com.microsoft.band.tiles.pages.PageData;
import com.microsoft.band.tiles.pages.PageElement;
import com.microsoft.band.tiles.pages.PageLayout;
import com.microsoft.band.tiles.pages.TextBlock;
import com.microsoft.band.tiles.pages.TextBlockData;
import com.microsoft.band.tiles.pages.TextBlockFont;
import com.microsoft.band.tiles.pages.TextButton;
import com.microsoft.band.tiles.pages.TextButtonData;
import com.pimp.companionforband.R;
import com.pimp.companionforband.activities.main.MainActivity;
import com.pimp.companionforband.utils.band.BandUtils;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public class ExtrasFragment extends Fragment {
    private static final UUID pageId1 = UUID.fromString("29411705-106f-48bc-a671-c6d7cb3e759a");
    private static final UUID pageId2 = UUID.fromString("3f34d8b4-e697-4b4b-89df-823cef78b744");
    private static final UUID pageId3 = UUID.fromString("134055f9-e786-47e9-a0ff-c12cce0b4f96");
    private static final UUID calculatorPageId1 = UUID.fromString("1cf3d563-9a09-4128-b839-5d12d56a002b");
    private static final UUID calculatorPageId2 = UUID.fromString("29acaf23-7d4b-4fc3-9265-680e411594d7");
    private static final UUID cameraPageId1 = UUID.fromString("7efc3ce8-0785-4860-88cd-54a11115298f");
    private static final UUID cameraPageId2 = UUID.fromString("7efc3ce8-0785-4860-88cd-54a11115298e");
    private static final UUID cameraPageId3 = UUID.fromString("7efc3ce8-0785-4860-88cd-54a11115298d");
    private static final UUID flashlightPageId1 = UUID.fromString("6ef43666-4117-432f-ba6a-663d45ba2d71");
    private static final UUID flashlightPageId2 = UUID.fromString("6ef43666-4117-432f-ba6a-663d45ba2d72");

    private static final UUID barcodeId1 = UUID.fromString("d71c0e1a-0bf7-4c00-b916-946b7a07f141");
    private static final UUID barcodeId2 = UUID.fromString("9a705a29-66b9-4777-a2e5-5979b6712b50");
    private static final UUID barcodeId3 = UUID.fromString("8303c1c4-70c7-4ffc-b41b-0809991da568");
    private static final UUID barcodeId4 = UUID.fromString("009d0e6a-9f52-4343-a75c-7799033306df");
    private static final UUID barcodeId5 = UUID.fromString("1cedb320-2572-44b1-b914-68d53e1ec191");

    EditText title, body, name, number;
    Button btnSend, btnHaptic, btnBarcode;

    Spinner hapticSpinner, typeSpinner, pageSpinner;
    Switch calculatorSwitch, musicSwitch, cameraSwitch, messageSwitch, barcodeSwitch, flashlightSwitch;
    boolean message = false, calculator = false, music = false, camera = false, barcode = false, flashlight = false;
    String barcode39 = "MK12345509";
    String barcode417 = "901234567890123456";
    public static UUID tileId = UUID.fromString("3263f46a-38be-4229-afa1-85d4399b7798");
    private UUID calculatorTileId = UUID.fromString("d5f788d2-08fd-4751-b766-fff1d773d5a7");
    private UUID musicTileId = UUID.fromString("48376b8f-1066-4b67-96c7-cecc9951bf3b");
    private UUID cameraTileId = UUID.fromString("006f9aa4-4092-44e2-b911-2f7262d198bc");
    private UUID barcodeTileId = UUID.fromString("23d392df-dd0c-4e93-8c0d-63dd7b02eb52");
    private UUID flashlightTileId = UUID.fromString("c2187a9c-db5c-4e73-9f9d-d742113f91e8");

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_extras, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final SharedPreferences sharedPreferences = getContext().getSharedPreferences("MyPrefs", 0);
        final SharedPreferences.Editor editor = sharedPreferences.edit();

        calculatorSwitch = (Switch) view.findViewById(R.id.calculator);
        calculatorSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new calculatorTask().execute();
                try {
                    if (!doesTileExist(tileId))
                        addTile();
                } catch (Exception e) {
                    Log.e("CALC ADD", e.toString());
                }
            }
        });

        musicSwitch = (Switch) view.findViewById(R.id.music);
        musicSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new musicTask().execute();
            }
        });

        title = (EditText) view.findViewById(R.id.smstitle);
        body = (EditText) view.findViewById(R.id.smsbody);
        btnSend = (Button) view.findViewById(R.id.message_send);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!title.getText().toString().equals("") || !body.getText().toString().equals(""))
                    new smsTask().execute();
                else
                    Toast.makeText(getActivity(), getString(R.string.message_send_error), Toast.LENGTH_LONG).show();
            }
        });

        String[] haptic = {getString(R.string.notification_one), getString(R.string.notification_two),
                getString(R.string.notification_alarm), getString(R.string.notification_timer),
                getString(R.string.one_high), getString(R.string.two_high), getString(R.string.three_high),
                getString(R.string.ramp_up), getString(R.string.ramp_down)};
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item, haptic);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        hapticSpinner = ((Spinner) view.findViewById(R.id.haptic_spinner));
        hapticSpinner.setAdapter(arrayAdapter);

        name = (EditText) view.findViewById(R.id.barcode_name);
        number = (EditText) view.findViewById(R.id.barcode_no);

        String[] type = {getString(R.string.barcode_type), "CODE39", "PDF417"};
        String[] page = {getString(R.string.barcode_page), "1", "2", "3", "4", "5"};

        arrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, type);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner = ((Spinner) view.findViewById(R.id.type_spinner));
        typeSpinner.setAdapter(arrayAdapter);

        arrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, page);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        pageSpinner = ((Spinner) view.findViewById(R.id.page_spinner));
        pageSpinner.setAdapter(arrayAdapter);
        pageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 1:
                        name.setText(sharedPreferences.getString("name1", "Barcode 1"));
                        number.setText(sharedPreferences.getString("no1", barcode39));
                        typeSpinner.setSelection(sharedPreferences.getInt("type1", 1));
                        break;
                    case 2:
                        name.setText(sharedPreferences.getString("name2", "Barcode 2"));
                        number.setText(sharedPreferences.getString("no2", barcode417));
                        typeSpinner.setSelection(sharedPreferences.getInt("type2", 2));
                        break;
                    case 3:
                        name.setText(sharedPreferences.getString("name3", "Barcode 3"));
                        number.setText(sharedPreferences.getString("no3", barcode39));
                        typeSpinner.setSelection(sharedPreferences.getInt("type3", 1));
                        break;
                    case 4:
                        name.setText(sharedPreferences.getString("name4", "Barcode 4"));
                        number.setText(sharedPreferences.getString("no4", barcode417));
                        typeSpinner.setSelection(sharedPreferences.getInt("type4", 2));
                        break;
                    case 5:
                        name.setText(sharedPreferences.getString("name5", "Barcode 5"));
                        number.setText(sharedPreferences.getString("no5", barcode39));
                        typeSpinner.setSelection(sharedPreferences.getInt("type5", 1));
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        btnBarcode = (Button) view.findViewById(R.id.barcode_send);
        btnBarcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (name.getText().toString().equals("") || number.getText().toString().equals(""))
                    Toast.makeText(getActivity(), getString(R.string.barcode_send_error_1), Toast.LENGTH_LONG).show();
                else if (typeSpinner.getSelectedItemPosition() == 0 || pageSpinner.getSelectedItemPosition() == 0)
                    Toast.makeText(getActivity(), getString(R.string.barcode_send_error_2), Toast.LENGTH_LONG).show();
                else {
                    int no = pageSpinner.getSelectedItemPosition();
                    editor.putString("name" + no, name.getText().toString());
                    editor.putString("no" + no, number.getText().toString());
                    editor.putInt("type" + no, typeSpinner.getSelectedItemPosition());
                    editor.apply();
                    new barcodeTask().execute();
                }
            }
        });

        btnHaptic = (Button) view.findViewById(R.id.haptic_send);
        btnHaptic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VibrationType vibrationType = VibrationType.NOTIFICATION_ONE_TONE;
                switch (hapticSpinner.getSelectedItemPosition()) {
                    case 0:
                        vibrationType = VibrationType.NOTIFICATION_ONE_TONE;
                        break;
                    case 1:
                        vibrationType = VibrationType.NOTIFICATION_TWO_TONE;
                        break;
                    case 2:
                        vibrationType = VibrationType.NOTIFICATION_ALARM;
                        break;
                    case 3:
                        vibrationType = VibrationType.NOTIFICATION_TIMER;
                        break;
                    case 4:
                        vibrationType = VibrationType.ONE_TONE_HIGH;
                        break;
                    case 5:
                        vibrationType = VibrationType.TWO_TONE_HIGH;
                        break;
                    case 6:
                        vibrationType = VibrationType.THREE_TONE_HIGH;
                        break;
                    case 7:
                        vibrationType = VibrationType.RAMP_UP;
                        break;
                    case 8:
                        vibrationType = VibrationType.RAMP_DOWN;
                        break;
                }
                try {
                    MainActivity.client.getNotificationManager().vibrate(vibrationType).await();
                } catch (BandException e) {
                    BandUtils.handleBandException(e);
                } catch (Exception e) {
                    MainActivity.appendToUI(e.getMessage(), "Style.ALERT");
                }
            }
        });

        cameraSwitch = (Switch) view.findViewById(R.id.camera);
        cameraSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new cameraTask().execute();
            }
        });

        flashlightSwitch = (Switch) view.findViewById(R.id.flashlight);
        flashlightSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new flashlightTask().execute();
            }
        });

        messageSwitch = (Switch) view.findViewById(R.id.message);
        messageSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (messageSwitch.isChecked())
                        addTile();
                    else
                        new removeTask().execute(tileId);
                } catch (Exception e) {
                    //
                }
            }
        });

        barcodeSwitch = (Switch) view.findViewById(R.id.barcode);
        barcodeSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (barcodeSwitch.isChecked())
                        addBarcodeTile();
                    else
                        new removeTask().execute(barcodeTileId);
                } catch (Exception e) {
                    //
                }
            }
        });

        pageSpinner.setSelection(1);

        new task().execute();
    }

    private void removeTile(UUID uuid) throws InterruptedException, BandException {
        if (doesTileExist(uuid)) {
            MainActivity.client.getTileManager().removeTile(uuid).await();
            MainActivity.appendToUI(getString(R.string.band_done), "Style.CONFIRM");
        }
    }

    private void setSwitches() {
        messageSwitch.setChecked(message);
        calculatorSwitch.setChecked(calculator);
        musicSwitch.setChecked(music);
        cameraSwitch.setChecked(camera);
        barcodeSwitch.setChecked(barcode);
        flashlightSwitch.setChecked(flashlight);
    }

    private boolean addTile() throws Exception {
        new doesTileExistTask().execute(tileId);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap tileIcon = BitmapFactory.decodeResource(getActivity().getResources(), R.raw.tile_icon_large, options);
        Bitmap badgeIcon = BitmapFactory.decodeResource(getActivity().getResources(), R.raw.tile_icon_small,
                options);

        BandTile tile = new BandTile.Builder(tileId, getString(R.string.message_tile), tileIcon)
                .setTileSmallIcon(badgeIcon).addPageLayout(createLayout()).build();
        MainActivity.appendToUI(getString(R.string.message_tile_adding), "Style.INFO");
        if (MainActivity.client.getTileManager().addTile(getActivity(), tile).await()) {
            MainActivity.appendToUI(getString(R.string.message_tile_added), "Style.CONFIRM");
            return true;
        } else {
            MainActivity.appendToUI(getString(R.string.message_tile_not_added), "Style.ALERT");
            return false;
        }
    }

    private boolean addCalculatorTile() throws Exception {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap tileIcon = BitmapFactory.decodeResource(getActivity().getResources(), R.raw.calculator_tile_large, options);
        Bitmap badgeIcon = BitmapFactory.decodeResource(getActivity().getResources(), R.raw.calculator_tile_small,
                options);

        BandTile tile = new BandTile.Builder(calculatorTileId, getString(R.string.calculator_tile), tileIcon)
                .setTileSmallIcon(badgeIcon).addPageLayout(createCalculator1Layout())
                .addPageLayout(createCalculator2Layout()).addPageLayout(createCalculator3Layout()).build();
        MainActivity.appendToUI(getString(R.string.calculator_tile_adding), "Style.INFO");
        if (MainActivity.client.getTileManager().addTile(getActivity(), tile).await()) {
            MainActivity.appendToUI(getString(R.string.calculator_tile_added), "Style.CONFIRM");
            return true;
        } else {
            MainActivity.appendToUI(getString(R.string.calculator_tile_not_added), "Style.ALERT");
            return false;
        }
    }

    private boolean addMusicTile() throws Exception {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap tileIcon = BitmapFactory.decodeResource(getActivity().getResources(), R.raw.music_tile_large, options);
        Bitmap badgeIcon = BitmapFactory.decodeResource(getActivity().getResources(), R.raw.music_tile_small,
                options);

        BandTile tile = new BandTile.Builder(musicTileId, getString(R.string.music_tile), tileIcon)
                .setTileSmallIcon(badgeIcon).addPageLayout(createMusic3Layout())
                .addPageLayout(createMusic2Layout()).addPageLayout(createMusic1Layout()).build();
        MainActivity.appendToUI(getString(R.string.music_tile_adding), "Style.INFO");
        if (MainActivity.client.getTileManager().addTile(getActivity(), tile).await()) {
            MainActivity.appendToUI(getString(R.string.music_tile_added), "Style.CONFIRM");
            return true;
        } else {
            MainActivity.appendToUI(getString(R.string.music_tile_not_added), "Style.ALERT");
            return false;
        }
    }

    private void sendMessage() throws BandIOException {
        MainActivity.client.getNotificationManager().sendMessage(tileId, title.getText().toString(),
                body.getText().toString(), new Date(), MessageFlags.SHOW_DIALOG);
    }

    private boolean doesTileExist(UUID uuid) throws InterruptedException, BandException {
        List<BandTile> tiles = MainActivity.client.getTileManager().getTiles().await();
        for (BandTile tile : tiles) {
            if (tile.getTileId().equals(uuid)) {
                return true;
            }
        }
        return false;
    }

    private PageLayout createLayout() {
        return new PageLayout(new FlowPanel(15, 0, 260, 105, FlowPanelOrientation.VERTICAL,
                new PageElement[0]).addElements(new TextBlock(0, 0, 0, 0, TextBlockFont.SMALL)
                .setAutoWidthEnabled(true).setMargins(0, 0, 0, 0).setId(12)));
    }

    private PageLayout createCalculator1Layout() {
        return new PageLayout(new FlowPanel(0, 0, 320, 105, FlowPanelOrientation.VERTICAL)
                .addElements(new FlowPanel(0, 0, 320, 50, FlowPanelOrientation.HORIZONTAL)
                        .addElements(new TextButton(0, 0, 50, 50).setMargins(0, 5, 0, 0)
                                .setId(100).setPressedColor(-1))
                        .addElements(new TextButton(0, 0, 50, 50).setMargins(0, 5, 0, 0)
                                .setId(101).setPressedColor(-1))
                        .addElements(new TextButton(0, 0, 50, 50).setMargins(0, 5, 0, 0)
                                .setId(102).setPressedColor(-1))
                        .addElements(new TextButton(0, 0, 50, 50).setMargins(0, 5, 0, 0)
                                .setId(103).setPressedColor(-1))
                        .addElements(new TextButton(0, 0, 50, 50).setMargins(0, 5, 0, 0)
                                .setId(104).setPressedColor(-1)))

                .addElements(new FlowPanel(5, 110, 320, 105, FlowPanelOrientation.HORIZONTAL)
                        .addElements(new TextButton(0, 0, 50, 50).setMargins(0, 5, 0, 0)
                                .setId(105).setPressedColor(-1))
                        .addElements(new TextButton(0, 0, 50, 50).setMargins(0, 5, 0, 0)
                                .setId(106).setPressedColor(-1))
                        .addElements(new TextButton(0, 0, 50, 50).setMargins(0, 5, 0, 0)
                                .setId(107).setPressedColor(-1))
                        .addElements(new TextButton(0, 0, 50, 50).setMargins(0, 5, 0, 0)
                                .setId(108).setPressedColor(-1))
                        .addElements(new TextButton(0, 0, 50, 50).setMargins(0, 5, 0, 0)
                                .setId(109).setPressedColor(-1))));
    }

    private PageLayout createCalculator2Layout() {
        return new PageLayout(new FlowPanel(0, 0, 320, 105, FlowPanelOrientation.VERTICAL)
                .addElements(new FlowPanel(0, 0, 320, 50, FlowPanelOrientation.HORIZONTAL)
                        .addElements(new TextButton(0, 0, 50, 50).setMargins(0, 5, 0, 0)
                                .setId(120).setPressedColor(-1))
                        .addElements(new TextButton(0, 0, 50, 50).setMargins(0, 5, 0, 0)
                                .setId(121).setPressedColor(-1))
                        .addElements(new TextButton(0, 0, 50, 50).setMargins(0, 5, 0, 0)
                                .setId(122).setPressedColor(-1))
                        .addElements(new TextButton(0, 0, 50, 50).setMargins(0, 5, 0, 0)
                                .setId(123).setPressedColor(-1))
                        .addElements(new TextButton(0, 0, 50, 50).setMargins(0, 5, 0, 0)
                                .setId(124).setPressedColor(-1)))

                .addElements(new FlowPanel(5, 110, 320, 105, FlowPanelOrientation.HORIZONTAL)
                        .addElements(new TextButton(0, 0, 50, 50).setMargins(0, 5, 0, 0)
                                .setId(125).setPressedColor(-1))
                        .addElements(new TextButton(0, 0, 50, 50).setMargins(0, 5, 0, 0)
                                .setId(126).setPressedColor(-1))
                        .addElements(new TextButton(0, 0, 50, 50).setMargins(0, 5, 0, 0)
                                .setId(127).setPressedColor(-1))
                        .addElements(new TextButton(0, 0, 50, 50).setMargins(0, 5, 0, 0)
                                .setId(128).setPressedColor(-1))
                        .addElements(new TextButton(0, 0, 50, 50).setMargins(0, 5, 0, 0)
                                .setId(129).setPressedColor(-1))));
    }

    private PageLayout createCalculator3Layout() {
        return new PageLayout(new FlowPanel(15, 0, 260, 105, FlowPanelOrientation.VERTICAL,
                new PageElement[0]).addElements(new TextBlock(0, 0, 0, 0, TextBlockFont.SMALL)
                .setAutoWidthEnabled(true).setMargins(0, 0, 0, 0).setId(150)));
    }

    private void updateCalculatorPages() throws BandIOException {
        MainActivity.client.getTileManager().setPages(this.calculatorTileId,
                new PageData(calculatorPageId1, 0)
                        .update(new TextButtonData(100, "0"))
                        .update(new TextButtonData(101, "1"))
                        .update(new TextButtonData(102, "2"))
                        .update(new TextButtonData(103, "3"))
                        .update(new TextButtonData(104, "4"))
                        .update(new TextButtonData(105, "5"))
                        .update(new TextButtonData(106, "6"))
                        .update(new TextButtonData(107, "7"))
                        .update(new TextButtonData(108, "8"))
                        .update(new TextButtonData(109, "9")),
                new PageData(calculatorPageId2, 1)
                        .update(new TextButtonData(120, "+"))
                        .update(new TextButtonData(121, "-"))
                        .update(new TextButtonData(122, "*"))
                        .update(new TextButtonData(123, "/"))
                        .update(new TextButtonData(124, "="))
                        .update(new TextButtonData(125, "("))
                        .update(new TextButtonData(126, ")"))
                        .update(new TextButtonData(127, "^"))
                        .update(new TextButtonData(128, "."))
                        .update(new TextButtonData(129, "=")));
    }

    private PageLayout createMusic1Layout() {
        return new PageLayout(new FlowPanel(15, 0, 260, 105, FlowPanelOrientation.VERTICAL,
                new PageElement[0]).addElements(new TextButton(0, 5, 210, 45)
                .setMargins(0, 5, 0, 0).setId(12).setPressedColor(-1))
                .addElements(new TextButton(0, 0, 210, 45).setMargins(0, 5, 0, 0)
                        .setId(22).setPressedColor(-1)));
    }

    private PageLayout createMusic2Layout() {
        return new PageLayout(new FlowPanel(15, 0, 260, 105, FlowPanelOrientation.VERTICAL,
                new PageElement[0]).addElements(new TextButton(0, 5, 210, 45)
                .setMargins(0, 5, 0, 0).setId(11).setPressedColor(-1))
                .addElements(new TextButton(0, 0, 210, 45).setMargins(0, 5, 0, 0)
                        .setId(21).setPressedColor(-1)));
    }

    private PageLayout createMusic3Layout() {
        return new PageLayout(new FlowPanel(15, 0, 260, 105, FlowPanelOrientation.VERTICAL,
                new PageElement[0]).addElements(new TextButton(0, 5, 210, 45)
                .setMargins(0, 5, 0, 0).setId(31).setPressedColor(-1))
                .addElements(new TextButton(0, 0, 210, 45).setMargins(0, 5, 0, 0)
                        .setId(32).setPressedColor(-1)));
    }

    private void updatePages() throws BandIOException {
        MainActivity.client.getTileManager().setPages(this.musicTileId,
                new PageData(pageId1, 0).update(new TextButtonData(31, "Volume Up"))
                        .update(new TextButtonData(32, "Volume Down")),
                new PageData(pageId2, 1).update(new TextButtonData(11, "Play / Pause"))
                        .update(new TextButtonData(21, "Previous")),
                new PageData(pageId3, 2).update(new TextButtonData(12, "Play / Pause"))
                        .update(new TextButtonData(22, "Next")));
    }

    private PageLayout createCamera1Layout() {
        return new PageLayout(new FilledPanel(0, 0, 260, 105,
                new PageElement[0]).addElements(new TextButton(0, 0, 210, 105)
                .setMargins(0, 0, 0, 0).setId(91).setPressedColor(-1)));
    }

    private PageLayout createCamera2Layout() {
        return new PageLayout(new FilledPanel(0, 0, 260, 105,
                new PageElement[0]).addElements(new TextButton(0, 0, 210, 105)
                .setMargins(0, 0, 0, 0).setId(92).setPressedColor(-1)));
    }

    private PageLayout createCamera3Layout() {
        return new PageLayout(new FilledPanel(0, 0, 260, 105,
                new PageElement[0]).addElements(new TextButton(0, 0, 210, 105)
                .setMargins(0, 0, 0, 0).setId(93).setPressedColor(-1)));
    }

    private void updateCameraPages() throws BandIOException {
        MainActivity.client.getTileManager().setPages(this.cameraTileId,
                new PageData(cameraPageId1, 0).update(new TextButtonData(93, "Flash Mode")),
                new PageData(cameraPageId2, 1).update(new TextButtonData(92, "Switch Camera")),
                new PageData(cameraPageId3, 2).update(new TextButtonData(91, "Capture")));
    }

    private boolean addCameraTile() throws Exception {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap tileIcon = BitmapFactory.decodeResource(getActivity().getResources(), R.raw.camera_tile_large, options);
        Bitmap badgeIcon = BitmapFactory.decodeResource(getActivity().getResources(), R.raw.camera_tile_small,
                options);

        BandTile tile = new BandTile.Builder(cameraTileId, getString(R.string.camera_tile), tileIcon)
                .setTileSmallIcon(badgeIcon).addPageLayout(createCamera3Layout())
                .addPageLayout(createCamera2Layout()).addPageLayout(createCamera1Layout()).build();
        MainActivity.appendToUI(getString(R.string.camera_tile_adding), "Style.INFO");
        if (MainActivity.client.getTileManager().addTile(getActivity(), tile).await()) {
            MainActivity.appendToUI(getString(R.string.camera_tile_added), "Style.CONFIRM");
            return true;
        } else {
            MainActivity.appendToUI(getString(R.string.camera_tile_not_added), "Style.ALERT");
            return false;
        }
    }

    private boolean addBarcodeTile() throws Exception {
        new doesTileExistTask().execute(barcodeTileId);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap tileIcon = BitmapFactory.decodeResource(getActivity().getResources(), R.raw.barcode_tile_large, options);
        Bitmap badgeIcon = BitmapFactory.decodeResource(getActivity().getResources(), R.raw.barcode_tile_small, options);

        SharedPreferences sharedPreferences = getContext().getSharedPreferences("MyPrefs", 0);
        BandTile tile = new BandTile.Builder(barcodeTileId, getString(R.string.barcode_tile), tileIcon)
                .setTileSmallIcon(badgeIcon).setPageLayouts(
                        createBarcodeLayout(sharedPreferences.getInt("type5", 1) == 1 ? BarcodeType.CODE39 : BarcodeType.PDF417),
                        createBarcodeLayout(sharedPreferences.getInt("type4", 2) == 1 ? BarcodeType.CODE39 : BarcodeType.PDF417),
                        createBarcodeLayout(sharedPreferences.getInt("type3", 1) == 1 ? BarcodeType.CODE39 : BarcodeType.PDF417),
                        createBarcodeLayout(sharedPreferences.getInt("type2", 2) == 1 ? BarcodeType.CODE39 : BarcodeType.PDF417),
                        createBarcodeLayout(sharedPreferences.getInt("type1", 1) == 1 ? BarcodeType.CODE39 : BarcodeType.PDF417)).build();
        MainActivity.appendToUI(getString(R.string.barcode_tile_adding), "Style.INFO");
        if (MainActivity.client.getTileManager().addTile(getActivity(), tile).await()) {
            MainActivity.appendToUI(getString(R.string.barcode_tile_added), "Style.CONFIRM");
            return true;
        } else {
            MainActivity.appendToUI(getString(R.string.barcode_tile_not_added), "Style.ALERT");
            return false;
        }
    }

    private PageLayout createBarcodeLayout(BarcodeType type) {
        return new PageLayout(
                new FlowPanel(15, 0, 245, 105, FlowPanelOrientation.VERTICAL)
                        .addElements(new Barcode(0, 0, 221, 70, type)
                                .setId(11).setMargins(3, 0, 0, 0))
                        .addElements(new TextBlock(0, 0, 230, 30, TextBlockFont.SMALL, 0)
                                .setId(21).setColor(Color.RED)));
    }

    private void updateBarcodePages() throws BandIOException {
        final SharedPreferences sharedPreferences = getContext().getSharedPreferences("MyPrefs", 0);
        final String no1 = sharedPreferences.getString("no1", barcode39);
        final String no2 = sharedPreferences.getString("no2", barcode417);
        final String no3 = sharedPreferences.getString("no3", barcode39);
        final String no4 = sharedPreferences.getString("no4", barcode417);
        final String no5 = sharedPreferences.getString("no5", barcode39);
        MainActivity.client.getTileManager().setPages(barcodeTileId,
                new PageData(barcodeId1, 0)
                        .update(new BarcodeData(11, no5, (sharedPreferences.getInt("type5", 1) == 1) ? BarcodeType.CODE39 : BarcodeType.PDF417))
                        .update(new TextBlockData(21, no5)),
                new PageData(barcodeId2, 1)
                        .update(new BarcodeData(11, no4,
                                (sharedPreferences.getInt("type4", 2) == 1) ? BarcodeType.CODE39 : BarcodeType.PDF417))
                        .update(new TextBlockData(21, no4)),
                new PageData(barcodeId3, 2)
                        .update(new BarcodeData(11, no3,
                                (sharedPreferences.getInt("type3", 1) == 1) ? BarcodeType.CODE39 : BarcodeType.PDF417))
                        .update(new TextBlockData(21, no3)),
                new PageData(barcodeId4, 3)
                        .update(new BarcodeData(11, no2,
                                (sharedPreferences.getInt("type2", 2) == 1) ? BarcodeType.CODE39 : BarcodeType.PDF417))
                        .update(new TextBlockData(21, no2)),
                new PageData(barcodeId5, 4)
                        .update(new BarcodeData(11, no1,
                                (sharedPreferences.getInt("type1", 1) == 1) ? BarcodeType.CODE39 : BarcodeType.PDF417))
                        .update(new TextBlockData(21, no1)));
    }

    private PageLayout createFlashlight1Layout() {
        return new PageLayout(
                new FilledPanel(0, 0, 2500, 2500).setBackgroundColor(Color.WHITE));
    }

    private void updateFlashlightPages() throws BandIOException {
        MainActivity.client.getTileManager().setPages(flashlightTileId,
                new PageData(flashlightPageId1, 0)
                        .update(new FilledButtonData(12, Color.WHITE)),
                new PageData(flashlightPageId2, 1)
                        .update(new FilledButtonData(12, Color.WHITE)));
    }

    private boolean addFlashlightTile() throws Exception {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap tileIcon = BitmapFactory.decodeResource(getActivity().getResources(), R.raw.flashlight_tile_large, options);
        Bitmap badgeIcon = BitmapFactory.decodeResource(getActivity().getResources(), R.raw.flashlight_tile_small,
                options);

        BandTile tile = new BandTile.Builder(flashlightTileId, getString(R.string.flashlight_tile), tileIcon)
                .setTileSmallIcon(badgeIcon).addPageLayout(createFlashlight1Layout())
                .addPageLayout(createFlashlight1Layout()).build();
        MainActivity.appendToUI(getString(R.string.flashlight_tile_adding), "Style.INFO");
        if (MainActivity.client.getTileManager().addTile(getActivity(), tile).await()) {
            MainActivity.appendToUI(getString(R.string.flashlight_tile_added), "Style.CONFIRM");
            return true;
        } else {
            MainActivity.appendToUI(getString(R.string.flashlight_tile_not_added), "Style.ALERT");
            return false;
        }
    }

    private class removeTask extends AsyncTask<UUID, Void, Void> {
        @Override
        protected Void doInBackground(UUID... params) {
            try {
                if (doesTileExist(params[0])) {
                    MainActivity.client.getTileManager().removeTile(params[0]).await();
                    MainActivity.appendToUI(getString(R.string.band_done), "Style.CONFIRM");
                }
            } catch (Exception e) {
                //
            }
            return null;
        }
    }

    private class doesTileExistTask extends AsyncTask<UUID, Void, Boolean> {
        @Override
        protected Boolean doInBackground(UUID... params) {
            try {
                List<BandTile> tiles = MainActivity.client.getTileManager().getTiles().await();
                for (BandTile tile : tiles) {
                    MainActivity.appendToUI(getString(R.string.band_grabbing_info), "Style.INFO");
                    if (tile.getTileId().equals(params[0])) {
                        MainActivity.appendToUI(getString(R.string.band_tile_added), "Style.CONFIRM");
                        return true;
                    }
                }
            } catch (Exception e) {
                //
            }
            return false;
        }
    }

    private class task extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                if (BandUtils.getConnectedBandClient()) {
                    try {
                        MainActivity.appendToUI(getString(R.string.band_grabbing_info), "Style.INFO");

                        message = doesTileExist(tileId);
                        calculator = doesTileExist(calculatorTileId);
                        music = doesTileExist(musicTileId);
                        camera = doesTileExist(cameraTileId);
                        barcode = doesTileExist(barcodeTileId);
                        flashlight = doesTileExist(flashlightTileId);

                        MainActivity.appendToUI(getString(R.string.band_done), "Style.CONFIRM");
                    } catch (Exception e) {
                        //
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setSwitches();
                        }
                    });
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

    private class smsTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                if (BandUtils.getConnectedBandClient()) {
                    if (doesTileExist(tileId)) {
                        sendMessage();
                    } else {
                        if (addTile()) {
                            sendMessage();
                        }
                    }
                } else {
                    MainActivity.appendToUI(getString(R.string.band_not_found), "Style.ALERT");
                    return false;
                }
            } catch (BandException e) {
                BandUtils.handleBandException(e);
                return false;
            } catch (Exception e) {
                MainActivity.appendToUI(e.getMessage(), "Style.ALERT");
                return false;
            }

            return true;
        }
    }

    private class calculatorTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                if (BandUtils.getConnectedBandClient()) {
                    if (doesTileExist(calculatorTileId)) {
                        removeTile(calculatorTileId);
                    } else {
                        addCalculatorTile();
                        updateCalculatorPages();
                    }
                }
            } catch (BandException e) {
                BandUtils.handleBandException(e);
            } catch (Exception e) {
                MainActivity.appendToUI(e.getMessage(), "Style.ALERT");
            }
            return null;
        }
    }

    private class musicTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                if (BandUtils.getConnectedBandClient()) {
                    if (doesTileExist(musicTileId)) {
                        removeTile(musicTileId);
                    } else {
                        addMusicTile();
                        updatePages();
                    }
                }
            } catch (BandException e) {
                BandUtils.handleBandException(e);
            } catch (Exception e) {
                MainActivity.appendToUI(e.getMessage(), "Style.ALERT");
            }
            return null;
        }
    }

    private class cameraTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                if (BandUtils.getConnectedBandClient()) {
                    if (doesTileExist(cameraTileId)) {
                        removeTile(cameraTileId);
                    } else {
                        addCameraTile();
                        updateCameraPages();
                    }
                }
            } catch (BandException e) {
                BandUtils.handleBandException(e);
            } catch (Exception e) {
                MainActivity.appendToUI(e.getMessage(), "Style.ALERT");
            }
            return null;
        }
    }

    private class flashlightTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                if (BandUtils.getConnectedBandClient()) {
                    if (doesTileExist(flashlightTileId)) {
                        removeTile(flashlightTileId);
                    } else {
                        addFlashlightTile();
                        updateFlashlightPages();
                    }
                }
            } catch (BandException e) {
                BandUtils.handleBandException(e);
            } catch (Exception e) {
                MainActivity.appendToUI(e.getMessage(), "Style.ALERT");
            }
            return null;
        }
    }

    private class barcodeTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                if (BandUtils.getConnectedBandClient()) {
                    if (doesTileExist(barcodeTileId)) {
                        updateBarcodePages();
                        MainActivity.appendToUI(getString(R.string.band_done), "Style.CONFIRM");
                    } else {
                        addBarcodeTile();
                        updateBarcodePages();
                    }
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