package com.twc.eis.lib.string;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class RegexHelper {
	
	private static Map<String, Pattern> cachedPatterns;
	private static Object semaphore = new Object();
	
	static {
		
		setCachedPatterns( new java.util.HashMap<String, Pattern>() );
					
	}
	
	
	private static final Map<String, Pattern> getCachedPatterns() {
		return cachedPatterns;
	}

	
	public static final Pattern getPattern( String expression ) {
		
		Pattern pattern = null;
		
		pattern = (Pattern) getCachedPatterns().get(expression.toUpperCase());
		
		if (pattern == null) {
			
			pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
			
			synchronized( semaphore ) {
			
				getCachedPatterns().put(expression.toUpperCase(), pattern);
			
			}
			
		}
		
		return pattern;
		
	}
	
	
	private static final void setCachedPatterns(Map<String, Pattern> cachedPatterns) {
		RegexHelper.cachedPatterns = cachedPatterns;
	}

	
	public static String[] find(String source, String... patterns) {
		
		List<String> results = new ArrayList<String>();
		
		Pattern p;
		Matcher m;
		String  s;
		
		for (String pattern : patterns) {
			
			//pattern = preprocess(scanExpression);
						
			p = getPattern(pattern);
			
			m = p.matcher(source);
			
			while ( m.find()  ) {
				
				s = m.group(0);
				
				//s = postprocess(s, scanExpression);
			
				results.add( s );
				
				
				
			}
			
		}
		
		return results.toArray( StringHelper.NULL_STRING_ARRAY);
	}
	
	
	public static String[] sscanf(String source, String... patterns) {
		
		List<String> results = new ArrayList<String>();
		
		Pattern p;
		Matcher m;
		String pattern, s, source2;
		
		for (String scanExpression : patterns) {
						
			//String[] pats = scanExpression.split(" ", 0);
			
			String[] pats = find(scanExpression, "[^(%s|%\\*s)]*(%s|%\\*s)[^(%s|%\\*s)]*");

			
			source2 = source;

			for ( int i = 0; i < pats.length; i++ ) {				
				
				String pat = pats[i];
				
				pattern = preprocess(pat);

				p = getPattern(pattern);
				
				m = p.matcher(source2);
				
				if ( m.find()  ) {
					
					s = m.group(0);
					
					if ( ! (pat.indexOf("*") > -1)  ) {
					
						s = postprocess(s, scanExpression, pats[i]);
					
						results.add( s );
										
					
				}
				
				//source2 = m.replaceFirst("");
				source2 = source2.substring( m.end() );
				
			}
				
		}
		
		}
		
		return results.toArray( StringHelper.NULL_STRING_ARRAY);
	}
	
	/*
	 * This method will strip out the delimiters that were in the scan expression
	 * in order to simulate the behaviour of c++ sscanf function
	 * by returning only the pure string that was bracketed by the delimiters
	 * 
	 * 
	 * 
	 */
	/*
	private static String postprocess(String result, String scanExpression, String pattern) {
		
		scanExpression = scanExpression.replaceAll("%s", "`");
		scanExpression = scanExpression.replaceAll("%\\*s", "`");
		
		scanExpression = scanExpression.replaceAll("%c", "`");
		
		String formatArtefact;
		
		for (int i = 0; i < scanExpression.length(); i++) {
			
			formatArtefact = scanExpression.substring(i,i+1);
			
			if ( result.indexOf( formatArtefact ) > -1 )
				result = result.replaceAll(formatArtefact, "");
				
			
		}
		
		return result;
		
	}
	*/
	
	private static String postprocess(String result, String scanExpression, String pattern) {
		
		
		pattern = pattern.replace("%s", "`");
		pattern = pattern.replace("%\\*s", "`");
		
		int len1 = pattern.length();		
		int len = result.length() - pattern.length() + 1 ;

		String placeHolder = StringHelper.replicate("`",  len);
		
		pattern = pattern.replace("`", placeHolder);				
		
		StringBuffer retval = new StringBuffer();
		
		for (int i = 0; i < pattern.length(); i++) {
			
			if (pattern.charAt(i) == '`') {
				
				retval.append( result.charAt(i) );
				
			}
			
		}
		
		return retval.toString();
		
	}
	
	private static String preprocess(String pattern) {
		
		// match a string - %s
		pattern = pattern.replaceAll("%s", "\\\\S+\\\\s*");

		// match a string - %s
		pattern = pattern.replaceAll("%\\*s", "\\\\S+\\\\s*");
		
		// match a character - %c
		pattern = pattern.replaceAll("%c", "\\\\S");
		
		return pattern;
		
	}
	
	private void test_sscanf() {
		
		String test;
		
		//test = "one, two,three";
		//test = "@value@;@name@;#link";		
		test = "Rudolph is 12 years old@id=phonenumber";
		
		//test = "@id@=phonenumber";
		
		String[] result;
		
		//result = sscanf(test, "%s=%s");
		
		//result = sscanf("Rudolph is 12 years old@id=phonenumber", "%s %*s %s %*s %s@%s=%snumber");
		//result = sscanf("Rudolph is 12 years old@id=phonenumber", "%*s %s %*s %s %*s@%s=phone%s");
		//result = sscanf("@value@;@name@;#link", "@%s@;@%s@;#%s");
		result = sscanf("rrCommercialAccount==rrCommercialAccount || rrResidentialAccount", "%s==%s");
		
		
		for (String s: result)			
			System.out.println(s);

	}
	
	private void test_find() {
		
		String test;
		
		test = "@value@;@name@;%link";		
		test = "%sabc%*sdef%*sghi%sjkl";
		
		String[] result;
				
		//result = find(test, "@\\S+@", "%\\S+");
		
		result = find(test, "[^(%s|%\\*s)]*(%s|%\\*s)[^(%s|%\\*s)]*");
		
		for (String s: result)			
			System.out.println(s);

	}

	
	public static void main(String args[]) {
		
		RegexHelper h = new RegexHelper();
		
		//h.test_sscanf();
		
		Pattern p = h.getPattern(".*[\\w].*");
		
		Matcher m = p.matcher("31415");
		
		boolean matches = m.lookingAt();
		
		System.out.println(matches);
		
		//h.test_find();
								
		
	}
	
	

}
