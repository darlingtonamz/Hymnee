package com.amanzed.hymnee;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;

import com.amanzed.hymnee.hymn.Hymn;

public class MainActivity extends Activity implements OnClickListener {

    private ImageButton gen, rccg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gen = (ImageButton) findViewById(R.id.img_gen_logo);
        rccg = (ImageButton) findViewById(R.id.img_rccg_logo);
        gen.setOnClickListener(this);
        rccg.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(MainActivity.this, HymnListActivity.class);
        Bundle b = new Bundle();
        switch (v.getId()) {
            case R.id.img_gen_logo:
                b.putString("denom", "general");
                b.putBoolean("online", false);
                b.putString("source", "hymns");
                break;
            case R.id.img_rccg_logo:
                b.putString("denom", "rccg");
                b.putBoolean("online", false);
                b.putString("source", "rccg");
                break;
        }
        Log.d("hymnee", "Clicked: "+b.getString("denom")+" : "+v.getId()+" ("+R.id.img_gen_logo+")");
        intent.putExtras(b);
        MainActivity.this.startActivity(intent);
    }
}
