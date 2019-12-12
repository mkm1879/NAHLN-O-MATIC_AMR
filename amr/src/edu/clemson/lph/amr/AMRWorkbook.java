/**
 * Copyright Jan 25, 2018 Michael K Martin
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
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.commons.collections4.iterators.PeekingIterator;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * 
 */
public class AMRWorkbook {
	
	private Workbook workbook = null;
	private PeekingIterator<Sheet> sheetIterator;
	private PeekingIterator<Row> rowIterator;
	private int iRow;
	private ArrayList<String> aHeader = null;
	private Sheet currentSheet = null;
	private AMRSpreadsheetRow currentRow = null;
	
		/**
	 * 
	 */
	public AMRWorkbook(File fFile) {
		try {
			FileInputStream inputStream = new FileInputStream(fFile);
			workbook = new XSSFWorkbook(inputStream);
			sheetIterator = new PeekingIterator<Sheet>(workbook.iterator());
			rowIterator = null;
		}catch( Exception e ) {
			System.err.println(e);
			e.printStackTrace();
		}
	}
	
	public boolean hasNextSheet() {
		boolean bRet = sheetIterator.hasNext();
		if(bRet) {
			Sheet nextSheet = sheetIterator.peek();
			String sName = nextSheet.getSheetName();
			if( "Table Data".equals(sName))
				bRet = false;
		}
		return bRet;
	}
	
	public void close() throws IOException {
		workbook.close();
	}
	
	public Sheet nextSheet() {
		currentSheet = null;
		if( sheetIterator.hasNext() ) {
			currentSheet = sheetIterator.next();
			rowIterator = new PeekingIterator<Row>(currentSheet.iterator());
			iRow = 0;
			while( rowIterator.hasNext() && iRow < 8 ) {
				Row rHeader = rowIterator.next();
				if( iRow++ < 7 ) continue;
				Iterator<Cell> iterator = rHeader.cellIterator();
				aHeader = new ArrayList<String>();
				while( iterator.hasNext() ) {
					Cell cNext = iterator.next();
					String sNext = cNext.getStringCellValue();
					aHeader.add(sNext);
				}
			}
			
		}
		return currentSheet;
	}
	
	public String getCurrentSheetName() {
		String sRet = null;
		if( currentSheet != null )
			sRet = currentSheet.getSheetName();
		return sRet;
	}
	
	public boolean hasNextRow() {
		boolean bRet = rowIterator.hasNext();
		if(bRet) {
			Row nextRow = rowIterator.peek();
			Cell cFirst = nextRow.getCell(0);
			if( cFirst == null )
				bRet = false;
			else if(cFirst.getCellType() != CellType.STRING )
				bRet = false;
			else {
				String sValue = cFirst.getStringCellValue();
				if( sValue == null || sValue.trim().length() == 0 )
					bRet = false;
			}
		}
		return bRet;
	}
	
	public AMRSpreadsheetRow nextRow() {
		AMRSpreadsheetRow rowRet = null;
		Row row = rowIterator.next();
		rowRet = new AMRSpreadsheetRow( row, aHeader );
		currentRow = rowRet;
		return rowRet;
	}
	
	public AMRSpreadsheetRow getCurrentRow() {
		return currentRow;
	}

}
