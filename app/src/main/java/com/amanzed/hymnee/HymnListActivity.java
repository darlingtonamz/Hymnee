package com.amanzed.hymnee;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.amanzed.hymnee.db.HymnDBHelper;
import com.amanzed.hymnee.fb.FBshare;
import com.amanzed.hymnee.google.ToastAdListener;
import com.amanzed.hymnee.hymn.Hymn;
import com.amanzed.hymnee.hymn.HymnListAdapter;
import com.amanzed.hymnee.source.GetHymnJSON;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.leo.simplearcloader.ArcConfiguration;
import com.leo.simplearcloader.SimpleArcDialog;
import com.leo.simplearcloader.SimpleArcLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import cn.pedant.SweetAlert.SweetAlertDialog;
import android.widget.AdapterView.OnItemClickListener;
import android.support.v7.app.ActionBar;

/**
 * Created by Amanze on 5/15/2016.
 */
public class HymnListActivity extends AppCompatActivity implements OnItemClickListener, TextWatcher {
    private HymnDBHelper mydb;
    private ArrayList<Hymn> hymnList, custom;
    private int hymns_num;
    private HymnListAdapter adapter;
    private Object server;
    private SweetAlertDialog pDialog;
    ListView hymnListView;
    private String shared = "";
    private SharedPreferences pref;
    public static UiLifecycleHelper uihelper;
    private AdView mAdView;
    EditText search;
    String denom, source;
    boolean isOnline;
    SimpleArcDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hymn_list_main);
        Bundle b = getIntent().getExtras();
        denom = b.getString("denom");
        isOnline = b.getBoolean("online");
        source = b.getString("source");
        ActionBar bar = getSupportActionBar();
        bar.setTitle(denom.toUpperCase());

        search = (EditText) findViewById(R.id.search_list_ET);
        search.addTextChangedListener(this);
        pref = getApplicationContext().getSharedPreferences("SOCIAL", Context.MODE_PRIVATE);
        shared = pref.getString("shared", "");


        mDialog = new SimpleArcDialog(this);
        //pDialog = new SweetAlertDialog(this, SweetAlertDialog.NORMAL_TYPE);//.PROGRESS_TYPE);


        uihelper =new UiLifecycleHelper(this,callback);
        uihelper.onCreate(savedInstanceState);
        mAdView = (AdView) findViewById(R.id.adView);
        mAdView.setAdListener(new ToastAdListener(this));
        mAdView.loadAd(new AdRequest.Builder().build());
        bar.setDisplayHomeAsUpEnabled(true);

        mydb = new HymnDBHelper(this);
        try {
            mydb.createDataBase();
        } catch (IOException e) {
            e.printStackTrace();
        }

        refreshDisplay();
    }

    private Session.StatusCallback callback =new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception)
        {		}
    };


    private void refreshDisplay() {
        hymnList = mydb.getAllHymns(denom);
        hymns_num = hymnList.size();

        Log.e("hymnee", "Denom: "+denom+" | Hymn_Size: "+hymnList.size());

        if (hymnList.size() > 700) {
            Collections.sort(hymnList, Hymn.HymnNumberComparator);
            custom = hymnList;
            adapter = new HymnListAdapter(this, hymnList);

            hymnListView = (ListView) findViewById(R.id.hymn_list);
            hymnListView.setAdapter(adapter);
            hymnListView.setOnItemClickListener(this);
        }else{
            loadDB();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        final Intent intent = new Intent(this, HymnDisplayActivity.class);
        Hymn hm = custom.get(arg2);
        Bundle b = new Bundle();
        b.putSerializable("hymn_detail", hm);
        intent.putExtras(b);

        if (shared.equals("otito")){
            startActivity(intent);
        } else {
            pDialog = new SweetAlertDialog(this, SweetAlertDialog.NORMAL_TYPE)
                    .setTitleText("Share Hymnee").setContentText("Help promote Hymnee")
                    .setConfirmText("OK")
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            FBshare fb = new FBshare(HymnListActivity.this,""
                                    , "Download the best Mobile-Hymn book online. Including free RCCG Hymnals", null);
                            fb.show();
                            savePreferences("shared", "otito");
                            pDialog.cancel();
                        }
                    })
                    .setCancelText("No, thanks")
                    .showCancelButton(true)
                    .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            startActivity(intent);
                            pDialog.cancel();
                        }
                    });
            pDialog.show();
        }
        shared = "otito";
    }

    protected void loadDB() {
        //hideMDialog();
        Log.d("hymnee", "loadDB: ");
        JSONObject obj = null;
        JSONArray response = null;

        if (isOnline)
            obj = GetHymnJSON.getOnline(this, source);
        else
            obj = GetHymnJSON.getOffline(this, source);

        try {
            response = obj.getJSONArray("hymns");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if ((response != null) && (hymns_num < response.length())) {
            //Log.e("hymnee", "Data: "+hymns_num+" | "+response.length());
            //hideMDialog();
            new ParseAsync().execute(response);
            //parseJsonFeed(response);
        } else if(response != null){
            Toast.makeText(HymnListActivity.this, "Hymn list is up to date", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(HymnListActivity.this, "Unable to get Hymns", Toast.LENGTH_SHORT).show();
        }

    }

    private boolean parseJsonFeed(JSONArray response) {
        Log.e("hymnee", "Data2: "+hymns_num+" | "+response.length());
        try {
            int n = response.length();
            if (hymns_num < n){
                mydb.deleteAll(denom);
                hymnList.clear();

                for (int i = 0; i < n; i++) {
                    Log.d("hymnee", i + " of " + n);
                    //pDialog.setTitleText(i + " of " + n);
                    JSONObject f = (JSONObject) response.get(i);

                    String verses = f.getJSONArray("verses").toString();
                    //ArrayList<Verse> verses = new ArrayList<Verse>();
                    /*for (int j = 0; j < vin.length(); j++){
                        Verse v = new Verse();
                        v.setBody(vin.get(j).toString());
                        v.setPosition(j);
                        verses.add(v);
                    }*/

                    int num = 0;
                    if (!(denom.equals("general")))
                        num = f.getInt("number");
                    else
                        num = i;

                    mydb.insertHymn(f.getString("title"), denom, num, "", verses);
                    mydb.close();
                }
                //Log.d("radio", "Outside loop");
            }

            return true;
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void hideMDialog() {
        if (mDialog.isShowing()) {
            mDialog.hide();
        } else{
            mDialog = new SimpleArcDialog(this);
            ArcConfiguration configuration = new ArcConfiguration(this);
            configuration.setLoaderStyle(SimpleArcLoader.STYLE.COMPLETE_ARC);
            configuration.setText("Installing Hymns...");
            mDialog.setConfiguration(configuration);
            mDialog.show();
        }
        /*setLoaderStyle(SimpleArcLoader.STYLE mLoaderStyle)
        setArcMargin(int mArcMargin)
        setArcWidthInPixel(int mStrokeWidth)
        setColors(int[] colors)
        setTypeFace(Typeface typeFace)
        setText(String mText)
        setTextColor(int mTextColor)
        setTextSize(int size)
        setAnimationSpeedWithIndex(int mAnimationIndex) Values to be passed SimpleArcLoader.SPEED_SLOW, SimpleArcLoader.SPEED_MEDIUM, SimpleArcLoader.SPEED_FAST*/
    }

    class ParseAsync extends AsyncTask<JSONArray, Void, Boolean> {
        SweetAlertDialog pDialog;
        protected void onPreExecute() {
            super.onPreExecute();
            hideMDialog();
            /*pDialog = new SweetAlertDialog(HymnListActivity.this, SweetAlertDialog.NORMAL_TYPE)//.PROGRESS_TYPE)
                    .setTitleText("Saving hymns");
            pDialog.show();
            pDialog.setCancelable(false);*/
            //pDialog.getProgressHelper().setBarColor(Color.GREEN);
        }
        protected Boolean doInBackground(JSONArray... response) {
            boolean b = false;
            //Log.e("hymnee", "Data: "+hymns_num+" | "+response[0].length());
            b = parseJsonFeed(response[0]);
            return b;
        }

        protected void onPostExecute(Boolean isSuccessful) {
            String msg;
            refreshDisplay();
            hideMDialog();
            /*if (isSuccessful)
                pDialog.setTitle("Done");
            else
                pDialog.setTitle("Failed");
            try {
                Thread.sleep(2000);
                pDialog.cancel();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/
        }
    }


    /**
     *  Method used to save Preferences */
    public  void savePreferences(String key, String value)
    {
        SharedPreferences.Editor editor = getSharedPreferences("SOCIAL", MODE_PRIVATE).edit();
        editor.putString(key, value);
        editor.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.hymn_list_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_search:
                if (search.isShown())
                    search.setVisibility(View.GONE);
                else
                    search.setVisibility(View.VISIBLE);
                break;
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        String query = s.toString().toLowerCase();
        int n = hymnList.size();
        custom = new ArrayList<Hymn>();
        for (int i = 0; i < n; i++) {
            String radioname = hymnList.get(i).getTitle().toLowerCase();
            if (radioname.contains(query)){
                custom.add(hymnList.get(i));
            }
        }
        adapter = new HymnListAdapter(this, custom);
        hymnListView.setAdapter(adapter);
    }

    @Override
    public void afterTextChanged(Editable arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                  int arg3) {
        // TODO Auto-generated method stub

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
