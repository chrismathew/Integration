package gov.hhs.cms.base.common.util;

import gov.hhs.cms.base.common.security.util.SecurityUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmailValidatorUtil{

	  private Pattern pattern;
	  private Matcher matcher;

	  private static final String EMAIL_PATTERN =
                   "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

	  /** this does not validate if there is "-" in the domain part
	   * Validate hex with regular expression
	   * @param hex hex for validation
	   * @return true valid hex, false invalid hex
	   */
	  public boolean validate(final String hex){
		  pattern = Pattern.compile(EMAIL_PATTERN);
		  matcher = pattern.matcher(hex);
		  return matcher.matches();
	  }

	  /**
	   * Validate hex with regular expression
	   * @param hex hex for validation
	   * @return true valid hex, false invalid hex
	   */
	  public static boolean validateCMSPattern(final String hex){
		  String EMAIL_PATTERN_CMS = FFEConfig.getProperty("email.pattern.cms", "^(?i)([a-z0-9&'?._%+-]+)@((?:[-a-z0-9]+\\.)+[a-z]{2,})$");
		  return SecurityUtil.isMatch(EMAIL_PATTERN_CMS, hex);
	  }

}
