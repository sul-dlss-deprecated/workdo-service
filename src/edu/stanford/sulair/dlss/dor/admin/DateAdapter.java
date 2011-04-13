package edu.stanford.sulair.dlss.dor.admin;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class DateAdapter extends XmlAdapter<String, Date> {

	public static final DateFormat STANDARD_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
	
	@Override
	public String marshal(Date arg0) throws Exception {
		return STANDARD_DATE_FORMAT.format(arg0);
	}

	@Override
	public Date unmarshal(String arg0) throws Exception {
		return STANDARD_DATE_FORMAT.parse(arg0);
	}

}
