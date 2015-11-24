package com.telecompp.util;

import java.io.File;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import android.os.Environment;
import de.mindpipe.android.logging.log4j.LogConfigurator;

public class LoggerHelper {

	private Logger logger;
	
	public LoggerHelper(Class clazz){
		LogConfigurator logConfigurator = new LogConfigurator();  
        logConfigurator.setFileName(Environment.getExternalStorageDirectory()  
                        + File.separator + "翼机通-碰碰-log" + File.separator + "logs"  
                        + File.separator + "log4j.txt");  
        logConfigurator.setRootLevel(Level.DEBUG);
        logConfigurator.setLevel("org.apache", Level.ERROR);  
        logConfigurator.setFilePattern("%d %-5p [%c{2}]-[%L] %m%n");  
        logConfigurator.setMaxFileSize(1024 * 1024 * 5);  
        logConfigurator.setImmediateFlush(true);  
        logConfigurator.configure();  
        logger = Logger.getLogger(clazz);  
	};
	
	public void printLogOnSDCard(Object msg) {
		logger.info(msg);
	}
}
