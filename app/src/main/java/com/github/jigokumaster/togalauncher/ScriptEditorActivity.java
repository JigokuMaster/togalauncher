package com.github.jigokumaster.togalauncher;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import java.io.File;
import java.io.FileWriter;
import android.widget.EditText;
import android.content.Intent;
import android.net.Uri;
import android.app.AlertDialog;
import java.io.FileReader;
import java.nio.CharBuffer;

public class ScriptEditorActivity extends Activity
{

	private EditText mScriptEditor;

	private File mScriptDir;

	private File mScripLogFile;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.editor_layout);
		mScriptEditor = (EditText)findViewById(R.id.script_editor);
		loadLastScript();
	}
	
	
	

	public void showScriptLog(View btn)
	{
		
		try
		{

		
			String logData = "";
			if(mScripLogFile.exists())
			{
				FileReader fr = new FileReader(mScripLogFile);
				CharBuffer data = CharBuffer.allocate((int)mScripLogFile.length());
				fr.read(data);
				logData = new String(data.array());
				fr.close();
			}
			
			showMsg(logData);


		}   
		catch (Exception e) 
		{
			e.printStackTrace();
			showMsg("Can't read script log \n\n" + e);
		}	
		
		
	}

	public void loadLastScript()
	{
		try
		{
			
		    mScriptDir = getExternalFilesDir(null);
			mScripLogFile = new File(mScriptDir, "log.txt");	
			File scriptFile = new File(mScriptDir, "tmp.py");
			if(scriptFile.exists() && scriptFile.length() > 0)
			{
				FileReader fr = new FileReader(scriptFile);
			
				CharBuffer data = CharBuffer.allocate((int)scriptFile.length());
				fr.read(data);
				mScriptEditor.setText(new String(data.array()));
				fr.close();
			}


		}   
		catch (Exception e) 
		{
		}
	}
    
	public void runScript(View btn)
	{
		try
		{
			File scriptFile = new File(mScriptDir, "tmp.py");
			FileWriter fw = new FileWriter(scriptFile);
			String data = mScriptEditor.getText().toString();
			fw.write(data);
			fw.close();
			Intent runIntent = new Intent(this, PythonActivity.class);
			runIntent.putExtra("fp", scriptFile.getAbsolutePath());
			runIntent.setAction(PythonActivity.ACTION_RUN_SCRIPT);
			startActivity(runIntent);

		}   
		catch (Exception e) 
		{
			e.printStackTrace();
			showMsg("Can't run the script \n\n" + e);
		}
	}
    
    
	void showMsg(String msg)
	{
		new AlertDialog.Builder(this).setMessage(msg).setCancelable(true).show();
	}
}
