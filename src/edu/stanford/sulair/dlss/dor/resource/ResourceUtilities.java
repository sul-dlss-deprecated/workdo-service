package edu.stanford.sulair.dlss.dor.resource;

import org.apache.log4j.Logger;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.ws.rs.core.Response;

public class ResourceUtilities {
	public static Response createErrorResponse(Exception e, Logger logger) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
        String trace = sw.toString();
        logger.error(trace);
		return Response.serverError().entity(trace).build();
	}
}
