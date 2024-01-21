package com.github.jigokumaster.togalauncher;

import android.content.*;
import android.content.res.*;
import java.io.*;
import java.util.zip.*;
import android.util.*;
import android.os.*;

public class AssetsZipExtractor
{


	private final String TAG = "AssetsZipExtractor";
	private Context mContext;

	public AssetsZipExtractor(Context ctx)
	{
		mContext = ctx;
	}

	public boolean extractFromAssets(AssetManager assetsManager , String fp, String dest)
	{
		try
		{
			InputStream inStream = assetsManager.open(fp);
			return extractStream(inStream, dest);
		}
		catch (IOException e)
		{
			return false;
		}

	}

	public boolean extractFromRes(Resources res, int resId,  String dest)
	{
		InputStream inStream = res.openRawResource(resId);
		return extractStream(inStream, dest);
	}

	public boolean extractStream(InputStream inStream, String dest)
	{		
		try
		{


			ZipInputStream zin = new ZipInputStream(inStream);
			ZipEntry ze = null;
			byte[] buffer = new byte[1024*1024];
			while ((ze = zin.getNextEntry()) != null)
			{
				if(!extractEntry(zin, ze, dest, buffer))
				{
					return false;
				}
			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
		return true;
	}


	public boolean extractEntry(ZipInputStream zin, ZipEntry ze, String dest, byte[] buffer)
	{


		Log.v(TAG, "Unzipping " + ze.getName());
		File f = new File(dest, ze.getName());
		if (ze.isDirectory())
		{
			if(f.exists())
			{
				return true;
			}
			if(!f.mkdirs() )
			{
				return false;
			}
		}

		else
		{

			if (!f.exists())
			{
				try
				{
					f.createNewFile();
				}
				catch (Exception e)
				{

					Log.w(TAG, "Failed to create file " + f.getName());
					e.printStackTrace();
					return false;
				}

				try
				{
					FileOutputStream fout = new FileOutputStream(f);
					int count;
					while((count = zin.read(buffer)) != -1)
					{
						fout.write(buffer, 0, count);
					}
					zin.closeEntry();
					fout.close();

				}
				catch (Exception e)
				{
					Log.w(TAG, "Failed to extract file " + f.getName());
					e.printStackTrace();
				}
			}
		}

		return true;
	}
}

