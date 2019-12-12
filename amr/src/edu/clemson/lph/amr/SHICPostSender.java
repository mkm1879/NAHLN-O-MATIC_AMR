/**
 * Copyright Feb 8, 2018 Michael K Martin
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;

import edu.clemson.lph.amr.exceptions.ConfigException;
import edu.clemson.lph.amr.exceptions.HL7Exception;

/**
 * 
 */
public class SHICPostSender implements Sender {

	/**
	 * 
	 */
	public SHICPostSender() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public String send( String sMsg ) throws HL7Exception, ConfigException, IOException {
	    SSLContextBuilder builder = null;
	    StringBuffer result = null;
	    try {
	    	builder = new SSLContextBuilder();
	    	builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
	    	SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
	    			builder.build());
	    	CloseableHttpClient client = HttpClients.custom().setSSLSocketFactory(
	    			sslsf).build();
	    	String sURI = ConfigFile.getHost();
	    	HttpPost post = new HttpPost(sURI);
	    	post.addHeader("api-key", ConfigFile.getSHICApiKey());
	    	post.addHeader("content-type","application/xml");
	    	post.setEntity(new StringEntity(sMsg, ContentType.TEXT_XML));
	    	HttpResponse response = null;
	    	
	    	response = client.execute(post);
			System.out.println("Response Code from " + sURI + " : " 
					+ response.getStatusLine().getStatusCode());
	    	BufferedReader rd = new BufferedReader(
	    			new InputStreamReader(response.getEntity().getContent()));
	    	result = new StringBuffer();
	    	String line = "";
	    	while ((line = rd.readLine()) != null) {
	    		result.append(line);
	    	}
	    } catch (NoSuchAlgorithmException | KeyStoreException | KeyManagementException e) {
	    	System.err.println(e.getMessage());
	    	e.printStackTrace();
	    }
		return result.toString();
	}
}

