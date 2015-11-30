package com.perficient.etm.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base service to group common properties and methods 
 * for the back-end services.
 * @author Alexandro Blanco <alex.blanco@perficient.com>
 *
 */
public class AbstractBaseService {

	/**
	 * Common logger object for the back end services.
	 */
	private static final Logger log = LoggerFactory.getLogger("BackendService");

	public static Logger getLog() {
		return log;
	}
	
}
