package com.example.blindindoornavigation;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import android.os.Environment;
import android.text.format.Time;

public class WriterUtility {

	//returns number of files in folder
	public static int writeStep(List<String> itemsToWrite) throws IOException
	{
		int n = 0; //n = the number of records (lines) created in a file
		try{
			File directory = Environment.getExternalStorageDirectory();
			Time time = new Time(){{setToNow();}};
			
			File myDirectory = new File(directory.getAbsolutePath() + "/indoorNavData");
			if(myDirectory.exists() == false)
			{
				myDirectory.mkdir();
			}
			File myFile = new File(myDirectory + "/beacon_data_" + time.format2445() +".csv"); //write new file with time stamp
			
			if(myFile.exists() == false)
			{
				myFile.createNewFile();
			}
			FileWriter fw = new FileWriter(myFile.getAbsoluteFile());
			BufferedWriter writer = new BufferedWriter(fw);  
			
			writer.write("hello from alon");
			
			writer.close();
			
			return n;
		}catch(IOException e)
		{
			throw e;
		}
	}
}
