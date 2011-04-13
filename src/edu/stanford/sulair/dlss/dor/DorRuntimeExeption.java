package edu.stanford.sulair.dlss.dor;

public class DorRuntimeExeption extends RuntimeException {
 
	private static final long serialVersionUID = 8942077323973910141L;

	public DorRuntimeExeption(String msg){
		super(msg);
	}
	
	public DorRuntimeExeption(String msg, Throwable t){
		super(msg, t);
	}
	
	public DorRuntimeExeption(Throwable t){
		super(t);
	}

}
