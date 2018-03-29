package com.twc.eis.lib.string;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

public class StringHelper {
	
	public static String NEWLINE = null;
	public static final String[] NULL_STRING_ARRAY = new String[0];
	
	static {
		char temp[] = {(char)13, (char)10};
		NEWLINE = new String(temp);
	}
			
	// no instantiation
	private StringHelper(){}
	public static String append(String strings[]) {
		return append(strings, null);
	}
	public static String append(String strings[], String delimiter) {
		
		int strlen, pos = 0, len = 0, len2 = 0;
		int delimiterLen = delimiter==null ? 0: delimiter.length();
		
		
		for (int i = 0; i < strings.length; i++) 
			if (strings[i]!= null) {
				len += strings[i].length();
				len2 += delimiterLen;
			} 
			    	    	    	
		char buffer[] = new char[len + len2];

		for (int i = 0; i < strings.length; i++) {
			
			if (strings[i] != null) {
			
				strlen=strings[i].length();
				strings[i].getChars(0, strlen, buffer, pos);
				pos += strlen;
	    		
				if (delimiterLen > 0 && i < strings.length-1) {
					delimiter.getChars(0, delimiterLen, buffer, pos);
	    			pos += delimiterLen;
				}	    		
			}    		
		}

		return new String(buffer, 0, buffer.length - delimiterLen);    	    	
		
	}
	public static String append(String s1, String s2) {
		
		String temp[] = {s1,s2};
		
		return append(temp, null);
		
	}
	// returns the end portion of a token-delimited string: endString("com.quantumstream.qnet.PropertyManager",".") = "PropertyManager"
	public static String endString(String string, String delimiter) {

			int pos;

			if ( (pos = string.lastIndexOf(delimiter)) == -1 ) return null;

			else return string.substring(pos+1);
		

	}
	private static int indexOfToken(String string, String token, char delimiter, boolean ordinal) {

		int ordinalIndex = 0;
		int pos = 0;
		int i;
		boolean searching;
		boolean matches;
		char c;

		int tokenLen = token.length();
		int stringLen = string.length();

		searching = true;
		
		while (searching && pos < stringLen && pos > -1) {

			c = string.charAt(pos);	
			boolean whitespace = c == ' ' || c == '\t' || c == '\n';
			while (whitespace) {
				c = string.charAt(++pos);	
				whitespace = c == ' ' || c == '\t' || c == '\n';
			}

			matches = true;		
			i = 0;

			matches = string.charAt(pos+i) == token.charAt(i++);

			while (matches && i < tokenLen && string.charAt(pos+i) != delimiter) {

				matches = string.charAt(pos+i) == token.charAt(i++);

			}

			c = pos + i >= stringLen ? ' ' : string.charAt(pos+i);

			matches = matches && (c == ' ' || c == '\t' || c == '\n' || c == delimiter);

			if (matches) {
	
				if ((c == ' ' || c == '\t' || c == '\n' || c == delimiter) || pos + i >= stringLen) ordinalIndex++;

				searching = false;

			}
			else {

				pos = string.indexOf(delimiter, pos+i);
				ordinalIndex = pos == -1 ? ordinalIndex : ordinalIndex + 1;

				pos = pos == -1 ? pos : pos + 1;
			}
	
		}		
			
		return ordinal ? ordinalIndex : pos;

	}
	public static void main(String args[]) {

		/*System.out.println( replicate("=*",10) + "!");

		java.text.DateFormat df = java.text.DateFormat.getDateTimeInstance();

		System.out.println("Current Time: " + df.format(new java.util.Date()));

		System.out.println( padl("X",'0',9) );			

		System.out.println( tokenPos("a,b,c,cat,dog,e,house",args[0],",") );
		System.out.println(args[0]);
		System.out.println( tokenPos(args[0],args[1],',') );
		System.out.println( tokenNum(args[0],args[1],',') );
		*/

		//System.out.println( replaceInString("This is a test", new String[][] {{"This", "That"}, {"is", "was"}, {"a", "some really cool"}, {"test", "code"}}));

		System.out.println( count('/', "abcd"));
		
		System.out.println("done");

	}
	public static String padl(double num, char c, int len) {
		return padl( String.valueOf(num), c, len );
	}
	public static String padl(float num, char c, int len) {
		return padl( String.valueOf(num), c, len );
	}
	public static String padl(int num, char c, int len) {
		return padl( String.valueOf(num), c, len );
	}
	public static String padl(long num, char c, int len) {
		return padl( String.valueOf(num), c, len );
	}
	// pads a string to the left with a character
	public static String padl(String string, char c, int len) {
		
		if (len <= string.length()) return string;

		StringBuffer buff = new StringBuffer(len);

		for (int i = 0; i < len - string.length(); i++)
			buff.append(c);

		buff.append(string);

		return buff.toString();

	}
	public static String padl(short num, char c, int len) {
		return padl( String.valueOf(num), c, len );
	}

	public static String replaceInString(String string, String oldString, String newString) {
		
		int pos = 0;
		int oldLen = oldString.length();
		
		while (  (pos = string.indexOf(oldString, pos)) > -1 )
				  {
			string = stuff(string, newString, pos, oldLen);
						pos += newString.length();
				  }
		
		return new String(string);
	}

	
	public static String replaceInString(String string, String oldString, String newString, boolean ignoreCase) {
		
		if (! ignoreCase )
			return replaceInString(string, oldString, newString);
		
		int pos = 0;
		int oldLen = oldString.length();
		
		while (  (pos = string.toUpperCase().indexOf(oldString.toUpperCase(), pos)) > -1 )
				  {
			string = stuff(string, newString, pos, oldLen);
						pos += newString.length();
				  }
		
		return new String(string);
	}
	
	// replicates a string "count" times: replicate("*", 5) = "*****"
	public static String replicate(String string, int count) {

		if (count < 1 || string == null) return "";

		int size = count * string.length();

		StringBuffer buff = new StringBuffer( size );

		for (int i = 0; i < count; i++)	
			buff.append(string);
		
		return buff.toString();
	}
	// returns the start portion of a token-delimited string: startString("com.quantumstream.qnet.PropertyManager",".") = "com.quantumstream.qnet"
	public static String startString(String string, String delimiter) {

			int pos;

			if ( (pos = string.lastIndexOf(delimiter)) == -1 ) return string;

			else return string.substring(0, pos);
		
	}
	public static String stuff(String original, String insert, int offset) {
		return stuff(original, insert, offset, 0);
	}
	public static String stuff(String original, String insert, int offset, int overwrite) {
		
		int originalLen = original.length();
		int insertLen = insert.length();
		int newLen = originalLen + insertLen - (Math.min(insertLen, overwrite));
		
		if (offset < 0 || offset > originalLen)
			throw new StringIndexOutOfBoundsException(offset);
		if (overwrite < 0)
			throw new StringIndexOutOfBoundsException(overwrite);
			
		StringBuffer buffer = new StringBuffer(newLen);
		
		int len = Math.min( overwrite, originalLen - offset );
				
		char temp[] = new char[ Math.max(offset, originalLen - offset - len) ];
				
		original.getChars(0, offset, temp, 0);        
		buffer.append(temp, 0, offset);        
		
		buffer.append(insert);
				
		original.getChars(offset + len, originalLen, temp, 0);
		buffer.append(temp, 0, originalLen - offset - len);
		
		return buffer.toString();
		
		
	}
	
	
	public static int count(char ch, String string) {
		
		if ( string == null )
			return 0;
		
		int retval = 0;
		
		int pos = string.indexOf(ch);
				
		while ( pos > -1 ) {
		
			++retval;
			pos = string.indexOf(ch, pos+1);
		
		}
		
		return retval;
	}
	
	public static int tokenNum(String string, String token) {
		return tokenNum(string, token, ',');
	}
	// returns the ordinal position of a token in a string
	public static int tokenNum(String string, String token, char delimiter) {
		return indexOfToken(string, token, delimiter, true);
	}
	public static int tokenPos(String string, String token) {
		return tokenPos(string, token, ',');
	}
	// returns the absolute position of a token in a string
	// (to be a token it must be delimited within that string)
	public static int tokenPos(String string, String token, char delimiter) {
		return indexOfToken(string, token, delimiter, false);
	}
	// better version of replaceInString:
	// minimises memory-thrashing, and also faster
	public static String replaceInString(String string, String[][] replacementPair) {
	     return replaceInString(string, replacementPair, null, null);
	}

	// better version of replaceInString:
	// minimises memory-thrashing, and also faster
	public static String replaceInString(String string, String[][] replacementPair, String leftDelim, String rightDelim) {

		StringBuffer buffer = new StringBuffer(string);

		for (int i = 0; i < replacementPair.length; i++) {
				
			  String oldStr;
			  
			  if (leftDelim != null && rightDelim != null)
			  	oldStr = leftDelim + replacementPair[i][0] + rightDelim;
			  else
			  	oldStr = replacementPair[i][0];
			  	
			  String newStr = replacementPair[i][1];
			  
	        int lenOld = oldStr.length();
	        int lenNew = newStr.length();
	        
	        int pos = 0, pos2 = 0;

	        while( pos2 < buffer.length() && (pos = indexOf(buffer, oldStr, pos2)) != -1 ) {
		        buffer.replace(pos, pos + lenOld, newStr);
		        pos2 = pos + (lenNew - lenOld)+ 1; // to prevent recursive endless loop
		   }
	     }
	     return buffer.toString();
	}

 public static int indexOf(StringBuffer buffer, String str) {
	   return indexOf(buffer, str, 0); 
	}

	public static int indexOf(StringBuffer buffer, String str, int fromIndex) {

		int offset = 0;
		int count = buffer.length();
		int strCount = str.length();
		
		int max = offset + (count - strCount);
		if (fromIndex >= count) {
			if (count == 0 && fromIndex == 0 && strCount == 0) {
				/* There is an empty string at index 0 in an empty string. */
				return 0;
			}
			/* Note: fromIndex might be near -1>>>1 */
			return -1;
		}
		if (fromIndex < 0) {
			fromIndex = 0;
		}
		if (strCount == 0) {
			return fromIndex;
		}

		int strOffset = 0;//str.offset;
		char first = str.charAt(strOffset);
		int i = offset + fromIndex;

		startSearchForFirstChar : while (true) {

			/* Look for first character. */
			while (i <= max && buffer.charAt(i) != first) {
				i++;
			}
			if (i > max) {
				return -1;
			}

			/* Found first character, now look at the rest of v2 */
			int j = i + 1;
			int end = j + strCount - 1;
			int k = strOffset + 1;
			while (j < end) {
				if (buffer.charAt(j++) != str.charAt(k++)) {
					i++;
					/* Look for str's first char again. */
					continue startSearchForFirstChar;
				}
			}
			return i - offset; /* Found whole string. */
		}
	}

  /** Escape each occurrence of a quote or a backslash with a backslash.
   */
  public static String backSlashQuotes(String s)
  {
	return backSlashEscape(s, new char[] {'\"'});
  }      

  /** Escape each occurrence of any of the given characters or a
   * backslash with a backslash.
   */
  public static String backSlashEscape(String s, char[] charsToEscape)
  {
	try {
	  StringReader input = new StringReader(s);
	  StringWriter output = new StringWriter(s.length());

	  // Add backslash to charsToEscape:
	  {
		char[] charsToEscapeOrig = charsToEscape;
		charsToEscape = new char[charsToEscapeOrig.length + 1];
		for (int i = 0; i < charsToEscapeOrig.length; i++)
		  charsToEscape[i] = charsToEscapeOrig[i];
		charsToEscape[charsToEscapeOrig.length] = '\\';
	  }

	  int ch;
	  while (-1 != (ch = input.read()))
		{
		  for (int j = 0; j < charsToEscape.length; j++)
			{
			  if (ch == charsToEscape[j])
				{
				  output.write("\\");
				  break;
				}
			}
		  output.write(ch);
		}
	  return output.toString();
	} catch (IOException ioe) {
	  // Should never happen because we're reading/writing from/to strings.
	  ioe.printStackTrace();
	  return s; // for lack of something better to return
	}
  }
  
  
  
  
}