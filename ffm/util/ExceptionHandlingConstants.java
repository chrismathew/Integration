package gov.hhs.cms.base.common.util;

public class ExceptionHandlingConstants {
	
	// 100's	
	//Hub call exceptions
	public final static String SSA_HUB_ERROR = "101";
	public final static String CURRENT_INCOME_HUB_ERROR = "105";	
	public final static String IRS_HUB_ERROR = "106";
	public final static String NON_ESIMEC_HUB_NOT_RESPONDING_ERROR = "107";	
	public final static String INTERNAL_ERROR_DAL_UNABLE_GENERATE_IDENTIFIER = "110";
	public final static String VLP_STEP1_HUB_ERROR = "112";
	public final static String VLP_STEP1A_HUB_ERROR = "113";
	public final static String VLP_STEP1B_HUB_ERROR = "114";
	public final static String PLANNED_SSA_OUTAGE = "121";

	// DSH Errors
	public static final String DSH_TRANSFORMER_REQUEST_REASON_CODE = "188";
	public static final String DSH_TRANSFORMER_RESPONSE_REASON_CODE = "189";
	
	public final static String REFERENCE_CODE_EXCEPTION = "102";
	
	//300's
	public final static String GENERIC_BRMS_ERROR = "300";
	
		
	//440's
	public final static String DOCUMENTS_SERVICE_REASON_CODE_PREFIX = "400";	
	public static final String UNKNOWN_USER = "434";
	public static final String NO_ACTION_DEFINED = "444";	
	
	
	//500's
	public final static String GENERIC_EXCEPTION = "500";
	public final static String MY_ACCOUNT_SERVICES_REASON_CODE_PREFIX = "500";
	public final static String BAD_REQUEST_APPLICATION_ID_NOT_PASSED = "501";
	public final static String BAD_REQUEST_NOT_ENOUGH_DATA = "502";
	public final static String BAD_REQUEST_MEMBER_ID_MISSING = "503";
	public final static String BAD_REQUEST_APPLICATION_ALREADY_SUBMITTED = "504";
	public final static String INTERNAL_ERROR_INVALID_VOPO_MAPPING = "505";
	public final static String INTERNAL_ERROR_INVALID_POVO_MAPPING = "506";
	public final static String INTERNAL_ERROR_DAL_INVALID_ARGUMENTS = "510";
	public final static String INTERNAL_ERROR_DAL_DOCUMENT_EXISTS = "511";
	public final static String INTERNAL_ERROR_DAL_DOCUMENT_NOT_FOUND = "512";
	public final static String INTERNAL_ERROR_DAL_GENERAL_EXCEPTION = "515";
	public static final String MEMBER_MORE_THAN_TWO_PARENTS = "516";
	public static final String NOTICE_FAILURE_FOR_BATCH = "521";
	public static final String WRONG_CALL = "522";
	public static final String CARD_NOT_EXIST = "540";
	public static final String BARCODE_IDENTIFIER_IS_EMPTY = "552";	
	public final static String BAD_REQUEST_PERSON_TRACKING_NUMBER_IS_MISSING = "575";
	public final static String BAD_REQUEST_PERSON_BAR_CODE_IDENTIFIER_IS_MISSING = "576";
	public final static String BAD_REQUEST_INVALID_PERSON_ID = "577";
	public final static String BAD_REQUEST_USER_TRACKING_NUMBER_IS_MISSING = "578";
	public final static String BAD_REQUEST_INVALID_APPLICATION_ID = "579";
	public final static String BAD_REQUEST_NULL_INPUT = "580";
	public final static String PERSON_MATCHING_EXCEPTION = "533";
	public static final String UNLINK_APPLICATION_TO_USER_EXCEPTION = "534";
	
	// 600's
	public final static String DIRECT_ENROLLMENT_SERVICE_REASON_CODE_PREFIX = "600";
	
	
	//900's	
	public final static String GENERIC_SYSTEM_FAULT = "900";
	public static final String APPLICATION_ID_NULL = "906";
	public static final String NO_AUTHORISED_USER = "907";
	public static final String PERSON_ID_NULL = "908";
	public static final String SYSTEM_USER_NULL = "909";
	public static final String EMPTY_LIST = "910";
	

	/******** 	Prefix Constants **********/ 
	
		//Prefix constants for Base/Arch	
		public final static String SERVICE_INVOKER_HELPER_PREFIX = "004";
		public final static String REFERENCE_TYPE_HELPER_PREFIX = "005";
		public final static String BRMS_UTIL_PREFIX = "007";
	
		// Prefix for IndApp
		public final static String INDIVIDUAL_APP_SERVICE_REASON_CODE_PREFIX = "100";

	
}
