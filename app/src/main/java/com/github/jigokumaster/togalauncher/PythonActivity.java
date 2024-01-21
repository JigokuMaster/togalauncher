package com.github.jigokumaster.togalauncher;

import android.app.Activity;
import java.io.File;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Menu;
import android.widget.TextView;
import android.view.View;
import android.content.Intent;
import android.widget.Toast;
import android.os.AsyncTask;
import org.beeware.rubicon.Python;
import android.system.Os;
import android.util.Log;
import android.content.Context;
import android.content.pm.PackageManager;
import android.Manifest;
import android.os.Build;
import android.net.Uri;

public class PythonActivity extends Activity{
	

	private String TAG = "PythonActivity";
	
	public static final String ACTION_RUN_SCRIPT = "com.github.jigokumaster.togalauncher.ACTION_RUN_SCRIPT";

	public static PythonActivity singletonThis;

	private static IPythonApp pythonApp;

	private SharedPreferences mPythonPreferences;

	private File mPythonRuntimePath;

	public static final int PERMISSION_REQUEST_CODE = 200;
	

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		singletonThis = this;
		if(checkPermissions())
		{
			setupPythonRuntime();
		}
		else
		{
			requestPermissions();
		}
    }


	@Override
	protected void onResume() {
		super.onResume();
		if(pythonApp != null)
		{
			pythonApp.onResume();
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		if(pythonApp != null)
		{
			Python.stop();
			System.exit(0);
			
		}


	}



	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		switch(requestCode)
		{
			case PERMISSION_REQUEST_CODE:
				if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED)
				{
					showToast("no permissions has been granted");
				}
				else
				{
					setupPythonRuntime();
				}
				break;
		}
		
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
	}

	
    @SuppressWarnings("unused")
    public static void setPythonApp(IPythonApp app) {
        pythonApp = app;
    }
	
	
    public boolean checkPermissions() {
		
		String[] perms = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
		
		for(String perm: perms)
		{
            if(checkSelfPermission(perm) != PackageManager.PERMISSION_GRANTED)
			{
				return false;
			}
		}
		return true;
    }

	public void requestPermissions()
	{
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
		{
			String[] perms = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
			requestPermissions(perms, PERMISSION_REQUEST_CODE);
		}
			
    }

	public void startPythonInterpreter()
	{
		try
		{

			Intent mIntent = getIntent();
			if (!ACTION_RUN_SCRIPT.equals(mIntent.getAction())) 
			{
				finish();
				return;
			}

			String pythonScript = mIntent.getStringExtra("fp");
			File pythonScriptFile = new File(pythonScript);
			String pythonScriptPath = pythonScriptFile.getParent();
			String pythonHome = mPythonRuntimePath.getAbsolutePath();
			String pythonPath = String.format("%s:%s:%s", pythonHome, new File(pythonHome, "lib/python3.9").getAbsolutePath() , pythonScript) ;
			String rubiconPath = getApplicationInfo().nativeLibraryDir + "/librubicon.so" ;
			Os.setenv("PYTHONDONTWRITEBYTECODE","1", true);
			Os.setenv("ANDROID_PACKAGE_NAME", getPackageName(), true);
			Os.setenv("SCRIPT_FP", pythonScript, true);
			Os.setenv("SCRIPT_CWD", pythonScriptPath, true);
			if(Python.init(pythonHome, pythonPath, rubiconPath) != 0)
			{
				showToast("Unable to start python interpreter");
				return;
			}

			setTitle("Running Script: " +pythonScriptFile.getName());
			if(Python.run("toga_launcher", new String[0]) != 0)
			{

				Python.stop();
				System.exit(1);

			}
			else{

				if(pythonApp != null)
				{
					pythonApp.onCreate();
				}
			}




		}
		catch(Exception e)
		{
			e.printStackTrace();
			showToast(e.toString());
		}
	}

    void setupPythonRuntime()
	{
		final String resource = "python_runtime";
		mPythonRuntimePath = getFilesDir();
		mPythonPreferences = getSharedPreferences("python_runtime", Context.MODE_PRIVATE);
		AsyncTask extractorTask = new AsyncTask(){


			public void recursiveDelete(File f)
			{
				if (f.isDirectory())
				{
            		for (File r : f.listFiles())
					{
						recursiveDelete(r);
            		}
				}
				f.delete();
			}

			@Override
			protected Object doInBackground(Object[] p1)
			{
				try
				{

					recursiveDelete(mPythonRuntimePath);
            		mPythonRuntimePath.mkdirs();
					AssetsZipExtractor ex = new AssetsZipExtractor(PythonActivity.this);

					if(!ex.extractFromAssets(getAssets(), resource + ".mp3" , mPythonRuntimePath.getAbsolutePath()))
					{

						return false;
					}


				}
				catch(Exception e)
				{
					e.printStackTrace();
					return false;
				}
				return true;
			}

			@Override
			protected void onPostExecute(Object res)
			{
				boolean extracted = res;
				SharedPreferences.Editor mSharedPreferencesEditor = mPythonPreferences.edit();
				mSharedPreferencesEditor.putBoolean("installed", extracted);
				mSharedPreferencesEditor.commit();
				if(extracted)
				{
					
					startPythonInterpreter();
				}
				else
				{
					showToast("Could not extract PythonRuntime");

				}
			}

		};


        if (!mPythonPreferences.getBoolean("installed", false)) {
			showToast("Extracting Python Runtime ...");
            Log.v(TAG, "Extracting " + resource + " assets.");
			extractorTask.execute();
		}
		else
		{
			startPythonInterpreter();

		}

	}


    public void showToast(String msg)
	{
		Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();

    }

	
    
    
}
