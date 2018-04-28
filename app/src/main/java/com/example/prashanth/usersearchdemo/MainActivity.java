package com.example.prashanth.usersearchdemo;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.prashanth.usersearchdemo.rest.BaseJsonHttpCallBack;
import com.example.prashanth.usersearchdemo.rest.HttpCall;
import com.example.prashanth.usersearchdemo.rest.ServiceGenerator;
import com.example.prashanth.usersearchdemo.threading.MainExecutor;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    public static final String LOG_TAG = "MainActivity";
//    ProgressDialog progressDialog;
    ArrayList<Pojo> names = new ArrayList<>();
    private RecyclerView.LayoutManager layoutManager;
    Adapter adapter;

    @BindView(R.id.search)
    EditText search;
    @BindView(R.id.rView)
    RecyclerView rView;
    @BindView(R.id.pBar)
    ProgressBar pBar;

    String str;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        adapter = new Adapter(names,MainActivity.this, cityCallback);
        layoutManager = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.VERTICAL, false);
        rView.setLayoutManager(layoutManager);
        rView.setAdapter(adapter);

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                str = s.toString();
                callServerApi(s.toString());
            }
        });

        rView.setOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

            }
        });
    }

    private void callServerApi(String q) {
        pBar.setVisibility(View.VISIBLE);
        rView.setVisibility(View.GONE);
        RestInterface mygateService = ServiceGenerator.createService(
                RestInterface.class, "https://api.github.com");

        HttpCall<JSONObject> httpCall = mygateService.hitApi(q,"followers","100");
        BaseJsonHttpCallBack callBack = new BaseJsonHttpCallBack() {
            @Override
            protected void onSuccess(final JSONObject result, long currentServerTime) {
                Log.d(LOG_TAG, "callServerApi: onSuccess ");
                MainExecutor.getInstance().executeInMainThread(new Runnable() {
                    @Override
                    public void run() {
                        pBar.setVisibility(View.GONE);
                        rView.setVisibility(View.VISIBLE);

                        ParentPojo parentPojo = null;
                        ArrayList<Pojo> memberList = null;
                        if(result != null) {
                            Gson gson = new Gson();
                            parentPojo = gson.fromJson(result.toString(), ParentPojo.class);
                            memberList = parentPojo.getList();

                            if(memberList == null){
                                parentPojo.setList(new ArrayList<Pojo>());
                            } else {
                                if(memberList.size() > 0){
                                    for (int i = memberList.size() - 1; i>=0; i--) {
                                        Pojo member = memberList.get(i);
                                        if(member == null){
                                            memberList.remove(i);
                                        }
                                    }
                                }
                            }

                        }

                        //TODO
                        names.clear();
                        names.addAll(memberList);
                        adapter.notifyDataSetChanged();
                        rView.setVisibility(View.VISIBLE);

                    }
                });
            }

            @Override
            protected void onError(final String message, JSONObject jsonResponse) {
                Log.d(LOG_TAG, "callServerApi: onError " + message);
                MainExecutor.getInstance().executeInMainThread(new Runnable() {
                    @Override
                    public void run() {
                        pBar.setVisibility(View.GONE);
                        rView.setVisibility(View.GONE);
                        Toast.makeText(MainActivity.this,"Sorry, Failed",Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            protected void onEmptyData(final JSONObject jsonResponse) {
                Log.d(LOG_TAG, "callServerApi: onEmptyData ");
                MainExecutor.getInstance().executeInMainThread(new Runnable() {
                    @Override
                    public void run() {
                        pBar.setVisibility(View.GONE);
                        rView.setVisibility(View.GONE);
                        Toast.makeText(MainActivity.this,"Sorry, Failed",Toast.LENGTH_LONG).show();
                    }
                });
            }
        };
        httpCall.enqueue(callBack);
    }

    private Adapter.AdapterCallback cityCallback = new Adapter.AdapterCallback() {
        @Override
        public void onClick(final int position) {
            if(position == 0){
                return;
            }

        }
    };

}
