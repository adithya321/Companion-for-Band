package com.pimp.companionforband;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.pimp.companionforband.util.IabHelper;
import com.pimp.companionforband.util.IabResult;
import com.pimp.companionforband.util.Inventory;
import com.pimp.companionforband.util.Purchase;

import java.util.ArrayList;
import java.util.List;

public class DonateActivity extends AppCompatActivity {

    static final String TAG = "Companion for Band";
    IabHelper mHelper;
    String SKU_COKE = "cfb_coke", SKU_COFFEE = "cfb_coffee", SKU_BURGER = "cfb_burger",
            SKU_PIZZA = "cfb_pizza", SKU_MEAL = "cfb_meal";
    IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
        public void onConsumeFinished(Purchase purchase, IabResult result) {
            if (mHelper == null) return;

            if (result.isSuccess()) {
                complain("Thank You");
            } else {
                complain(result.toString());
            }
        }
    };
    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            if (mHelper == null) return;

            if (result.isFailure()) {
                complain("Error purchasing: " + result);
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
    IabHelper.QueryInventoryFinishedListener mQueryFinishedListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
            String[] title = {"Coke", "Coffee", "Burger", "Pizza", "Meal"};
            String[] price = {"Rs. 10.00", "Rs. 50.00", "Rs. 100.00", "Rs. 500.00", "Rs. 1,000.00"};

            ListView listView = (ListView) findViewById(R.id.list);
            listView.setAdapter(new CustomAdapter(DonateActivity.this, title, price));
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    switch (position) {
                        case 0:
                            mHelper.launchPurchaseFlow(DonateActivity.this, SKU_COKE, 1, mPurchaseFinishedListener, "payload");
                            break;
                        case 1:
                            mHelper.launchPurchaseFlow(DonateActivity.this, SKU_COFFEE, 1, mPurchaseFinishedListener, "payload");
                            break;
                        case 2:
                            mHelper.launchPurchaseFlow(DonateActivity.this, SKU_BURGER, 1, mPurchaseFinishedListener, "payload");
                            break;
                        case 3:
                            mHelper.launchPurchaseFlow(DonateActivity.this, SKU_PIZZA, 1, mPurchaseFinishedListener, "payload");
                            break;
                        case 4:
                            mHelper.launchPurchaseFlow(DonateActivity.this, SKU_MEAL, 1, mPurchaseFinishedListener, "payload");
                            break;
                    }
                }
            });
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donate);
        setTitle(getString(R.string.support));
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String base64EncodedPublicKey = getString(R.string.base64);

        mHelper = new IabHelper(this, base64EncodedPublicKey);
        mHelper.enableDebugLogging(false);

        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                if (!result.isSuccess()) {
                    Log.e("mHelper", "Problem setting up In-app Billing: " + result);
                    return;
                }
                if (mHelper == null) return;
                List<String> additionalSkuList = new ArrayList<>();
                additionalSkuList.add(SKU_COKE);
                additionalSkuList.add(SKU_COFFEE);
                additionalSkuList.add(SKU_BURGER);
                additionalSkuList.add(SKU_PIZZA);
                additionalSkuList.add(SKU_MEAL);
                mHelper.queryInventoryAsync(true, additionalSkuList, mQueryFinishedListener);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mHelper == null) return;

        if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        } else {
            Log.d(TAG, "onActivityResult handled by IABUtil.");
        }
    }

    void complain(String message) {
        alert(message);
    }

    void alert(String message) {
        AlertDialog.Builder bld = new AlertDialog.Builder(this);
        bld.setMessage(message);
        bld.setNeutralButton("OK", null);
        bld.create().show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mHelper != null) mHelper.dispose();
        mHelper = null;
    }
}