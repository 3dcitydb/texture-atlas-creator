package org.citygml.util;

import java.text.DecimalFormat;
import java.util.Calendar;



public class Logger {
	public final static boolean SHOW_STACK_PRINT=true;
	public final static int TYPE_INFO=1;
	public final static int TYPE_ERROR=2;
	public final static int TYPE_NESS=3;
	
	private Calendar cal;
	private DecimalFormat df = new DecimalFormat("00");
	public boolean showDetail=true;
	
	private static Logger INSTANCE = new Logger();
	
	public static Logger getInstance() {
		return INSTANCE;
	}
	
	public  String getPrefix() {
		cal = Calendar.getInstance();

		int h = cal.get(Calendar.HOUR_OF_DAY);
		int m = cal.get(Calendar.MINUTE);
		int s = cal.get(Calendar.SECOND);
		
		

		StringBuffer prefix = new StringBuffer();
		prefix.append("[");
		prefix.append(df.format(h));
		prefix.append(":");
		prefix.append(df.format(m));
		prefix.append(":");
		prefix.append(df.format(s));
		prefix.append("] ");

		return prefix.toString();
	}
	
	public  void log(int type,String log){
		if ((type==TYPE_INFO && this.showDetail)|| type==TYPE_NESS)
			System.out.println(getPrefix()+log);
		else if (type==TYPE_ERROR){
			System.out.println(getPrefix()+log);
		}
	}
	
	public void setShowDetail(boolean b){
		this.showDetail=b;
	}
}
