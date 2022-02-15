package com.example.chestnovv.myapplication;

import com.example.chestnovv.myapplication.utilbilling.IabBroadcastReceiver;
import com.example.chestnovv.myapplication.utilbilling.IabHelper;
import com.example.chestnovv.myapplication.utilbilling.IabHelper.IabAsyncInProgressException;
import com.example.chestnovv.myapplication.utilbilling.IabResult;
import com.example.chestnovv.myapplication.utilbilling.Inventory;
import com.example.chestnovv.myapplication.utilbilling.Purchase;

import android.app.AlertDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.chestnovv.myapplication.db.Document;
import com.example.chestnovv.myapplication.db.DocumentWithCount;
import com.example.chestnovv.myapplication.db.Mark;
import com.example.chestnovv.myapplication.db.MarkBalance;
import com.example.chestnovv.myapplication.repository.Resource;
import com.example.chestnovv.myapplication.service.*;
import com.example.chestnovv.myapplication.utils.ScanBroadcastReceiver;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static android.support.v7.widget.DividerItemDecoration.VERTICAL;

public class DocumentsActivity extends AppCompatActivity implements DocumentAdapter.ItemClickListener,
        DocumentAdapter.ItemContextClickListener,
        SharedPreferences.OnSharedPreferenceChangeListener,
        IabBroadcastReceiver.IabBroadcastListener,
        ScanBroadcastReceiver.ScanBroadcastListener{
    private static final String TAG = DocumentsActivity.class.getSimpleName();
    private RecyclerView mRecyclerView;
    private DocumentAdapter mAdapter;
    private MainViewModel mMainViewModel;
    private ProgressBar mLoadingIndicator;
    private BroadcastReceiver mScanReceiver;
    private static boolean PREFERENCES_HAVE_BEEN_UPDATED = false;

    // The helper object
    IabHelper mHelper;
    // Provides purchase notification while this app is running
    IabBroadcastReceiver mBroadcastReceiver;
    static final String SKU_PREMIUM = "premium";
    static final int RC_REQUEST = 10001;
    boolean mIsPremium = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.documents_main);

        //license();

        setTitle("Документы");

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerViewDocuments);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mAdapter = new DocumentAdapter(this, this, this);

        mRecyclerView.setAdapter(mAdapter);

        DividerItemDecoration decoration = new DividerItemDecoration(getApplicationContext(), VERTICAL);
        mRecyclerView.addItemDecoration(decoration);

        mLoadingIndicator = findViewById(R.id.pb_loading_indicator);

        mScanReceiver = new ScanBroadcastReceiver(DocumentsActivity.this);

        setupViewModel();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this.getApplication());
        preferences.registerOnSharedPreferenceChangeListener(this);

        String baseUrl = preferences.getString(this.getString(R.string.prefHttpService), "");
        String login = preferences.getString(this.getString(R.string.prefLogin), "");
        String password = preferences.getString(this.getString(R.string.prefPassword), "");

        if (!baseUrl.isEmpty()) mMainViewModel.setHostInterceptor(baseUrl);
        if (!login.isEmpty() && !password.isEmpty()) mMainViewModel.login(login, password);
    }

    private void setupViewModel() {
        mMainViewModel = ViewModelProviders.of(this).get(MainViewModel.class);

        mMainViewModel.getDocuments().observe(this, new Observer<List<DocumentWithCount>>() {
            @Override
            public void onChanged(@Nullable List<DocumentWithCount> documents) {
                Collections.sort(documents, new Comparator<DocumentWithCount>() {
                    @Override
                    public int compare(DocumentWithCount o1, DocumentWithCount o2) {
                        int dComp = o1.getDate().compareTo(o2.getDate());
                        if (dComp!=0){
                            return dComp;
                        }
                        else return o1.getNumber().compareTo(o2.getNumber());
                    }
                });
                mAdapter.setDocuments(documents);
            }
        });

        mMainViewModel.getRespApi().observe(this, new Observer<Resource<List<DocumentPOJO>>>() {
            @Override
            public void onChanged(@Nullable Resource<List<DocumentPOJO>> response) {
                switch (response.status){
                    case LOADING:
                        //showProgressBar();
                        setWaitScreen(true);
                        break;
                    case SUCCESS:
                        //hideProgressBar();
                        setWaitScreen(false);
                        break;
                    case ERROR:
                        //hideProgressBar();
                        setWaitScreen(false);
                        showMessage("Error: " + response.message);
                        break;
                }
                Log.d(TAG, "onChanged: yes");
            }
        });

        mMainViewModel.getRespApiSend().observe(this, new Observer<Resource<List<Mark>>>() {
            @Override
            public void onChanged(@Nullable Resource<List<Mark>> response) {
                switch (response.status){
                    case LOADING:
                        //showProgressBar();
                        setWaitScreen(true);
                        break;
                    case SUCCESS:
                        //hideProgressBar();
                        setWaitScreen(false);
                        break;
                    case ERROR:
                        //hideProgressBar();
                        setWaitScreen(false);
                        showMessage("Error: " + response.message);
                        break;
                }
                Log.d(TAG, "onChanged: yes");
            }
        });

        mMainViewModel.getDocumentByBarcode().observe(this, new Observer<Document>() {
            @Override
            public void onChanged(@Nullable Document document) {
                if (document!=null){
                    startBodyActivity(document.number);
                }
                else{
                    showMessage("Документ не найден");
                    mMainViewModel.setBarcode(null);
                }
            }
        });

        mMainViewModel.getRespApiBalance().observe(this, new Observer<Resource<List<MarkBalance>>>() {
            @Override
            public void onChanged(@Nullable Resource<List<MarkBalance>> response) {
                switch (response.status){
                    case LOADING:
                        //showProgressBar();
                        setWaitScreen(true);
                        break;
                    case SUCCESS:
                        //hideProgressBar();
                        setWaitScreen(false);
                        break;
                    case ERROR:
                        //hideProgressBar();
                        setWaitScreen(false);
                        showMessage("Error: " + response.message);
                        break;
                }
            }
        });
    }

    private void license(){
        String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAkXYql+Byo+LUaQzaaKIRCkLOjF95JX3XhjJv8NRJR/LrExuaJvEtnbFSxAdNqLN9Ku5PC6I3My5nhnxhge9qFICV591WlIGgXpxxySJf74EBHhzBiDfTv+2yXJ0HJXNr+AIYGFsfMQVO7yleOPkbFoEio4g44nZJNafg1HlkB43TM+GG4yJfdNyssxU3JTmgyzVAJg64wy12FEQemPAXdAd+GWD6NNwKRVoZqAe2dL9IpiSyJz32Yt/ZSB/pP4I6rjQ6/tByk40Aii6h36F17QQh7wgT/Rf6s+pjI3hUyKpkAa0GwCXapPhJ1NVnGEj2Mx5rbc0333d7eyNxqIkLyQIDAQAB";
        // Create the helper, passing it our context and the public key to verify signatures with
        Log.d(TAG, "Creating IAB helper.");
        mHelper = new IabHelper(this, base64EncodedPublicKey);

        // enable debug logging (for a production application, you should set this to false).
        mHelper.enableDebugLogging(true);

        // Start setup. This is asynchronous and the specified listener
        // will be called once setup completes.
        Log.d(TAG, "Starting setup.");
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                Log.d(TAG, "Setup finished.");

                if (!result.isSuccess()) {
                    // Oh noes, there was a problem.
                    complain("Problem setting up in-app billing: " + result);
                    return;
                }

                // Have we been disposed of in the meantime? If so, quit.
                if (mHelper == null) return;

                // Important: Dynamically register for broadcast messages about updated purchases.
                // We register the receiver here instead of as a <receiver> in the Manifest
                // because we always call getPurchases() at startup, so therefore we can ignore
                // any broadcasts sent while the app isn't running.
                // Note: registering this listener in an Activity is a bad idea, but is done here
                // because this is a SAMPLE. Regardless, the receiver must be registered after
                // IabHelper is setup, but before first call to getPurchases().
                mBroadcastReceiver = new IabBroadcastReceiver(DocumentsActivity.this);
                IntentFilter broadcastFilter = new IntentFilter(IabBroadcastReceiver.ACTION);
                registerReceiver(mBroadcastReceiver, broadcastFilter);

                // IAB is fully set up. Now, let's get an inventory of stuff we own.
                Log.d(TAG, "Setup successful. Querying inventory.");
                try {
                    mHelper.queryInventoryAsync(mGotInventoryListener);
                } catch (IabAsyncInProgressException e) {
                    complain("Error querying inventory. Another async operation in progress.");
                }
            }
        });
    }

    @Override
    public void onItemClickListener(String itemId) {
        startBodyActivity(itemId);
    }

    public void startBodyActivity(String number){
        Intent intent = new Intent(DocumentsActivity.this, BodyActivity.class);
        intent.putExtra(BodyActivity.EXTRA_BODY_ID, number);
        startActivity(intent);
    }

    public void onContextItemSelected(MenuItem item, final String number) {
        if (mIsPremium){
            mMainViewModel.send(number);
        }
        else {
            upgradeApp();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.document_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id==R.id.action_refresh){
            if (mIsPremium){
                refreshData();
            }
            else {
                upgradeApp();
            }

            return true;
        }
        else if (id==R.id.action_settings){
            Intent startSettingsActivity = new Intent(this, SettingsActivity.class);
            startActivity(startSettingsActivity);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void refreshData() {
        mMainViewModel.refresh();
    }

    public void showMessage(String message){
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    void setWaitScreen(boolean set) {
        mLoadingIndicator.setVisibility(set ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mScanReceiver != null){
            unregisterReceiver(mScanReceiver);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        IntentFilter filter = new IntentFilter();
        filter.addAction(ScanBroadcastReceiver.MESSAGEUROVO);
        filter.addAction(ScanBroadcastReceiver.MESSAGESCAN);
        registerReceiver(mScanReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
        // very important:
        if (mBroadcastReceiver != null) {
            unregisterReceiver(mBroadcastReceiver);
        }

        // very important:
        Log.d(TAG, "Destroying helper.");
        if (mHelper != null) {
            mHelper.disposeWhenFinished();
            mHelper = null;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (PREFERENCES_HAVE_BEEN_UPDATED) {
            Log.d(TAG, "onStart: preferences were updated");

            PREFERENCES_HAVE_BEEN_UPDATED = false;

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this.getApplication());

            String baseUrl = preferences.getString(this.getString(R.string.prefHttpService), "");
            String login = preferences.getString(this.getString(R.string.prefLogin), "");
            String password = preferences.getString(this.getString(R.string.prefPassword), "");

            mMainViewModel.setHostInterceptor(baseUrl);
            mMainViewModel.login(login, password);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Log.d(TAG, "Change pref");
        PREFERENCES_HAVE_BEEN_UPDATED = true;
    }

    // Callback for when a purchase is finished
    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            Log.d(TAG, "Purchase finished: " + result + ", purchase: " + purchase);

            // if we were disposed of in the meantime, quit.
            if (mHelper == null) return;

            if (result.isFailure()) {
                complain("Error purchasing: " + result);
                setWaitScreen(false);
                return;
            }
            if (!verifyDeveloperPayload(purchase)) {
                complain("Error purchasing. Authenticity verification failed.");
                setWaitScreen(false);
                return;
            }

            Log.d(TAG, "Purchase successful.");

            if (purchase.getSku().equals(SKU_PREMIUM)) {
                // bought the premium upgrade!
                Log.d(TAG, "Purchase is premium upgrade. Congratulating user.");
                alert("Thank you for upgrading to premium!");
                mIsPremium = true;
                updateUi();
                setWaitScreen(false);
            }
        }
    };

    // Listener that's called when we finish querying the items and subscriptions we own
    IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
            Log.d(TAG, "Query inventory finished.");

            // Have we been disposed of in the meantime? If so, quit.
            if (mHelper == null) return;

            // Is it a failure?
            if (result.isFailure()) {
                complain("Failed to query inventory: " + result);
                return;
            }

            Log.d(TAG, "Query inventory was successful.");

            /*
             * Check for items we own. Notice that for each purchase, we check
             * the developer payload to see if it's correct! See
             * verifyDeveloperPayload().
             */

            // Do we have the premium upgrade?
            Purchase premiumPurchase = inventory.getPurchase(SKU_PREMIUM);
            mIsPremium = (premiumPurchase != null && verifyDeveloperPayload(premiumPurchase));
            Log.d(TAG, "User is " + (mIsPremium ? "PREMIUM" : "NOT PREMIUM"));

            updateUi();
            setWaitScreen(false);
            Log.d(TAG, "Initial inventory query finished; enabling main UI.");
        }
    };

    @Override
    public void receivedBroadcast() {
        // Received a broadcast notification that the inventory of items has changed
        Log.d(TAG, "Received broadcast notification. Querying inventory.");
        try {
            mHelper.queryInventoryAsync(mGotInventoryListener);
        } catch (IabAsyncInProgressException e) {
            complain("Error querying inventory. Another async operation in progress.");
        }
    }

    @Override
    public void receiverBroadcastScan(String barcode) {
        mMainViewModel.setBarcode(barcode);
    }

    void complain(String message) {
        Log.e(TAG, "**** TrivialDrive Error: " + message);
        alert("Error: " + message);
    }

    void alert(String message) {
        AlertDialog.Builder bld = new AlertDialog.Builder(this);
        bld.setMessage(message);
        bld.setNeutralButton("OK", null);
        Log.d(TAG, "Showing alert dialog: " + message);
        bld.create().show();
    }

//    void saveData() {
//
//        /*
//         * WARNING: on a real application, we recommend you save data in a secure way to
//         * prevent tampering. For simplicity in this sample, we simply store the data using a
//         * SharedPreferences.
//         */
//
//        SharedPreferences.Editor spe = getPreferences(MODE_PRIVATE).edit();
//        spe.putInt("tank", mTank);
//        spe.apply();
//        Log.d(TAG, "Saved data: tank = " + String.valueOf(mTank));
//    }
//
//    void loadData() {
//        SharedPreferences sp = getPreferences(MODE_PRIVATE);
//        mTank = sp.getInt("tank", 2);
//        Log.d(TAG, "Loaded data: tank = " + String.valueOf(mTank));
//    }

    /** Verifies the developer payload of a purchase. */
    boolean verifyDeveloperPayload(Purchase p) {
        String payload = p.getDeveloperPayload();

        /*
         * TODO: verify that the developer payload of the purchase is correct. It will be
         * the same one that you sent when initiating the purchase.
         *
         * WARNING: Locally generating a random string when starting a purchase and
         * verifying it here might seem like a good approach, but this will fail in the
         * case where the user purchases an item on one device and then uses your app on
         * a different device, because on the other device you will not have access to the
         * random string you originally generated.
         *
         * So a good developer payload has these characteristics:
         *
         * 1. If two different users purchase an item, the payload is different between them,
         *    so that one user's purchase can't be replayed to another user.
         *
         * 2. The payload must be such that you can verify it even when the app wasn't the
         *    one who initiated the purchase flow (so that items purchased by the user on
         *    one device work on other devices owned by the user).
         *
         * Using your own server to store and verify developer payloads across app
         * installations is recommended.
         */

        return true;
    }

    // updates UI to reflect model
    public void updateUi() {
//        // update the car color to reflect premium status or lack thereof
//        ((ImageView)findViewById(R.id.free_or_premium)).setImageResource(mIsPremium ? R.drawable.premium : R.drawable.free);
//
//        // "Upgrade" button is only visible if the user is not premium
//        findViewById(R.id.upgrade_button).setVisibility(mIsPremium ? View.GONE : View.VISIBLE);

//        ImageView infiniteGasButton = (ImageView) findViewById(R.id.infinite_gas_button);
//        if (mSubscribedToInfiniteGas) {
//            // If subscription is active, show "Manage Infinite Gas"
//            infiniteGasButton.setImageResource(R.drawable.manage_infinite_gas);
//        } else {
//            // The user does not have infinite gas, show "Get Infinite Gas"
//            infiniteGasButton.setImageResource(R.drawable.get_infinite_gas);
//        }
//
//        // update gas gauge to reflect tank status
//        if (mSubscribedToInfiniteGas) {
//            ((ImageView)findViewById(R.id.gas_gauge)).setImageResource(R.drawable.gas_inf);
//        }
//        else {
//            int index = mTank >= TANK_RES_IDS.length ? TANK_RES_IDS.length - 1 : mTank;
//            ((ImageView)findViewById(R.id.gas_gauge)).setImageResource(TANK_RES_IDS[index]);
//        }
    }

    // User clicked the "Upgrade to Premium".
    public void upgradeApp() {
        Log.d(TAG, "Upgrade button clicked; launching purchase flow for upgrade.");
        setWaitScreen(true);

        /* TODO: for security, generate your payload here for verification. See the comments on
         *        verifyDeveloperPayload() for more info. Since this is a SAMPLE, we just use
         *        an empty string, but on a production app you should carefully generate this. */
        String payload = "";

        try {
            mHelper.launchPurchaseFlow(this, SKU_PREMIUM, RC_REQUEST,
                    mPurchaseFinishedListener, payload);
        } catch (IabAsyncInProgressException e) {
            complain("Error launching purchase flow. Another async operation in progress.");
            setWaitScreen(false);
        }
    }
}
