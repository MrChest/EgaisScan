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
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chestnovv.myapplication.db.AppDatabase;
import com.example.chestnovv.myapplication.db.BodyWithTovar;
import com.example.chestnovv.myapplication.db.Document;
import com.example.chestnovv.myapplication.db.Mark;
import com.example.chestnovv.myapplication.db.MarkBalance;
import com.example.chestnovv.myapplication.db.Tovar;
import com.example.chestnovv.myapplication.repository.Resource;
import com.example.chestnovv.myapplication.utils.DialogMessage;
import com.example.chestnovv.myapplication.utils.ScanBroadcastReceiver;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import static android.support.v7.widget.DividerItemDecoration.VERTICAL;
import static com.example.chestnovv.myapplication.repository.Status.LOADING;

public class MarkActivity extends AppCompatActivity implements ScanBroadcastReceiver.ScanBroadcastListener {
    RecyclerView mRecyclerView;
    MarkAdapter mAdapter;

    public static final String EXTRA_NUMBER_DOC = "extraNumberDoc";
    public static final String EXTRA_TOVAR_ID = "extraTovarId";

    private String mNumberDoc;
    private String mTovarId;

    private TextView mId;
    private TextView mDescription;
    private TextView mCount;

    private AppDatabase mDb;
    private MarkViewModel mViewModel;
    private ProgressBar mLoadingIndicator;

    private BroadcastReceiver mScanReceiver;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tovar_layout);

        mRecyclerView = findViewById(R.id.recyclerViewMark);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mAdapter = new MarkAdapter(this);
        mRecyclerView.setAdapter(mAdapter);

        DividerItemDecoration decoration = new DividerItemDecoration(getApplicationContext(), VERTICAL);
        mRecyclerView.addItemDecoration(decoration);
        mLoadingIndicator = findViewById(R.id.pb_loading_indicator);

        mId = findViewById(R.id.tovar_id);
        mDescription = findViewById(R.id.description);
        mCount = findViewById(R.id.countStamp);

        mDb = AppDatabase.getInstance(this);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(EXTRA_NUMBER_DOC) && intent.hasExtra(EXTRA_TOVAR_ID)) {
            mNumberDoc = intent.getStringExtra(EXTRA_NUMBER_DOC);
            mTovarId = intent.getStringExtra(EXTRA_TOVAR_ID);
            setupViewModel();
            mViewModel.setDocumentId(mNumberDoc);
        }

        mScanReceiver = new ScanBroadcastReceiver(MarkActivity.this);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                String id = String.valueOf(viewHolder.itemView.getTag());
                mViewModel.remoteMarkById(mNumberDoc, id);

            }
        }).attachToRecyclerView(mRecyclerView);
    }

    private void setupViewModel() {
        mViewModel = ViewModelProviders.of(this, new MarkViewModelFactory(mDb, mNumberDoc, mTovarId)).get(MarkViewModel.class);
        mViewModel.getMarks().observe(this, new Observer<List<Mark>>() {
            @Override
            public void onChanged(@Nullable List<Mark> marks) {
                mAdapter.setMark(marks);
                if (marks!=null) {
                    int sum = 0;
                    for (Mark item: marks){
                        sum +=item.getCount();
                    }
                    mCount.setText(String.valueOf(sum));
                }
            }
        });

        mViewModel.getTovar().observe(this, new Observer<Tovar>() {
            @Override
            public void onChanged(@Nullable Tovar tovar) {
                mId.setText(tovar.getId());
                mDescription.setText(tovar.getDescription());
            }

        });

        mViewModel.getmDocument().observe(this, new Observer<Document>() {
            @Override
            public void onChanged(@Nullable Document document) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyy", Locale.getDefault());
                setTitle("№"+document.numberInovice+" от "+dateFormat.format(document.getDate()));
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

        mViewModel.getmDeleteMark().observe(this, new Observer<Resource<Boolean>>() {
            @Override
            public void onChanged(@Nullable Resource<Boolean> booleanResource) {
                switch (booleanResource.status){
                    case LOADING:
                        showProgressBar();
                        break;
                    case SUCCESS:
                        hideProgressBar();
                        break;
                    case ERROR:
                        hideProgressBar();
                        DialogMessage message = DialogMessage.newInstance("Error: " + booleanResource.message);
                        message.show(getSupportFragmentManager(), "message");
                        break;
                }
            }
        });
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

    public void showProgressBar(){
        mLoadingIndicator.setVisibility(View.VISIBLE);
    }

    public void hideProgressBar(){
        mLoadingIndicator.setVisibility(View.INVISIBLE);
    }

    @Override
    public void receiverBroadcastScan(String barcode) {
        mViewModel.saveMark(mNumberDoc, mTovarId, barcode);
    }
}
