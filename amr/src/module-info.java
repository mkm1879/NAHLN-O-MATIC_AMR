module amr {
	requires lphdialogs;
	requires lphutils;
	requires transitive poi;
	requires transitive java.xml;
	requires java.desktop;
	requires poi.ooxml;
	requires httpcore;
	requires httpclient;
	
	exports edu.clemson.lph.amr;
}