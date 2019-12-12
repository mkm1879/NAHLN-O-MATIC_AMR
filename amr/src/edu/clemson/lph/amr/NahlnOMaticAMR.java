/**
 * Copyright Jan 24, 2018 Michael K Martin
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package edu.clemson.lph.amr;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

import javax.swing.UIManager;

import edu.clemson.lph.amr.mappings.LoincMap;
import edu.clemson.lph.amr.mappings.SnomedMap;
import edu.clemson.lph.dialogs.MessageDialog;

public class NahlnOMaticAMR {
	private static final String sVersion = "1.2";
	@SuppressWarnings("exports")
	public static SnomedMap snomedMap;
	@SuppressWarnings("exports")
	public static LoincMap loincMap;
	
	// Static properties just to track status for logging
	public static AMRSpreadsheetRow currentRow;
	public static void setCurrentRow( AMRSpreadsheetRow row ) {
		currentRow = row;
	}
	public static AMRSpreadsheetRow getCurrentRow() {
		return currentRow;
	}
	public static File currentFile;
	public static void setCurrentFile( File File ) {
		currentFile = File;
	}
	public static File getCurrentFile() {
		return currentFile;
	}
	public static AMRWorkbook currentSheet;
	public static void setCurrentSheet( AMRWorkbook sheet ) {
		currentSheet = sheet;
	}
	public static AMRWorkbook getCurrentSheet() {
		return currentSheet;
	}
	public static String currentTab;
	public static void setCurrentTab( String sTab ) {
		currentTab = sTab;
	}
	public static String getCurrentTab() {
		return currentTab;
	}
	public static String currentColumn;
	public static void setCurrentColumn( String sColumn ) {
		currentColumn = sColumn;
	}
	public static String getCurrentColumn() {
		return currentColumn;
	}
	

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			  try {
				  FileOutputStream fos = new FileOutputStream("./NOMOut.txt", false);
				  FileOutputStream fes = new FileOutputStream("./NOMErr.txt", true);
				  PrintStream printStreamOut = new PrintStream(fos, true);
				  PrintStream printStreamErr = new PrintStream(fes, true);
				  System.setOut(new PrintStream(printStreamOut));
				  System.setErr(new PrintStream(printStreamErr));
			  } catch (FileNotFoundException e) {
				  // TODO Auto-generated catch block
				  e.printStackTrace();
			  }
			System.out.println("Starting NAHLN-O-MATIC_AMR version " + sVersion);
			String os = System.getProperty("os.name");
			if( os.toLowerCase().contains("mac os") ) {
				System.setProperty("apple.laf.useScreenMenuBar", "true");
				System.setProperty("com.apple.mrj.application.apple.menu.about.name",
						"Civet");
				System.setProperty("com.apple.mrj.application.growbox.intrudes",
						"false");
				UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
			}
			else {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			}
		} catch (Exception e) {
			// Record everywhere:  console, log, dialog
			e.printStackTrace();
			MessageDialog.messageWait(null, "NAHLN-O-MATIC_AMR", "Configuration error: " + e.getMessage());
		}
		try {
			snomedMap = new SnomedMap();
			loincMap = new LoincMap();
			if( args.length > 0 && args[0].equalsIgnoreCase("ROBOT") ) {
				ProcessingLoop loop = new ProcessingLoop();
				loop.start();
			}
			else if( args.length > 0 && args[0].equalsIgnoreCase("HEADLESS") ) {
				ProcessingSingle single = new ProcessingSingle(true);
				single.start();
			}
			else {
				ProcessingSingle single = new ProcessingSingle(false);
				single.start();
			}
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}

}
