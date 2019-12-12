/**
 * Copyright Feb 12, 2018 Michael K Martin
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
 */
package edu.clemson.lph.amr;

import java.io.File;
import javax.swing.SwingUtilities;

import edu.clemson.lph.amr.exceptions.ConfigException;
import edu.clemson.lph.dialogs.MessageDialog;
import edu.clemson.lph.dialogs.ProgressDialog;
import edu.clemson.lph.dialogs.ThreadCancelListener;
import edu.clemson.lph.utils.FileUtils;
import edu.clemson.lph.utils.UniqueID;

/**
 * 
 */
public class ProcessingLoop extends Thread implements ThreadCancelListener {
	private ProgressDialog prog;
	private boolean bCancel = false;
	private String sInbox;
	private String sOutbox;
	private String sErrorsbox;

	/**
	 * @throws ConfigException 
	 * 
	 */
	public ProcessingLoop() throws ConfigException {
		prog = new ProgressDialog(null, "NAHLN-O-MATIC_AMR", "Ready to process");
		prog.setCancelListener(this);
		prog.setAuto(true);
		prog.setVisible(true);
		sInbox = ConfigFile.getInBox();
		sOutbox = ConfigFile.getOutBox();
		sErrorsbox = ConfigFile.getErrorsBox();
	}

	@Override
	public void run() {
		try {
			while( !bCancel ) {
				synchronized (this) {
					try {
						IntPair retVal = step();
						if(retVal.iAccept > 0 || retVal.iFail > 0 )
							updateProgress( retVal.iAccept + " messages accepted\n"
									+ retVal.iFail + " messages failed", "Progress ...");
						wait(ConfigFile.getPollSeconds() * 1000 );
					}
					catch (InterruptedException ex) {
					}
				}
			}
		}
		catch( Throwable e ) {
			System.err.println("Unexpected exception in main loop: " + e.getMessage());
			e.printStackTrace();
			MessageDialog.messageWait(null, "NAHLN-O-MATIC_AMR", "Unexpected exception in main loop");
		}
		finally {
			System.err.println("Exiting NAHLN-O-MATIC_AMR");
			UniqueID.save();
			System.exit(0);
		}
	}
	
	private void updateProgress( String sProgMsg, String sProgLabel ) {
		synchronized (prog) {
			try {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						prog.setMessage(sProgMsg);
						prog.setProgLabel(sProgLabel);
					}
				});
			}
			catch (Exception ex) {
				ex.printStackTrace();
			}
		}

	}
	
	private IntPair step() {
		Sender sender = null;
		IntPair retVal = new IntPair();
		try {
			if(ConfigFile.isSHICHost() )
				sender = new SHICPostSender();
			else
				sender = new NAHLNPostSender();
		} catch (ConfigException e1) {
			e1.printStackTrace();
		}
		File fDirIn = new File(sInbox);
		File fDirOut = new File(sOutbox);
		File fDirErrors = new File(sErrorsbox);
		for( File fFile : fDirIn.listFiles() ) {
			try {
				NahlnOMaticAMR.setCurrentFile(fFile);
				updateProgress("Reading " + fFile.getName(), "Progress ...");
				pause();
				AMRWorkbook sheet = new AMRWorkbook(fFile);
				NahlnOMaticAMR.setCurrentSheet(sheet);
				boolean bHasErrors = false;
				while( sheet.hasNextSheet() ) {
					sheet.nextSheet();
					updateProgress( "Processing " + sheet.getCurrentSheetName(), "Progress ...");
					NahlnOMaticAMR.setCurrentTab(sheet.getCurrentSheetName());
					while( sheet.hasNextRow() ) {
						AMRSpreadsheetRow row = sheet.nextRow();
						NahlnOMaticAMR.setCurrentRow(row);
						
						OpuR25Document opu = new OpuR25Document(row);
						String sMsg = opu.toXMLString();
						String sID = opu.getUniqueSpecimen();
						updateProgress("Sending " + fFile.getName() + "_" + sID, "Progress ...");
						pause();
						String sRet = sender.send(sMsg);
						String sMsgFile = null;
						String sAckFile = null;
						if(sRet.contains("<MSA.1>AA</MSA.1>")) {
							retVal.iAccept++;
							updateProgress(fFile.getName() + "_" + sID + " Accepted", "Waiting ...");
							pause();
							sMsgFile = fDirOut.getAbsolutePath() + "/" + fFile.getName() + "_" + sID + ".xml";
							sAckFile = fDirOut.getAbsolutePath() + "/"  + fFile.getName() + "_" + sID + "_ACK.xml";
						}
						else {
							System.err.println(sRet);
							retVal.iFail++;
							bHasErrors = true;
							updateProgress(fFile.getName() + "_" + sID + " contained errors", "Waiting ...");
							pause();
							sMsgFile = fDirErrors.getAbsolutePath() + "/"  + fFile.getName() + "_" + sID + ".xml";
							sAckFile = fDirErrors.getAbsolutePath() + "/"  + fFile.getName() + "_" + sID + "_ACK.xml";
						}
						FileUtils.writeTextFile(sMsg, sMsgFile);
						FileUtils.writeTextFile(sRet, sAckFile);
					}
				}
				if( bHasErrors )
					FileUtils.move( fFile, fDirErrors );
				else
					FileUtils.move( fFile, fDirOut );
			} catch (Throwable e) {
				System.err.println("Error in main loop while processing " + NahlnOMaticAMR.getCurrentFile().getName()
						+ "\r\n Error: " + e.getMessage()
						+ "\r\n Sheet: " + NahlnOMaticAMR.getCurrentTab()
						+ "\r\n Column: " + NahlnOMaticAMR.getCurrentColumn()
						+ "\r\n Row:\r\n" + NahlnOMaticAMR.getCurrentRow().toString() + "\n" + e.getMessage());
				MessageDialog.messageWait(null, "NAHLN-O-MATIC_AMR", "Error in main loop while processing " + NahlnOMaticAMR.getCurrentFile().getName() 
						+ "\r\n Error: " + e.getMessage()
						+ "\r\n Sheet: " + NahlnOMaticAMR.getCurrentTab()
						+ "\r\n Column: " + NahlnOMaticAMR.getCurrentColumn()
						+ "\r\n Row:\r\n" + NahlnOMaticAMR.getCurrentRow().toString() );
				e.printStackTrace();
				FileUtils.move( fFile, fDirErrors );
			}
		}
		return retVal;
	}
	
	private void pause() {
		try {
			wait(10);
		} catch( InterruptedException e) {}
	}

	/* (non-Javadoc)
	 * @see edu.clemson.lph.dialogs.ThreadCancelListener#cancelThread()
	 */
	@Override
	public void cancelThread() {
		bCancel = true;
		this.interrupt();
	}

}
