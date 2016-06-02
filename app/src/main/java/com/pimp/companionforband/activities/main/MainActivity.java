package com.pimp.companionforband.activities.main;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.github.javiersantos.appupdater.AppUpdater;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.appinvite.AppInviteInvitation;
import com.microsoft.band.BandClient;
import com.microsoft.band.BandInfo;
import com.mikepenz.aboutlibraries.Libs;
import com.mikepenz.aboutlibraries.LibsBuilder;
import com.mikepenz.aboutlibraries.LibsConfiguration;
import com.mikepenz.aboutlibraries.entity.Library;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.pes.androidmaterialcolorpickerdialog.ColorPicker;
import com.pimp.companionforband.AnalyticsApplication;
import com.pimp.companionforband.R;
import com.pimp.companionforband.activities.cloud.CloudActivity;
import com.pimp.companionforband.activities.cloud.WebviewActivity;
import com.pimp.companionforband.activities.support.ChangelogActivity;
import com.pimp.companionforband.activities.support.GittyActivity;
import com.pimp.companionforband.utils.band.BandSensorData;
import com.pimp.companionforband.utils.band.BandUtils;
import com.pimp.companionforband.utils.iab.IabHelper;
import com.pimp.companionforband.utils.iab.IabResult;
import com.pimp.companionforband.utils.iab.Purchase;
import com.yalantis.ucrop.UCrop;

import net.rdrei.android.dirchooser.DirectoryChooserConfig;
import net.rdrei.android.dirchooser.DirectoryChooserFragment;

import java.io.File;

import angtrim.com.fivestarslibrary.FiveStarsDialog;
import angtrim.com.fivestarslibrary.NegativeReviewListener;
import angtrim.com.fivestarslibrary.ReviewListener;
import cat.ereza.customactivityoncrash.CustomActivityOnCrash;

public class MainActivity extends AppCompatActivity implements NegativeReviewListener, ReviewListener,
        DirectoryChooserFragment.OnFragmentInteractionListener {

    protected static final int REQUEST_STORAGE_READ_ACCESS_PERMISSION = 101;
    protected static final int REQUEST_STORAGE_WRITE_ACCESS_PERMISSION = 102;
    private static final int REQUEST_SELECT_PICTURE = 0x01;
    private static final String SAMPLE_CROPPED_IMAGE_NAME = "SampleCropImage.jpeg";

    public static Context sContext;
    public static Activity sActivity;

    public static BandClient client = null;
    public static boolean band2 = false;
    public static BandInfo[] devices;

    public static SharedPreferences sharedPreferences;
    public static SharedPreferences.Editor editor;

    public static BandSensorData bandSensorData;

    int base = 0;
    int r, g, b;
    Drawer result;
    int REQUEST_INVITE = 0;
    LibsConfiguration.LibsListener libsListener = new LibsConfiguration.LibsListener() {
        @Override
        public void onIconClicked(View v) {

        }

        @Override
        public boolean onLibraryAuthorClicked(View v, Library library) {
            return false;
        }

        @Override
        public boolean onLibraryContentClicked(View v, Library library) {
            return false;
        }

        @Override
        public boolean onLibraryBottomClicked(View v, Library library) {
            return false;
        }

        @Override
        public boolean onExtraClicked(View v, Libs.SpecialButton specialButton) {
            switch (v.getId()) {
                case R.id.aboutSpecial1:
                    startActivity(new Intent(MainActivity.this, ChangelogActivity.class));
                    return true;
            }
            return false;
        }

        @Override
        public boolean onIconLongClicked(View v) {
            return false;
        }

        @Override
        public boolean onLibraryAuthorLongClicked(View v, Library library) {
            return false;
        }

        @Override
        public boolean onLibraryContentLongClicked(View v, Library library) {
            return false;
        }

        @Override
        public boolean onLibraryBottomLongClicked(View v, Library library) {
            return false;
        }
    };
    IabHelper mHelper;
    String SKU_COKE = "cfb_coke", SKU_COFFEE = "cfb_coffee", SKU_BURGER = "cfb_burger",
            SKU_PIZZA = "cfb_pizza", SKU_MEAL = "cfb_meal";
    IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
        public void onConsumeFinished(Purchase purchase, IabResult result) {
            if (mHelper == null) return;

            AlertDialog.Builder bld = new AlertDialog.Builder(getApplicationContext());
            bld.setNeutralButton("OK", null);
            if (result.isSuccess()) {
                bld.setMessage("Thank You");
                bld.create().show();
            } else {
                bld.setMessage(result.toString());
                bld.create().show();
            }
        }
    };
    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            if (mHelper == null) return;

            if (result.isFailure()) {
                AlertDialog.Builder bld = new AlertDialog.Builder(getApplicationContext());
                bld.setMessage("Error purchasing : " + result);
                bld.setNeutralButton("OK", null);
                bld.create().show();
                return;
            }

            if (purchase.getSku().equals(SKU_COKE)
                    || purchase.getSku().equals(SKU_COFFEE)
                    || purchase.getSku().equals(SKU_BURGER)
                    || purchase.getSku().equals(SKU_PIZZA)
                    || purchase.getSku().equals(SKU_MEAL)) {
                mHelper.consumeAsync(purchase, mConsumeFinishedListener);
            }
        }
    };
    private Switch logSwitch, backgroundLogSwitch;
    private TextView logStatus, backgroundLogStatus;
    private Tracker mTracker;
    private AlertDialog mAlertDialog;
    private Uri mDestinationUri;
    private DirectoryChooserFragment mDialog;

    public static void appendToUI(String string, String style) {
        Snackbar snackbar = Snackbar.make(sActivity.findViewById(R.id.main_content), string, Snackbar.LENGTH_SHORT);
        View view = snackbar.getView();
        switch (style) {
            case "Style.CONFIRM":
                view.setBackgroundColor(sContext.getResources().getColor(R.color.style_confirm));
                break;
            case "Style.INFO":
                view.setBackgroundColor(sContext.getResources().getColor(R.color.style_info));
                break;
            case "Style.ALERT":
                view.setBackgroundColor(sContext.getResources().getColor(R.color.style_alert));
                break;
        }
        snackbar.show();
    }

    @Override
    public void onNegativeReview(int stars) {
        StringBuilder emailBuilder = new StringBuilder();
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("mailto:pimplay69@gmail.com"));
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.feedback) + " : " + getResources().getString(R.string.app_name));

        emailBuilder.append("\n \n \nOS Version: ").append(System.getProperty("os.version")).append("(").append(Build.VERSION.INCREMENTAL).append(")");
        emailBuilder.append("\nOS API Level: ").append(Build.VERSION.SDK_INT);
        emailBuilder.append("\nDevice: ").append(Build.DEVICE);
        emailBuilder.append("\nManufacturer: ").append(Build.MANUFACTURER);
        emailBuilder.append("\nModel (and Product): ").append(Build.MODEL).append(" (").append(Build.PRODUCT).append(")");
        PackageInfo appInfo = null;
        try {
            appInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        assert appInfo != null;
        emailBuilder.append("\nApp Version Name: ").append(appInfo.versionName);
        emailBuilder.append("\nApp Version Code: ").append(appInfo.versionCode);

        intent.putExtra(Intent.EXTRA_TEXT, emailBuilder.toString());
        startActivity(Intent.createChooser(intent, "Send via"));
    }

    @Override
    public void onReview(int stars) {
    }

    private boolean checkCameraPermission(boolean b) {
        int result;
        if (b)
            result = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA);
        else
            result = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        return result == PackageManager.PERMISSION_GRANTED;
    }

    private void requestCameraPermission(boolean b) {
        if (b) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.CAMERA)) {
                Toast.makeText(MainActivity.this, getString(R.string.camera_permission), Toast.LENGTH_LONG).show();
            } else {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.CAMERA}, 1);
            }
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                Toast.makeText(MainActivity.this, getString(R.string.storage_permission), Toast.LENGTH_LONG).show();
            } else {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this, getString(R.string.camera_granted),
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(MainActivity.this, getString(R.string.camera_denied),
                            Toast.LENGTH_LONG).show();
                }
                break;
            case 2:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this, getString(R.string.storage_granted),
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(MainActivity.this, getString(R.string.storage_denied),
                            Toast.LENGTH_LONG).show();
                }
                break;
        }
        switch (requestCode) {
            case REQUEST_STORAGE_READ_ACCESS_PERMISSION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pickFromGallery();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sContext = getApplicationContext();
        sActivity = this;

        sharedPreferences = getApplicationContext().getSharedPreferences("MyPrefs", 0);
        editor = sharedPreferences.edit();

        bandSensorData = new BandSensorData();

        mDestinationUri = Uri.fromFile(new File(getCacheDir(), SAMPLE_CROPPED_IMAGE_NAME));

        SectionsPagerAdapter mSectionsPagerAdapter;
        ViewPager mViewPager;

        if (!checkCameraPermission(true))
            requestCameraPermission(true);
        if (!checkCameraPermission(false))
            requestCameraPermission(false);

        FiveStarsDialog fiveStarsDialog = new FiveStarsDialog(this, "pimplay69@gmail.com");
        fiveStarsDialog.setTitle(getString(R.string.rate_dialog_title))
                .setRateText(getString(R.string.rate_dialog_text))
                .setForceMode(false)
                .setUpperBound(4)
                .setNegativeReviewListener(this)
                .showAfter(5);

        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        mTracker = application.getDefaultTracker();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setPageTransformer(true, new ZoomOutPageTransformer());
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                String name;
                switch (position) {
                    case 0:
                        name = "THEME";
                        break;
                    case 1:
                        name = "SENSORS";
                        break;
                    case 2:
                        name = "EXTRAS";
                        break;
                    default:
                        name = "CfB";
                }

                mTracker.setScreenName("Image~" + name);
                mTracker.send(new HitBuilders.ScreenViewBuilder().build());

                logSwitch = (Switch) findViewById(R.id.log_switch);
                backgroundLogSwitch = (Switch) findViewById(R.id.backlog_switch);
                logStatus = (TextView) findViewById(R.id.logStatus);
                backgroundLogStatus = (TextView) findViewById(R.id.backlogStatus);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        Drawable headerBackground = null;
        String encoded = sharedPreferences.getString("me_tile_image", "null");
        if (!encoded.equals("null")) {
            byte[] imageAsBytes = Base64.decode(encoded.getBytes(), Base64.DEFAULT);
            headerBackground = new BitmapDrawable(BitmapFactory
                    .decodeByteArray(imageAsBytes, 0, imageAsBytes.length));
        }

        AccountHeader accountHeader = new AccountHeaderBuilder().withActivity(this)
                .withCompactStyle(true)
                .withHeaderBackground((headerBackground == null) ?
                        getResources().getDrawable(R.drawable.pipboy) : headerBackground)
                .addProfiles(
                        new ProfileDrawerItem().withName(sharedPreferences.getString("device_name", "Companion For Band"))
                                .withEmail(sharedPreferences.getString("device_mac", "pimplay69@gmail.com"))
                                .withIcon(getResources().getDrawable(R.drawable.band))
                )
                .build();

        result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withActionBarDrawerToggleAnimated(true)
                .withAccountHeader(accountHeader)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(getString(R.string.drawer_cloud)).withIcon(GoogleMaterial.Icon.gmd_cloud).withIdentifier(1),
                        new DividerDrawerItem(),
                        new PrimaryDrawerItem().withName(getString(R.string.rate)).withIcon(GoogleMaterial.Icon.gmd_rate_review).withIdentifier(2),
                        new PrimaryDrawerItem().withName(getString(R.string.feedback)).withIcon(GoogleMaterial.Icon.gmd_feedback).withIdentifier(3),
                        new DividerDrawerItem(),
                        new PrimaryDrawerItem().withName(getString(R.string.share)).withIcon(GoogleMaterial.Icon.gmd_share).withIdentifier(4),
                        new PrimaryDrawerItem().withName(getString(R.string.other)).withIcon(GoogleMaterial.Icon.gmd_apps).withIdentifier(5),
                        new DividerDrawerItem(),
                        new PrimaryDrawerItem().withName(getString(R.string.report)).withIcon(GoogleMaterial.Icon.gmd_bug_report).withIdentifier(6),
                        new PrimaryDrawerItem().withName(getString(R.string.translate)).withIcon(GoogleMaterial.Icon.gmd_translate).withIdentifier(9),
                        new DividerDrawerItem(),
                        new PrimaryDrawerItem().withName(getString(R.string.support)).withIcon(GoogleMaterial.Icon.gmd_attach_money).withIdentifier(7),
                        new PrimaryDrawerItem().withName(getString(R.string.aboutLib)).withIcon(GoogleMaterial.Icon.gmd_info).withIdentifier(8)
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        boolean flag;
                        if (drawerItem != null) {
                            flag = true;
                            switch ((int) drawerItem.getIdentifier()) {
                                case 1:
                                    mTracker.send(new HitBuilders.EventBuilder()
                                            .setCategory("Action")
                                            .setAction("Cloud")
                                            .build());

                                    if (!sharedPreferences.getString("access_token", "hi").equals("hi"))
                                        startActivity(new Intent(getApplicationContext(), CloudActivity.class));
                                    else
                                        startActivity(new Intent(getApplicationContext(), WebviewActivity.class));
                                    break;
                                case 2:
                                    mTracker.send(new HitBuilders.EventBuilder()
                                            .setCategory("Action")
                                            .setAction("Rate and Review")
                                            .build());
                                    String MARKET_URL = "https://play.google.com/store/apps/details?id=";
                                    String PlayStoreListing = getPackageName();
                                    Intent rate = new Intent(Intent.ACTION_VIEW, Uri.parse(MARKET_URL + PlayStoreListing));
                                    startActivity(rate);
                                    break;
                                case 3:
                                    mTracker.send(new HitBuilders.EventBuilder()
                                            .setCategory("Action")
                                            .setAction("Feedback")
                                            .build());
                                    final StringBuilder emailBuilder = new StringBuilder();
                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("mailto:pimplay69@gmail.com"));
                                    intent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.app_name));

                                    emailBuilder.append("\n \n \nOS Version: ").append(System.getProperty("os.version")).append("(").append(Build.VERSION.INCREMENTAL).append(")");
                                    emailBuilder.append("\nOS API Level: ").append(Build.VERSION.SDK_INT);
                                    emailBuilder.append("\nDevice: ").append(Build.DEVICE);
                                    emailBuilder.append("\nManufacturer: ").append(Build.MANUFACTURER);
                                    emailBuilder.append("\nModel (and Product): ").append(Build.MODEL).append(" (").append(Build.PRODUCT).append(")");
                                    PackageInfo appInfo = null;
                                    try {
                                        appInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                                    } catch (PackageManager.NameNotFoundException e) {
                                        e.printStackTrace();
                                    }
                                    assert appInfo != null;
                                    emailBuilder.append("\nApp Version Name: ").append(appInfo.versionName);
                                    emailBuilder.append("\nApp Version Code: ").append(appInfo.versionCode);

                                    intent.putExtra(Intent.EXTRA_TEXT, emailBuilder.toString());
                                    startActivity(Intent.createChooser(intent, "Send via"));
                                    break;
                                case 4:
                                    mTracker.send(new HitBuilders.EventBuilder()
                                            .setCategory("Action")
                                            .setAction("Share")
                                            .build());
                                    Intent i = new AppInviteInvitation.IntentBuilder(getString(R.string.invitation_title))
                                            .setMessage(getString(R.string.invitation_message))
                                            .setCallToActionText(getString(R.string.invitation_cta))
                                            .build();
                                    startActivityForResult(i, REQUEST_INVITE);
                                    break;
                                case 5:
                                    mTracker.send(new HitBuilders.EventBuilder()
                                            .setCategory("Action")
                                            .setAction("Other Apps")
                                            .build());
                                    String PlayStoreDevAccount = "https://play.google.com/store/apps/developer?id=P.I.M.P.";
                                    Intent devPlay = new Intent(Intent.ACTION_VIEW, Uri.parse(PlayStoreDevAccount));
                                    startActivity(devPlay);
                                    break;
                                case 6:
                                    mTracker.send(new HitBuilders.EventBuilder()
                                            .setCategory("Action")
                                            .setAction("Report Bugs")
                                            .build());
                                    startActivity(new Intent(MainActivity.this, GittyActivity.class));
                                    break;
                                case 7:
                                    mTracker.send(new HitBuilders.EventBuilder()
                                            .setCategory("Action")
                                            .setAction("Donate")
                                            .build());

                                    String base64EncodedPublicKey = getString(R.string.base64);
                                    mHelper = new IabHelper(getApplicationContext(), base64EncodedPublicKey);
                                    mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
                                        public void onIabSetupFinished(IabResult result) {
                                            if (!result.isSuccess()) {
                                                Toast.makeText(MainActivity.this,
                                                        "Problem setting up In-app Billing: " + result,
                                                        Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });

                                    final Dialog dialog = new Dialog(MainActivity.this);
                                    dialog.setContentView(R.layout.dialog_donate);
                                    dialog.setTitle("Donate");

                                    String[] title = {"Coke", "Coffee", "Burger", "Pizza", "Meal"};
                                    String[] price = {"Rs. 10.00", "Rs. 50.00", "Rs. 100.00", "Rs. 500.00", "Rs. 1,000.00"};

                                    ListView listView = (ListView) dialog.findViewById(R.id.list);
                                    listView.setAdapter(new DonateListAdapter(MainActivity.this, title, price));
                                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                            switch (position) {
                                                case 0:
                                                    mHelper.launchPurchaseFlow(MainActivity.this, SKU_COKE, 1, mPurchaseFinishedListener, "payload");
                                                    break;
                                                case 1:
                                                    mHelper.launchPurchaseFlow(MainActivity.this, SKU_COFFEE, 1, mPurchaseFinishedListener, "payload");
                                                    break;
                                                case 2:
                                                    mHelper.launchPurchaseFlow(MainActivity.this, SKU_BURGER, 1, mPurchaseFinishedListener, "payload");
                                                    break;
                                                case 3:
                                                    mHelper.launchPurchaseFlow(MainActivity.this, SKU_PIZZA, 1, mPurchaseFinishedListener, "payload");
                                                    break;
                                                case 4:
                                                    mHelper.launchPurchaseFlow(MainActivity.this, SKU_MEAL, 1, mPurchaseFinishedListener, "payload");
                                                    break;
                                            }
                                        }
                                    });

                                    dialog.show();
                                    break;
                                case 8:
                                    mTracker.send(new HitBuilders.EventBuilder()
                                            .setCategory("Action")
                                            .setAction("About")
                                            .build());
                                    new LibsBuilder()
                                            .withLicenseShown(true)
                                            .withVersionShown(true)
                                            .withActivityStyle(Libs.ActivityStyle.DARK)
                                            .withAboutVersionShown(true)
                                            .withActivityTitle(getString(R.string.app_name))
                                            .withAboutIconShown(true)
                                            .withListener(libsListener)
                                            .start(MainActivity.this);
                                    break;
                                case 9:
                                    mTracker.send(new HitBuilders.EventBuilder()
                                            .setCategory("Action")
                                            .setAction("Translate")
                                            .build());
                                    Intent translate = new Intent(Intent.ACTION_VIEW,
                                            Uri.parse("https://poeditor.com/join/project/AZQxDV2440"));
                                    startActivity(translate);
                                    break;
                                default:
                                    break;
                            }
                        } else {
                            flag = false;
                        }
                        return flag;
                    }
                })
                .withSavedInstance(savedInstanceState)
                .build();

        AppUpdater appUpdater = new AppUpdater(this);
        appUpdater.start();

        final DirectoryChooserConfig config = DirectoryChooserConfig.builder()
                .allowNewDirectoryNameModification(true)
                .newDirectoryName("CfBCamera")
                .initialDirectory(sharedPreferences.getString("pic_location", "/storage/emulated/0/CompanionForBand/Camera"))
                .build();
        mDialog = DirectoryChooserFragment.newInstance(config);

        new BandUtils().execute();

        CustomActivityOnCrash.install(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_INVITE) {
            if (resultCode != -1) {
                Toast.makeText(MainActivity.this, getString(R.string.send_failed), Toast.LENGTH_LONG).show();
            }
        }
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_SELECT_PICTURE) {
                final Uri selectedUri = data.getData();
                if (selectedUri != null) {
                    startCropActivity(data.getData());
                } else {
                    Toast.makeText(MainActivity.this, R.string.toast_cannot_retrieve_selected_image, Toast.LENGTH_SHORT).show();
                }
            } else if (requestCode == UCrop.REQUEST_CROP) {
                handleCropResult(data);
            }
        }
        if (resultCode == UCrop.RESULT_ERROR) {
            handleCropError(data);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("CfB", "Setting screen name: " + "MAIN");
        mTracker.setScreenName("Image~" + "MAIN");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    public void ButtonClick(View view) {
        switch (view.getId()) {
            case R.id.base:
                pickColor("base", view.getId());
                break;
            case R.id.highlight:
                pickColor("highLight", view.getId());
                break;
            case R.id.lowlight:
                pickColor("lowLight", view.getId());
                break;
            case R.id.secondaryText:
                pickColor("secondaryText", view.getId());
                break;
            case R.id.highContrast:
                pickColor("highContrast", view.getId());
                break;
            case R.id.muted:
                pickColor("muted", view.getId());
                break;
        }
    }

    void pickColor(final String string, final int id) {
        base = sharedPreferences.getInt(string, -16777216);
        r = (base >> 16 & 0xFF);
        g = (base >> 8 & 0xFF);
        b = (base & 0xFF);
        final ColorPicker colorPicker = new ColorPicker(MainActivity.this, r, g, b);
        colorPicker.show();
        Button okColor = (Button) colorPicker.findViewById(R.id.okColorButton);
        okColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putInt(string, colorPicker.getColor());
                editor.apply();
                findViewById(id).setBackgroundColor(colorPicker.getColor());
                colorPicker.dismiss();
            }
        });
    }

    public void SwitchClick(View view) {
        switch (view.getId()) {
            case R.id.log_switch:
                setSwitch(logSwitch, logStatus, "log");
                if (logSwitch.isChecked())
                    backgroundLogSwitch.setVisibility(View.VISIBLE);
                else {
                    backgroundLogSwitch.setChecked(false);
                    setSwitch(backgroundLogSwitch, backgroundLogStatus, "backlog");
                    backgroundLogSwitch.setVisibility(View.GONE);
                }
                break;
            case R.id.backlog_switch:
                setSwitch(backgroundLogSwitch, backgroundLogStatus, "backlog");
                break;
        }
    }

    void setSwitch(Switch s, TextView textView, String string) {
        if (!s.isChecked()) {
            editor.putBoolean(string, false);
            editor.apply();
            textView.setVisibility(View.GONE);
        } else {
            editor.putBoolean(string, true);
            editor.apply();
            textView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        if (client != null) {
            try {
                client.disconnect().await();
            } catch (Exception e) {
                // Do nothing as this is happening during destroy
            }
        }
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAlertDialog != null && mAlertDialog.isShowing()) {
            mAlertDialog.dismiss();
        }
    }

    protected void requestPermission(final String permission, String rationale, final int requestCode) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
            showAlertDialog(getString(R.string.permission_title_rationale), rationale,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{permission}, requestCode);
                        }
                    }, getString(R.string.label_ok), null, getString(R.string.label_cancel));
        } else {
            ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
        }
    }

    protected void showAlertDialog(@Nullable String title, @Nullable String message,
                                   @Nullable DialogInterface.OnClickListener onPositiveButtonClickListener,
                                   @NonNull String positiveText,
                                   @Nullable DialogInterface.OnClickListener onNegativeButtonClickListener,
                                   @NonNull String negativeText) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(positiveText, onPositiveButtonClickListener);
        builder.setNegativeButton(negativeText, onNegativeButtonClickListener);
        mAlertDialog = builder.show();
    }

    private void pickFromGallery() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN // Permission was added in API Level 16
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE,
                    getString(R.string.permission_read_storage_rationale),
                    REQUEST_STORAGE_READ_ACCESS_PERMISSION);
        } else {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(Intent.createChooser(intent, getString(R.string.label_select_picture)), REQUEST_SELECT_PICTURE);
        }
    }

    private void startCropActivity(@NonNull Uri uri) {
        UCrop uCrop = UCrop.of(uri, mDestinationUri);
        if (band2) uCrop.withAspectRatio(310, 128);
        else uCrop.withAspectRatio(310, 102);
        uCrop.start(MainActivity.this);
    }

    private void handleCropResult(@NonNull Intent result) {
        final Uri resultUri = UCrop.getOutput(result);
        if (resultUri != null) {
            ((ImageView) findViewById(R.id.selected_me_tile_image_view)).setImageURI(resultUri);
        } else {
            Toast.makeText(MainActivity.this, R.string.toast_cannot_retrieve_cropped_image, Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    private void handleCropError(@NonNull Intent result) {
        final Throwable cropError = UCrop.getError(result);
        if (cropError != null) {
            Toast.makeText(MainActivity.this, cropError.getMessage(), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(MainActivity.this, R.string.toast_unexpected_error, Toast.LENGTH_SHORT).show();
        }
    }

    public void CropClick(View view) {
        pickFromGallery();
    }

    public void changePictureLocation(View view) {
        mDialog.show(getFragmentManager(), null);
    }

    @Override
    public void onSelectDirectory(@NonNull String path) {
        editor.putString("pic_location", path);
        editor.apply();
        mDialog.dismiss();
    }

    @Override
    public void onCancelChooser() {
        mDialog.dismiss();
    }
}