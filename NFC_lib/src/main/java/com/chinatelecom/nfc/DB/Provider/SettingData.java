package com.chinatelecom.nfc.DB.Provider;

import android.net.Uri;

public class SettingData {
	public static final Integer INSERT_ERR = -1;
	
	public static final Integer DEFAULT_MODE = -1;
	public static final Integer ON = 0;
	public static final Integer OFF = 1;
	
	public static final long ONE_DAY = 8640000;
	
	public static final int USER = 10;
	public static final int ADMIN = 11;
	
	public static final int AWARDS_NOT = 0;
	public static final int AWARDS = 1;
	
	
	
	/**
	 * URI of begin transaction
	 */
	public static final String TRANSACTION = "transaction";
	/**
	 * URI of commit
	 */
	public static final String COMMIT = "commit";
	/**
	 * URI of rollback
	 */
	public static final String ROLLBACK = "rollback";
	
	/**
	 *URI to attach
	 */
	public static final String ATTACH = "attach";
	/**
	 * URI to detach
	 */
	public static final String DETACH = "detach";
	/**
	 * database to attach
	 */
	public static final String ATTACH_DB = "db";
	/**
	 * path of the database to attach 
	 */
	public static final String ATTACH_DB_PATH = "path";

	/**
	 * name of the database to attach 
	 */
	public static final String ATTACH_DB_NAME = "tmpdb";
	
	/**
	 * Other filter declaration(GROUP BY)
	 */
	public static final String GROUPBY = "GROUP BY ";
	/**
	 * Other filter declaration(HAVING)
	 */
	public static final String HAVING  = "HAVING ";
	/**
	 * Other filter declaration(ORDER BY)
	 */
	public static final String ORDERBY = "ORDER BY ";
	/**
	 * Other filter declaration(LIMIT)
	 */
	public static final String LIMIT   = "LIMIT ";
	/**
	 * Other filter declaration(OFFSET)
	 */
	public static final String OFFSET   = "OFFSET ";

	
	/**
	 *  The current path of AUTHORITY
	 */
	private static final String AUTHORITY_BASE = "com.chinatelecom.nfc.db.";
	public static final String AUTHORITY_TELECOM = AUTHORITY_BASE + "telecomcontentprovider";

	public static final Uri URI_ENVIRONMENTS = Uri.parse("content://" + AUTHORITY_TELECOM + "/");
}
