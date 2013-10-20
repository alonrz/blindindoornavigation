package com.example.blindindoornavigation;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

import android.os.Environment;
import android.text.format.Time;
import org.json.simple.parser.JSONParser;

/**
 * This Will serve to read from DB or file. Any info that requires reading will be handled here
 */
public class ReaderUtility {

	public void readPointsOfInterest() throws IOException
	{
		try {
			File rootDirectory = Environment.getExternalStorageDirectory();

			File myDirectory = new File(rootDirectory.getAbsolutePath()
					+ "/indoorNavData");
			if (myDirectory.exists() == false) {
				myDirectory.mkdir();
			}
			// Create file name with time stamp.
			File myFile = new File(myDirectory + "/POI.xml"); //find POI.xml file

			if (myFile.exists() == false) {
				throw new FileNotFoundException("POI.xml is missing");
			}

			//TODO: Write JSONParser here
			
//			FileWriter fw = new FileWriter(myFile.getAbsoluteFile());
//			BufferedWriter writer = new BufferedWriter(fw);
//
//			writer.write(sb.toString());
//			writer.newLine();
//			writer.close();

		} catch (IOException e) {
			throw e;
		} finally {
//			if (sensor != null) {
//				mSensorManager.unregisterListener(mySensorEventListener);
//			}
		}
	}
}
