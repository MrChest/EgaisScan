package com.example.chestnovv.myapplication;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.chestnovv.myapplication.db.AppDatabase;
import com.example.chestnovv.myapplication.db.BodyWithTovar;
import com.example.chestnovv.myapplication.db.Document;
import com.example.chestnovv.myapplication.db.Mark;
import com.example.chestnovv.myapplication.db.MarkBalance;
import com.example.chestnovv.myapplication.repository.Resource;
import com.example.chestnovv.myapplication.utils.DialogMessage;
import com.example.chestnovv.myapplication.utils.ScanBroadcastReceiver;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import static android.support.v7.widget.DividerItemDecoration.VERTICAL;

public class BodyActivity extends AppCompatActivity implements BodyAdapter.ItemClickListener, ScanBroadcastReceiver.ScanBroadcastListener {

    private static final String TAG = DocumentsActivity.class.getSimpleName();
    private RecyclerView mRecyclerView;
    private BodyAdapter mAdapter;
    private AppDatabase mDb;
    public static final String EXTRA_BODY_ID = "extraBodyId";
    public String mBodyNumber;
    private BodyViewModel mViewModel;
    private ProgressBar mLoadingIndicator;
    private BroadcastReceiver mScanReceiver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.documents_main);


        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerViewDocuments);

        mLoadingIndicator = findViewById(R.id.pb_loading_indicator);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mAdapter = new BodyAdapter(this, this);

        mRecyclerView.setAdapter(mAdapter);

        DividerItemDecoration decoration = new DividerItemDecoration(getApplicationContext(), VERTICAL);
        mRecyclerView.addItemDecoration(decoration);

        mDb = AppDatabase.getInstance(this);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(EXTRA_BODY_ID)) {
            mBodyNumber = intent.getStringExtra(EXTRA_BODY_ID);
            setupViewModel();

            mViewModel.setDocumentId(mBodyNumber);
        }

        mScanReceiver = new ScanBroadcastReceiver(BodyActivity.this);
    }

    private void setupViewModel() {
        mViewModel = ViewModelProviders.of(this, new BodyViewModelFactory(mDb, mBodyNumber)).get(BodyViewModel.class);
        mViewModel.getBodyWithTovars().observe(this, new Observer<List<BodyWithTovar>>() {
            @Override
            public void onChanged(@Nullable List<BodyWithTovar> bodyWithTovars) {
                mAdapter.setBody(bodyWithTovars);
            }
        });

        mViewModel.getmDocument().observe(this, new Observer<Document>() {
            @Override
            public void onChanged(@Nullable Document document) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyy", Locale.getDefault());
                setTitle("№"+document.numberInovice+" от "+dateFormat.format(document.getDate()));
            }
        });

        mViewModel.getRespApiSend().observe(this, new Observer<Resource<List<Mark>>>() {
            @Override
            public void onChanged(@Nullable Resource<List<Mark>> response) {
                switch (response.status){
                    case LOADING:
                        showProgressBar();
                        break;
                    case SUCCESS:
                        hideProgressBar();
                        break;
                    case ERROR:
                        hideProgressBar();
                        showMessage("Error: " + response.message);
                        break;
                }
                Log.d(TAG, "onChanged: yes");
            }
        });

        mViewModel.getmMarkBalance().observe(this, new Observer<Resource<MarkBalance>>() {
            @Override
            public void onChanged(@Nullable Resource<MarkBalance> markBalanceResource) {
                switch (markBalanceResource.status){
                    case LOADING:
                        showProgressBar();
                        break;
                    case SUCCESS:
                        hideProgressBar();
                        if (markBalanceResource.data==null || markBalanceResource.data.getStamp().isEmpty()){
                            DialogMessage message = DialogMessage.newInstance("Марка не найдена, попробуйте обновить остатки");
                            message.show(getSupportFragmentManager(), "message");
                        }
                        break;
                    case ERROR:
                        hideProgressBar();
                        DialogMessage message = DialogMessage.newInstance("Error: " + markBalanceResource.message);
                        message.show(getSupportFragmentManager(), "message");
                        break;
                }
            }
        });
    }

    @Override
    public void onItemClickListener(String itemId) {
        Intent intent = new Intent(BodyActivity.this, MarkActivity.class);
        intent.putExtra(MarkActivity.EXTRA_TOVAR_ID, itemId);
        intent.putExtra(MarkActivity.EXTRA_NUMBER_DOC, mBodyNumber);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(this);
        inflater.inflate(R.menu.body_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id==R.id.action_send){
            mViewModel.send(mBodyNumber);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void showProgressBar(){
        mLoadingIndicator.setVisibility(View.VISIBLE);
    }

    public void hideProgressBar(){
        mLoadingIndicator.setVisibility(View.INVISIBLE);
    }

    public void showMessage(String message){
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
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
    public void receiverBroadcastScan(String barcode) {
        mViewModel.saveMark(mBodyNumber, barcode);
    }
}
