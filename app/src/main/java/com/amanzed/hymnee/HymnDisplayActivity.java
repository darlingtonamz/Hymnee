package com.amanzed.hymnee;

import cn.pedant.SweetAlert.SweetAlertDialog;

import com.amanzed.hymnee.db.HymnDBHelper;
import com.amanzed.hymnee.hymn.Hymn;
import com.amanzed.hymnee.hymn.Verse;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import android.support.v7.app.ActionBar;

public class HymnDisplayActivity extends AppCompatActivity {
	TextView tv, title, numb;
	private HymnDBHelper mydb;
	private AdView mAdView;
	String v ;
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.hymn_display_page);
		ActionBar bar = getSupportActionBar();
		Bundle b = getIntent().getExtras();
		Hymn hm = (Hymn) b.getSerializable("hymn_detail");
		
		tv = (TextView) findViewById(R.id.hymn_display_textview);
		//title = (TextView) findViewById(R.id.hymn_display_title);
		//numb = (TextView) findViewById(R.id.hymn_display_numb);
		
		//title.setText(hm.getTitle());
		bar.setTitle(hm.getTitle());
		//numb.setText(String.valueOf(hm.getNumb()));
		
		mydb = new HymnDBHelper(this);
		v = mydb.getVersesById(hm.getId());
		String out = "";
		try{
			JSONArray verses = new JSONArray(v);
			for (int i = 0; i < verses.length(); i++) {
				out += verses.getString(i)+"\n\n";
			}
		}catch (JSONException e){}


		/*for (Verse mv : v){
			out += mv.getBody()+"\n\n";
		}*/
		tv.setText(out);
		bar.setDisplayHomeAsUpEnabled(true);
		//mAdView = (AdView) findViewById(R.id.adView);
        //mAdView.setAdListener(new ToastAdListener(this));
        //mAdView.loadAd(new AdRequest.Builder().build());
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.hymn_display_page, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_share:
				//tv = (TextView) findViewById(R.id.contentText);
				Intent sendIntent = new Intent();
				sendIntent.setAction(Intent.ACTION_SEND);
				sendIntent.putExtra(Intent.EXTRA_TEXT, tv.getText() +
						"\nShared via Hymnee Android Hymnal App " +
						"\n\nDownload now: https://play.google.com/store/apps/details?id=com.amanzed.hymnee");
				sendIntent.setType("text/plain");
				startActivity(Intent.createChooser(sendIntent, "Share..."));	
				break;
			case android.R.id.home:
				this.finish();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	
}
