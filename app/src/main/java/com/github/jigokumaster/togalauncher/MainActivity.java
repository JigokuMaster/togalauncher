package com.github.jigokumaster.togalauncher;
 
import android.app.Activity;
import android.os.Bundle;
import java.io.File;
import android.content.res.Resources;
import java.io.FileInputStream;
import java.io.InputStream;
import android.util.Log;
import java.io.FileOutputStream;
import android.widget.Toast;
import android.os.AsyncTask;
import android.content.Intent;
import android.widget.TextView;
import android.view.View.OnLongClickListener;
import android.view.View;
import android.content.Context;
import android.content.SharedPreferences;
import android.system.Os;
import android.view.MenuItem;
import android.view.Menu;
import android.content.pm.PackageManager;

public class MainActivity extends Activity
{ 


    
  
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		showAppInfo();
    }

	
	
	void showAppInfo()
	{
		setContentView(R.layout.activity_main);
		TextView mTextView = (TextView)findViewById(R.id.info_tv);
		StringBuilder sb = new StringBuilder("toga launcher is an application to test simple toga scripts\n\n");
		sb.append("toga launcher uses Python-for-Android built by beeware team \n\n");
		sb.append("link: https://github.com/beeware/Python-Android-support/releases/tag/3.9-b3\n\n");
		sb.append("Author: JigokuMaster (2024)\n\n");
		sb.append("github: https://github.com/JigokuMaster");
		mTextView.setText(sb);
	}

	public void openScriptEditor(View btn)
	{
		startActivity(new Intent(this, ScriptEditorActivity.class));
		finish();
	}
	

	
} 


