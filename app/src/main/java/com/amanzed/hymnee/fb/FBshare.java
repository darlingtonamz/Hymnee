package com.amanzed.hymnee.fb;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import com.amanzed.hymnee.HymnListActivity;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.widget.FacebookDialog;
import com.facebook.widget.WebDialog;

public class FBshare {
	Activity activity;
	String name,  description, URL;
	public FBshare(Activity activity, String name, String description, String URL){
		this.activity = activity;
		this.name = name;
		this.description = description;
		this.URL = URL;
	}
	public void show(){
		if (FacebookDialog.canPresentShareDialog(activity,
				FacebookDialog.ShareDialogFeature.SHARE_DIALOG)) {
			    // Publish the post using the Share Dialog
			    FacebookDialog shareDialog = new FacebookDialog.ShareDialogBuilder(activity)
			            .setLink(URL==null ? "https://play.google.com/store/apps/details?id=com.amanzed.hymnee" : URL)
			            .setName(name==null ? "Hymnee" : name)
			            //.setCaption("Build great social apps and get more installs.")
			            .setDescription(description)
			            .setPicture("http://thelifted.org/hymn_icon.png")
			            .build();
			    HymnListActivity.uihelper.trackPendingDialogCall(shareDialog.present());
			} else {
			    // Fallback. For example, publish the post using the Feed Dialog
			    //publishFeedDialog();
			    Toast.makeText(activity, "You don't have facebook installed", Toast.LENGTH_SHORT).show();
			}
	}
	
	private void publishFeedDialog() {
	    Bundle params = new Bundle();
	    params.putString("name", "Facebook SDK for Android");
	    params.putString("caption", "Build great social apps and get more installs.");
	    params.putString("description", "The Facebook SDK for Android makes it easier and faster to develop Facebook integrated Android apps.");
	    params.putString("link", "https://developers.facebook.com/android");
	    params.putString("picture", "http://foofans.com/images/logo.jpg");

	    WebDialog feedDialog = (
	            new WebDialog.FeedDialogBuilder(activity,
	                    Session.getActiveSession(),
	                    params)).build();
	    feedDialog.show();
	}
}
