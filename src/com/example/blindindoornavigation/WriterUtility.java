package com.example.blindindoornavigation;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import android.os.Environment;
import android.text.format.Time;

public class WriterUtility {

	private static StringBuilder sb;
	private static int n = 0; //n = the number of records (lines) added.
	
	//returns number of files in folder
	public static void startTest()
	{
		sb = new StringBuilder();
	}
	
	public static void endTestAndSave() throws IOException
	{
		try{
			File rootDirectory = Environment.getExternalStorageDirectory();
			Time time = new Time(){{setToNow();}};
			
			File myDirectory = new File(rootDirectory.getAbsolutePath() + "/indoorNavData");
			if(myDirectory.exists() == false)
			{
				myDirectory.mkdir();
			}
			//Create file name with time stamp. 
			File myFile = new File(myDirectory + "/beacon_data_" + time.format2445() +".csv"); //write new file with time stamp
			
			if(myFile.exists() == false)
			{
				myFile.createNewFile();
			}
			FileWriter fw = new FileWriter(myFile.getAbsoluteFile());
			BufferedWriter writer = new BufferedWriter(fw);  
			
			writer.write(sb.toString());
			writer.newLine();
			writer.close();
			
			
		}catch(IOException e)
		{
			throw e;
		}
	}
	public static int writeStep(List<String> itemsToWrite) throws IOException
	{
		n++;
		
		for(int i=0; i<itemsToWrite.size(); i++)
		{
			sb.append(itemsToWrite.get(i));
			if(i != itemsToWrite.size()-1)
			{
				sb.append(",");
			}
		}
		
		//*** Add more data such as sensors here ***
		
		//Add time
		sb.append("," + new Time(){{setToNow();}}.format2445());
		
		
		sb.append('\n');
		return n;
	}
	
	public static int undoStep()
	{
		int lastNewLine = sb.lastIndexOf("\n");
		sb.delete(lastNewLine, sb.length());
		lastNewLine = sb.lastIndexOf("\n");
		sb.delete(lastNewLine+1, sb.length());
		n--;
		return n;
	}
}
