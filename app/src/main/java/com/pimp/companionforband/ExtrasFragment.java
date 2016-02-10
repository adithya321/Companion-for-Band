package com.pimp.companionforband;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

import com.microsoft.band.BandClient;
import com.microsoft.band.BandClientManager;
import com.microsoft.band.BandException;
import com.microsoft.band.BandIOException;
import com.microsoft.band.BandInfo;
import com.microsoft.band.ConnectionState;
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

import java.util.Date;
import java.util.List;
import java.util.UUID;

import de.keyboardsurfer.android.widget.crouton.Configuration;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class ExtrasFragment extends Fragment {
    private static final UUID pageId1 = UUID.fromString("29411705-106f-48bc-a671-c6d7cb3e759a");
    private static final UUID pageId2 = UUID.fromString("3f34d8b4-e697-4b4b-89df-823cef78b744");
    private static final UUID pageId3 = UUID.fromString("134055f9-e786-47e9-a0ff-c12cce0b4f96");
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
    Switch musicSwitch, cameraSwitch, messageSwitch, barcodeSwitch, flashlightSwitch;
    boolean message = false, music = false, camera = false, barcode = false, flashlight = false;
    private BandClient client = null;
    private UUID tileId = UUID.fromString("3263f46a-38be-4229-afa1-85d4399b7798");
    private UUID musicTileId = UUID.fromString("48376b8f-1066-4b67-96c7-cecc9951bf3b");
    private UUID cameraTileId = UUID.fromString("006f9aa4-4092-44e2-b911-2f7262d198bc");
    private UUID barcodeTileId = UUID.fromString("23d392df-dd0c-4e93-8c0d-63dd7b02eb52");
    private UUID flashlightTileId = UUID.fromString("c2187a9c-db5c-4e73-9f9d-d742113f91e8");

    public static ExtrasFragment newInstance() {
        return new ExtrasFragment();
    }

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
                        name.setText(sharedPreferences.getString("name1", "CODE39"));
                        number.setText(sharedPreferences.getString("no1", "MK12345509"));
                        typeSpinner.setSelection(sharedPreferences.getInt("type1", 1));
                        break;
                    case 2:
                        name.setText(sharedPreferences.getString("name2", "PDF417"));
                        number.setText(sharedPreferences.getString("no2", "901234567890123456"));
                        typeSpinner.setSelection(sharedPreferences.getInt("type2", 2));
                        break;
                    case 3:
                        name.setText(sharedPreferences.getString("name3", "CODE39"));
                        number.setText(sharedPreferences.getString("no3", "MK12345509"));
                        typeSpinner.setSelection(sharedPreferences.getInt("type3", 1));
                        break;
                    case 4:
                        name.setText(sharedPreferences.getString("name4", "PDF417"));
                        number.setText(sharedPreferences.getString("no4", "901234567890123456"));
                        typeSpinner.setSelection(sharedPreferences.getInt("type4", 2));
                        break;
                    case 5:
                        name.setText(sharedPreferences.getString("name5", "CODE39"));
                        number.setText(sharedPreferences.getString("no5", "MK12345509"));
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
                    client.getNotificationManager().vibrate(vibrationType).await();
                } catch (BandException e) {
                    handleBandException(e);
                } catch (Exception e) {
                    appendToUI(e.getMessage(), Style.ALERT);
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
            client.getTileManager().removeTile(uuid).await();
            appendToUI(getString(R.string.band_done), Style.CONFIRM);
        }
    }

    private void setSwitches() {
        messageSwitch.setChecked(message);
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
        appendToUI(getString(R.string.message_tile_adding), Style.INFO);
        if (client.getTileManager().addTile(getActivity(), tile).await()) {
            appendToUI(getString(R.string.message_tile_added), Style.CONFIRM);
            return true;
        } else {
            appendToUI(getString(R.string.message_tile_not_added), Style.ALERT);
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
        appendToUI(getString(R.string.music_tile_adding), Style.INFO);
        if (client.getTileManager().addTile(getActivity(), tile).await()) {
            appendToUI(getString(R.string.music_tile_added), Style.CONFIRM);
            return true;
        } else {
            appendToUI(getString(R.string.music_tile_not_added), Style.ALERT);
            return false;
        }
    }

    private void sendMessage() throws BandIOException {
        client.getNotificationManager().sendMessage(tileId, title.getText().toString(),
                body.getText().toString(), new Date(), MessageFlags.SHOW_DIALOG);
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

    private boolean doesTileExist(UUID uuid) throws InterruptedException, BandException {
        List<BandTile> tiles = client.getTileManager().getTiles().await();
        for (BandTile tile : tiles) {
            appendToUI(getString(R.string.band_grabbing_info), Style.INFO);
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

    private void updatePages()
            throws BandIOException {
        this.client.getTileManager().setPages(this.musicTileId,
                new PageData(pageId1, 0).update(new TextButtonData(31, "Volume Up"))
                        .update(new TextButtonData(32, "Volume Down")),
                new PageData(pageId2, 1).update(new TextButtonData(11, "Play / Pause"))
                        .update(new TextButtonData(21, "Previous")),
                new PageData(pageId3, 2).update(new TextButtonData(12, "Play / Pause"))
                        .update(new TextButtonData(22, "Next")));
    }

    private void appendToUI(final String string, final Style style) {
        try {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Crouton.cancelAllCroutons();
                    View view = getView();
                    if (view != null) {
                        Crouton crouton = Crouton.makeText(getActivity(), string,
                                style, (ViewGroup) view.findViewById(R.id.extras));
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
        this.client.getTileManager().setPages(this.cameraTileId,
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
        appendToUI(getString(R.string.camera_tile_adding), Style.INFO);
        if (client.getTileManager().addTile(getActivity(), tile).await()) {
            appendToUI(getString(R.string.camera_tile_added), Style.CONFIRM);
            return true;
        } else {
            appendToUI(getString(R.string.camera_tile_not_added), Style.ALERT);
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
                        createBarcodeLayout(sharedPreferences.getInt("type1", 1) == 1 ? BarcodeType.CODE39 : BarcodeType.PDF417, 50, 51),
                        createBarcodeLayout(sharedPreferences.getInt("type2", 1) == 1 ? BarcodeType.CODE39 : BarcodeType.PDF417, 52, 53),
                        createBarcodeLayout(sharedPreferences.getInt("type3", 1) == 1 ? BarcodeType.CODE39 : BarcodeType.PDF417, 54, 55),
                        createBarcodeLayout(sharedPreferences.getInt("type4", 1) == 1 ? BarcodeType.CODE39 : BarcodeType.PDF417, 56, 57),
                        createBarcodeLayout(sharedPreferences.getInt("type5", 1) == 1 ? BarcodeType.CODE39 : BarcodeType.PDF417, 58, 59)).build();
        appendToUI(getString(R.string.barcode_tile_adding), Style.INFO);
        if (client.getTileManager().addTile(getActivity(), tile).await()) {
            appendToUI(getString(R.string.barcode_tile_added), Style.CONFIRM);
            return true;
        } else {
            appendToUI(getString(R.string.barcode_tile_not_added), Style.ALERT);
            return false;
        }
    }

    private PageLayout createBarcodeLayout(BarcodeType type, int id1, int id2) {
        return new PageLayout(
                new FlowPanel(15, 0, 245, 105, FlowPanelOrientation.VERTICAL)
                        .addElements(new Barcode(0, 0, 221, 70, type)
                                .setId(id1).setMargins(3, 0, 0, 0))
                        .addElements(new TextBlock(0, 0, 230, 30, TextBlockFont.SMALL, 0)
                                .setId(id2).setColor(Color.RED)));
    }

    private void updateBarcodePages() throws BandIOException {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("MyPrefs", 0);
        client.getTileManager().setPages(barcodeTileId,
                new PageData(barcodeId5, 4)
                        .update(new BarcodeData(59, sharedPreferences.getString("no5", "MK12345509"),
                                (sharedPreferences.getInt("type5", 1) == 1) ? BarcodeType.CODE39 : BarcodeType.PDF417))
                        .update(new TextBlockData(58, sharedPreferences.getString("no5", "MK12345509"))),
                new PageData(barcodeId4, 3)
                        .update(new BarcodeData(57, sharedPreferences.getString("no4", "901234567890123456"),
                                (sharedPreferences.getInt("type4", 1) == 1) ? BarcodeType.CODE39 : BarcodeType.PDF417))
                        .update(new TextBlockData(56, sharedPreferences.getString("no4", "901234567890123456"))),
                new PageData(barcodeId3, 2)
                        .update(new BarcodeData(55, sharedPreferences.getString("no3", "MK12345509"),
                                (sharedPreferences.getInt("type3", 1) == 1) ? BarcodeType.CODE39 : BarcodeType.PDF417))
                        .update(new TextBlockData(54, sharedPreferences.getString("no3", "MK12345509"))),
                new PageData(barcodeId2, 1)
                        .update(new BarcodeData(53, sharedPreferences.getString("no2", "901234567890123456"),
                                (sharedPreferences.getInt("type2", 1) == 1) ? BarcodeType.CODE39 : BarcodeType.PDF417))
                        .update(new TextBlockData(52, sharedPreferences.getString("no2", "901234567890123456"))),
                new PageData(barcodeId1, 0)
                        .update(new BarcodeData(51, sharedPreferences.getString("no1", "MK12345509"),
                                (sharedPreferences.getInt("type1", 1) == 1) ? BarcodeType.CODE39 : BarcodeType.PDF417))
                        .update(new TextBlockData(50, sharedPreferences.getString("no1", "MK12345509"))));
    }

    private PageLayout createFlashlight1Layout() {
        return new PageLayout(
                new FilledPanel(0, 0, 2500, 2500).setBackgroundColor(Color.WHITE));
    }

    private void updateFlashlightPages() throws BandIOException {
        this.client.getTileManager().setPages(flashlightTileId,
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
        appendToUI(getString(R.string.flashlight_tile_adding), Style.INFO);
        if (client.getTileManager().addTile(getActivity(), tile).await()) {
            appendToUI(getString(R.string.flashlight_tile_added), Style.CONFIRM);
            return true;
        } else {
            appendToUI(getString(R.string.flashlight_tile_not_added), Style.ALERT);
            return false;
        }
    }

    private class removeTask extends AsyncTask<UUID, Void, Void> {
        @Override
        protected Void doInBackground(UUID... params) {
            try {
                if (doesTileExist(params[0])) {
                    client.getTileManager().removeTile(params[0]).await();
                    appendToUI(getString(R.string.band_done), Style.CONFIRM);
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
                List<BandTile> tiles = client.getTileManager().getTiles().await();
                for (BandTile tile : tiles) {
                    appendToUI(getString(R.string.band_grabbing_info), Style.INFO);
                    if (tile.getTileId().equals(params[0])) {
                        appendToUI(getString(R.string.band_tile_added), Style.CONFIRM);
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
                if (getConnectedBandClient()) {
                    appendToUI(getString(R.string.band_connected), Style.CONFIRM);
                    try {
                        if (doesTileExist(tileId))
                            message = true;
                        if (doesTileExist(musicTileId))
                            music = true;
                        if (doesTileExist(cameraTileId))
                            camera = true;
                        if (doesTileExist(barcodeTileId))
                            barcode = true;
                        if (doesTileExist(flashlightTileId))
                            flashlight = true;

                        appendToUI(getString(R.string.band_done), Style.CONFIRM);
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

    private class smsTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                if (getConnectedBandClient()) {
                    if (doesTileExist(tileId)) {
                        sendMessage();
                    } else {
                        if (addTile()) {
                            sendMessage();
                        }
                    }
                } else {
                    appendToUI(getString(R.string.band_not_found), Style.ALERT);
                    return false;
                }
            } catch (BandException e) {
                handleBandException(e);
                return false;
            } catch (Exception e) {
                appendToUI(e.getMessage(), Style.ALERT);
                return false;
            }

            return true;
        }
    }

    private class musicTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                if (getConnectedBandClient()) {
                    if (doesTileExist(musicTileId)) {
                        removeTile(musicTileId);
                    } else {
                        addMusicTile();
                        updatePages();
                    }
                }
            } catch (BandException e) {
                handleBandException(e);
            } catch (Exception e) {
                appendToUI(e.getMessage(), Style.ALERT);
            }
            return null;
        }
    }

    private class cameraTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                if (getConnectedBandClient()) {
                    if (doesTileExist(cameraTileId)) {
                        removeTile(cameraTileId);
                    } else {
                        addCameraTile();
                        updateCameraPages();
                    }
                }
            } catch (BandException e) {
                handleBandException(e);
            } catch (Exception e) {
                appendToUI(e.getMessage(), Style.ALERT);
            }
            return null;
        }
    }

    private class flashlightTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                if (getConnectedBandClient()) {
                    if (doesTileExist(flashlightTileId)) {
                        removeTile(flashlightTileId);
                    } else {
                        addFlashlightTile();
                        updateFlashlightPages();
                    }
                }
            } catch (BandException e) {
                handleBandException(e);
            } catch (Exception e) {
                appendToUI(e.getMessage(), Style.ALERT);
            }
            return null;
        }
    }

    private class barcodeTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                if (getConnectedBandClient()) {
                    if (doesTileExist(barcodeTileId)) {
                        updateBarcodePages();
                        appendToUI(getString(R.string.band_done), Style.CONFIRM);
                    } else {
                        addBarcodeTile();
                        updateBarcodePages();
                    }
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