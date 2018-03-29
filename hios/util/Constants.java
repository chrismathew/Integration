package org.cms.hios.common.util;

public class Constants {
	
	public static final String DB_YES_VAL ="1";
	public static final String DB_NO_VAL ="0";
	
	public static final int DB_LIMIT =2000;
	public static final int MAX_INVALID_PASSWORD_ATTEMPT=3;
	public static final int MAX_RETRY_INTERVAL=10;
	public static final String CURRENT_TIME_FIELD = "CurrentDateTime";
	public static final String RBIS_SP_SUBMISSION_WINDOW_IS_OPEN="SP_RBIS_SUBMISSION_WINDOW";
	public static final String CCC_SP_SUBMISSION_WINDOW_IS_OPEN="SP_CCC_SUBMISSION_WINDOW";
	public static final String QHP_SP_SUBMISSION_WINDOW_IS_OPEN="SP_QHP_SUBMISSION_WINDOW";
	public static final String QBSA_SP_SUBMISSION_WINDOW_IS_OPEN="SP_FFEBSA_SUBMISSION_WINDOW";
	public static final String QRM_SP_SUBMISSION_WINDOW_IS_OPEN="SP_FFERDM_SUBMISSION_WINDOW";
	public static final String URR_SP_SUBMISSION_WINDOW_IS_OPEN="SP_FFERRJ_SUBMISSION_WINDOW";
	public static final String STEV_SP_SUBMISSION_WINDOW_IS_OPEN="SP_QHPSTATE_SUBMISSION_WINDOW";
	public static final String FINMGT_SP_SUBMISSION_WINDOW_IS_OPEN="SP_FM_SUBMISSION_WINDOW";
	public static final String QHPARGM_SP_SUBMISSION_WINDOW_IS_OPEN="SP_QHP_AGRM_SUBMISSION_WINDOW";
	public static final String CMS_SP_SUBMISSION_WINDOW_IS_OPEN="SP_CMS_SUBMISSION_WINDOW";
	public static final String QHPPPREVIEW_SP_SUBMISSION_WINDOW_IS_OPEN="SP_QHPPPreview_SUBMISSION_WINDOW";
	
	/**
	 * MM/dd/yyyy HH:mm:ss
	 */
	public static final String DATE_TIME_FORMAT ="MM/dd/yyyy HH:mm:ss";
	public static final String DATE_TIME_AM_PM_FORMAT = "MM/dd/yyyy HH:mm:ss a";
	public static final String DATE_FORMAT = "MM/dd/yyyy";
	
	//constants referred in HIOS-ESB-BGM Email framework
	public static final String COMPANY_ID = "COMPANY_ID";
	public static final String REQUEST_PK = "REQUEST_PK";
	public static final String ISSUER_ID = "ISSUER_ID";
	public static final String USER_CREATION = "USER_CREATION";
	public static final String HPID_USER_ACCESS_APPROVER = "HPIDUserAccessApprover";

	public static final String AUTHORIZING_OFFICIAL = "Authorizing Official";
	public static final String SUBMITTER = "Submitter";
	public static final String HPOES_BEAN_NAME = "HpoesUserRoleApprovedBean";
	public static final String HPOES_NEW_USER_AO_REQUEST = "HpoesNewUserAORequestBean";
	public static final String HPOES_NEW_USER_REQUEST_APPROVED = "HpoesNewUserRequestApprovedBean";
	public static final String HPOES_AP_PENDING_APP= "HpoesAOPendingApplicationBean";
	public static final String HPID_AUTH_OFFICIAL = "HPIDAuthorizingOfficial";
	public static final String COMPANY_EMAIL_APPROVED_BEAN = "ApprovedCompanyEmailBean";
	public static final String ISSUER_EMAIL_APPROVED_BEAN = "ApprovedIssuerEmailBean";
	public static final String PENDING_REQUEST_APPROVAL_BEAN = "PendingRequestApprovalBean";
	public static final String APRVD_NONFED_EMAIL_BEAN = "ApprovedNonFedEmailBean";
	public static final String APRVD_OTHER_ORG_TYPE_EMAIL_BEAN = "ApprovedOtherOrgTypeEmailBean";
	public static final String ISSUER_MESSAGE_KEY = "ISSUER_CREATION";
	public static final String ISSUER_MESSAGE_VALUE = "Issuer Creation";


	
	//Entity Type Constants
	public static final String ENTITY_TYPE_COMPANY = "COMPANY";
    public static final String ENTITY_TYPE_ISSUER = "ISSUER";
    public static final String ENTITY_TYPE_STATE = "STATE";
    public static final String ENTITY_TYPE_GRANTSTATE = "GRANTSTATE";
    public static final String ENTITY_TYPE_CONTRACTOR = "CONTRACTOR";
    public static final String ENTITY_TYPE_CAPSITE = "CAPSITE";
    
    public static final String CMP_EDIT_EMAIL_ATTR_COMPANY="pendingCompany";
    public static final String CMP_EDIT_EMAIL_ATTR_CHANGED_FIELDS="FIELDS_CHANGED";
    public static final String CMP_EDIT_EMAIL_ATTR_CHANGED_USERID="USERID_CHANGED";
    public static final String CMP_EDIT_EMAIL_ATTR_REQUEST_PK = "REQUEST_PK";
    

    public static final String ISSR_EDIT_EMAIL_ATTR_CHANGED_FIELDS="FIELDS_CHANGED";
    public static final String ISSR_EDIT_EMAIL_ATTR_CHANGED_USERID="USERID_CHANGED";
    public static final String ISSR_EDIT_EMAIL_ATTR_ISSUER_ID = "ISSUER_ID";

    public static final String NON_FED_ORG_EDIT_EMAIL_ATTR_NON_FED="pendingNonFed";
    public static final String NON_FED_ORG_EDIT_EMAIL_ATTR_CHANGED_FIELDS="FIELDS_CHANGED";
    public static final String NON_FED_ORG_EDIT_EMAIL_ATTR_CHANGED_USERID="USERID_CHANGED";
    public static final String NON_FED_ORG_EDIT_EMAIL_ATTR_REQUEST_PK = "REQUEST_PK";
    
    
    public static final String MODULE_ID = "ModuleId";
    public static final String SUBNOTVALIDATED="No";
	public static final String SUBVALIDATED="Yes";
	
	public static final int BUSINESS_CLSFCN_TYPE_CAT_ID=48;
	public static final String USER_ROLE_APPROVER="UserRoleApprover";
	public static final String USER_ROLE_APPROVER_SEC_ROLE="HPORT_UserRoleApprover";
	public static final String COMPANY_ADMIN_SEC_ROLE="HPORT_COMPANYADMINISTRATOR";
	public static final String COMPANY_ADMIN_HIOS_ROLE="CompanyAdministrator";
	public static final String ORG_APPROVER_SEC_ROLE = "HPORT_OrganizationApprover";
	public static final String ORG_APPROVER_HIOS_ROLE = "OrganizationApprover";
	public static final String ORG_ADMINISTRATOR_ROLE = "OrganizationAdministrator";
	public static final String ERE_SUBMITTER_ROLE = "ERESubmitter";
	public static final String ERE_CCIIO_REVIEWER_ROLE = "ERECCIIOReviewer";
	public static final String ROLE_APPROVER_ADMINISTRATOR_ROLE = "RoleApproverAdministrator";
	public static final String ROLE_APPROVER_ADMIN_SEC_REOLE = "HPORT_ROLEAPPROVERADMINISTRATOR";
	public static final String CSG_APPROVER_ROLE = "CSGApprover";
	public static final String CSG_APPROVER_SEC_REOLE = "HPORT_CSGApprover";
	public static final String ASISTR_ATTESTER_ROLE = "AssisterAttester";
	public static final String ASISTR_SUBMITTER_ROLE = "AssisterSubmitter";
	public static final String ASISTR_CSGREVIEWER_ROLE = "CSGReviewer";
	public static final String ASISTR_STATE_ROLE = "AssisterState";
	public static final String ASISTR_ATTESTER_SEC_ROLE = "ASISTR_AssisterAttester";
	public static final String ASISTR_SUBMITTER_SEC_ROLE = "ASISTR_AssisterSubmitter";
	public static final String ASISTR_CSGREVIWER_SEC_ROLE = "ASISTR_CSGReviewer";
	public static final String ASISTR_STATE_SEC_ROLE = "ASISTR_AssisterState";
	public static final String ESM_SUBMITTER_ROLE = "ESMSubmitter";
	
	
	
	//ORG_TYPE_HIERARCHY_LVL_NAME
	public static final String ORG_TYPE_HIERARCHY_LVL_1_NAME="Level 1";
	
	//FIELD LENGTH
	public static final int FEIN_FIELD_LENGTH=9;
	public static final int ISSUER_FIELD_LENGTH=5;

	//PAGE HEADING ATTR
	public static final String ATTR_PAGE_HEADING = "attrPageHeading";
	
	//HIPPA OPT OUT EMAIL Constants
	public static final String ORG_PK =  "ORG_PK";
	public static final String NONFED_PLAN_NAME = "NONFED_PLAN_NAME";
	
	//TPA Contansts
	public static final String LEGAL_ADDRESS_CODE="300";
	public static final String ESM_CODE="1";
	public static final String ESM_AUTHORIZING_OFFICIAL="4";
	public static final String ESM_PRIMARY_CONTACT="5";
	public static final String ESM_SECONDARY_CONTACT="6";
	public static final String ESM_SUPPLEMENTAL_ONE="7";
	public static final String ESM_SUPPLEMENTAL_TWO="8";
	public static final String BUSINESS_PHONE="203";
	
	//Other Org Type Constants
	public static final String OTHER_ORG_TYPE="301";
	public static final String BUSINESS_ADDRESS="302";
	public static final int OTHER_ORG_CREATION=304;
	public static final int PENDING_APPROVAL_STATUS=176;
}
