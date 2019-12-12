/**
 * Copyright Jan 29, 2018 Michael K Martin
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
package edu.clemson.lph.amr.datatypes;

import org.w3c.dom.Document;

import edu.clemson.lph.amr.AMRSpreadsheetRow;
import edu.clemson.lph.amr.HL7Object;
import edu.clemson.lph.amr.exceptions.XMLException;

/**
 * 
 */
public class OBX extends HL7Object {

	/**
	 * @param doc
	 * @param row
	 * @throws XMLException
	 */
	public OBX(Document doc, AMRSpreadsheetRow row) throws XMLException {
		super(doc, row);
		// TODO Auto-generated constructor stub
	}

}
