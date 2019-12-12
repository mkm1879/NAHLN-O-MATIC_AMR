package edu.clemson.lph.utils;
/*
Copyright 2014 Michael K Martin

This file is part of Civet.

Civet is free software: you can redistribute it and/or modify
it under the terms of the Lesser GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Civet is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the Lesser GNU General Public License
along with Civet.  If not, see <http://www.gnu.org/licenses/>.
*/
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

public class FileUtils {

	public static String readTextFile( File fIn ) throws Exception {
	    StringBuilder text = new StringBuilder();
	    String NL = System.getProperty("line.separator");
	    FileInputStream fsIn = null;
	    Scanner scanner = null;
	    try {
	    	fsIn = new FileInputStream(fIn);
	    	scanner =new Scanner(fsIn, "UTF-8"); 
	      while (scanner.hasNextLine()){
	        text.append(scanner.nextLine() + NL);
	      }
	    }
	    catch( Exception e ) {
			System.err.println("Error reading file " + fIn.getAbsolutePath() + "\n" + e.getMessage() );
			throw e;
	    }
	    finally{
	    	try {
	    		if( scanner != null ) {
	    			scanner.close();
	    			scanner = null;
	    		}
	    	} catch( Exception e2 ) {
	    		System.err.println("Failure to close file " + fIn.getAbsolutePath() + "\n" + e2.getMessage() );
	    		e2.printStackTrace();
	    	}
	    	try {
	    		if( fsIn != null ) {
	    			fsIn.close();
	    			fsIn = null;
	    		}
	    	} catch( Exception e3 ) {
	    		System.err.println("Failure to close file " + fIn.getAbsolutePath() + "\n" + e3.getMessage() );
	    		e3.printStackTrace();
	    	}
	    }
	    return text.toString();
	}
	
	public static byte[] readBinaryFile( String sFilePath ) throws Exception {
		File f = new File( sFilePath );
		return readBinaryFile( f );
	}
		
	public static byte[] readBinaryFile( File fThis ) throws Exception {
		byte[] bytes = null;
		long len = fThis.length();
		int iRead = -1;
		FileInputStream r = null;
		try {
			r = new FileInputStream( fThis );
			bytes = new byte[(int)len];
			iRead = r.read(bytes);
		}
		catch( Exception e ) {
			System.err.println("Error reading file " + fThis.getAbsolutePath() + "\n" + e.getMessage() );
			throw e;
		}
		finally {
			try {
				if( r != null ) {
					r.close();
					r = null;
				}
			} catch( Exception e2 ) {
	    		System.err.println("Failure to close file " + fThis.getAbsolutePath() + "\n" + e2.getMessage() );
	    		e2.printStackTrace();
			}
		}
		if( iRead != len ) 
			throw new Exception("File " + fThis.getName() + " size = " + len + " read = " + iRead);
		return bytes;
	}
	
	
	public static File writeBinaryFile( byte[] bytes, String sFileOut ) {
		File fOut = null;
		FileOutputStream fsOut = null;
		try {
			try {
				fOut = new File(sFileOut);
				fsOut = new FileOutputStream( fOut );
				fsOut.write(bytes);
			}
			catch (IOException ex1) {
				System.err.println("\nError writing " + sFileOut+ "\n" + ex1.getMessage() );
				ex1.printStackTrace();
			}
			finally { 
				try {
					if( fsOut != null ) {
						fsOut.close();
						fsOut = null;
					}
				} catch( Exception ex2 ) {
		    		System.err.println("Failure to close file " + sFileOut + "\n" + ex2.getMessage() );
		    		ex2.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();;
		}
		return fOut;
	}

	/**
	 * This should be named copyBinaryFileToTemp.
	 * @param fIn
	 * @return
	 */
	public static File copyBinaryFile( File fIn ) {
		File fOut = null;
		String sFileOut = null;
		FileOutputStream fsOut = null;
		try {
			byte[] bytes = readBinaryFile(fIn);
			try {
				fOut = File.createTempFile("TempFileCVI", ".pdf");
				sFileOut = fOut.getAbsolutePath();
				fsOut = new FileOutputStream( sFileOut );
				fsOut.write(bytes);
			}
			catch (IOException ex1) {
				System.err.println(ex1.getMessage() + "\nError writing " + sFileOut + " temporary file");
				ex1.printStackTrace();
			}
			finally { 
				try {
					if( fsOut != null ) {
						fsOut.close();
						fsOut = null;
					}
				} catch( Exception ex2 ) {
					System.err.println("Error closing " + sFileOut + " temporary file \n" + ex2.getMessage());
					ex2.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fOut;
	}
	
	public static String replaceInvalidFileNameChars( String sFileName ) {
		if( sFileName == null || sFileName.trim().length() == 0 )
			return null;
		String os = System.getProperty("os.name");
		os = os.toLowerCase();
		String invalidChars;
		if (os.contains("win")) {
		    invalidChars = "\\/:*?\"<>|";
		} else if (os.contains("mac")) {
		    invalidChars = "/:";
		} else { // assume Unix/Linux
		    invalidChars = "/";
		}
		for (int i = 0; i < invalidChars.length(); i++) {
			sFileName = sFileName.replace(invalidChars.charAt(i), '_');
		}
		return sFileName;
	}
	
	
	public static void move(File fFile, File fDir) {
		fFile.renameTo( new File( incrementFileName(fDir.getAbsolutePath() + "/" + fFile.getName() ) ) );
	}
	/**
	 * Take a filename or path and add (1) until unique
	 * @param sFileName
	 * @return
	 */
	public static String incrementFileName( String sFileName ) {
		if( sFileName == null || sFileName.trim().length() == 0 ) return null;
		String sName = null;
		String sExt = null;
		File f = new File( sFileName );
		while( f.exists() ) {
			sName = getLeftPart(sFileName);
			sExt = getExt(sFileName);
			if( !sName.endsWith(")") ) {
				sName = sName + "(1)";
				sFileName = sName + sExt;
			}
			else {
				String sNum = sName.substring(sName.lastIndexOf('(')+1, sName.lastIndexOf(')'));
				int iNum = Integer.parseInt(sNum) + 1;
				sName = sName.substring(0,sName.lastIndexOf('(')) + "(" + iNum + ")";
				sFileName = sName + sExt;
			}
			f = new File( sFileName );
		}
		return sFileName;
	}
	
	private static String getLeftPart( String sFileName ) {
		if( !sFileName.contains(".") ) return sFileName;
		return sFileName.substring(0,sFileName.lastIndexOf('.'));
	}
	
	public static String getExt( String sFileName ) {
		if( !sFileName.contains(".") ) return "";
		return sFileName.substring(sFileName.lastIndexOf('.'));
	}
	
	public static String getRoot( String sFileName ) {
		if( !sFileName.contains(".") ) return "";
		return sFileName.substring(0,sFileName.lastIndexOf('.'));
	}
	
	
	
	public static File writeTextFile( String sText, String sFileOut ) {
		return writeTextFile( sText, sFileOut, false );
	}
		
	public static File writeTextFile( String sText, String sFileOut, boolean bAppend ) {
		File fOut = null;
		PrintWriter pwOut = null;
		try {
			try {
				fOut = new File(sFileOut);
				pwOut = new PrintWriter(new FileOutputStream( fOut, bAppend ));
				pwOut.write(sText);
			}
			catch (IOException ex1) {
				System.err.println("\nError writing " + sFileOut + "\n" + ex1.getMessage());
				ex1.printStackTrace();
			}
			finally { 
				try {
					if( pwOut != null ) {
						pwOut.close();
						pwOut = null;
					}
				} catch( Exception ex2 ) {
					System.err.println("Error closing " + "\n" + ex2.getMessage());
					ex2.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fOut;
	}


}
