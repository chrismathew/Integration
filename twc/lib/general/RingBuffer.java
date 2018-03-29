/* See javadoc output for more info.
 *
 * $Date: 2001/04/10 02:43:59 $ 
 * 
 */

package com.twc.eis.lib.general;

/** A ring buffer.  A fast-access, fixed-length queue of characters.
 *
 * @author $Author: mcarter $
 * @version $Header: /home/cvsroot/qS/1.0/Development/source/java/com.twc.eis/lib/general/RingBuffer.java,v 1.12 2001/04/10 02:43:59 mcarter Exp $
 */
public class RingBuffer
{
  private StringBuffer buf;
  private int head; // index in buf of next char to be written
  private int tail; // index in buf of next char to be read

  /* Data gets added to buf in the direction of increasing index.  Data
   *   wraps around to the beginning when it reaches the end.  The beginning
   *   and the end are hidden from the client.  Buf actually has one more
   *   byte than the client knows about.  This is to allow us to know the
   *   difference between completely full and completely empty:
   *   full === (tail + 1)%buf.size == head
   *   empty === tail == head
   *
   * Example:
   * Code                                    What rb looks like internally
   * ----                                    -----------------------------
   * RingBuffer rb = new RingBuffer(16);     |                 |
   *                                          ^--head
   *                                          ^--tail
   *
   * rb.addToHead("abcdefgh");               |abcdefgh         |
   *                                                  ^--head
   *                                          ^--tail
   *
   * String residue = rb.removeFromTail(8);  |abcdefgh         |
   *                                                  ^--head
   *                                                  ^--tail
   *
   * rb.addToHead("Hello there");            |recdefghHello the|
   *                                            ^--head
   *                                                  ^--tail
   */

  /** Exception that gets thrown whenever an operation would make
   *  head reach tail.
   */
  public static class InsufficientSpace extends Exception {
	public InsufficientSpace() {}
	public InsufficientSpace(String s) {super(s);}
  }

  /** Exception that gets thrown whenever an operation would make
   *  tail pass head.
   */
  public static class InsufficientData extends Exception {
	public InsufficientData() {}
	public InsufficientData(String s) {super(s);}
  }

  /** Constructs a RingBuffer from a String.
   * <code>
   *   RingBuffer rb = new RingBuffer(str);
   * </code>
   * is equavalent to:
   * <code>
   *   RingBuffer rb = new RingBuffer(str.length());
   *   rb.addToHead(str);
   * </code>
   */
  public
  RingBuffer(String str)
  {
	this(str.length()+1);
	try {
	  this.addToHead(str);
	} catch (InsufficientSpace impossible_error) {
	  // There is no way for this error to occur
	}
  }  

  /** Constructs an empty RingBuffer of the given size.
   */
  public
  RingBuffer(int size)
  {
	buf = new StringBuffer(size+1);
	head = 0;
	tail = 0;
  }  

  /** Tells how many free chars this RingBuffer has available.
   */
  public int charsAvailable()
  {
	return buf.capacity() - 1 - charsUsed();
  }  

  /** Tells how many chars are currently being used in this RingBuffer.
   */
  public int charsUsed()
  {
	int ret_val = head - tail;
	if (ret_val < 0) // wrap case
	  ret_val += buf.capacity();
	return ret_val;
  }  

  /** Adds a char to the ring buffer.
   * See {@link #addToHead(String) addToHead(String)}.
   * @throws InsufficientSpace See {@link InsufficientSpace InsufficientSpace}.
   */
  public void addToHead(char ch)
	throws InsufficientSpace
  {
	addToHead(new String(new char [] {ch}));
  }  

  /** Adds a String to the ring buffer.
   * @throws InsufficientSpace See {@link InsufficientSpace InsufficientSpace}.
   */
  public void addToHead(String str)
	throws InsufficientSpace
  {
	if (charsAvailable() < str.length())
	  throw new InsufficientSpace();
	if (str.length() > buf.capacity() - head)
	  { // needs to wrap
		// Replace the part before the wrap
		buf.replace(head,
					buf.capacity(),
					str.substring(0, buf.capacity() - head));
		// Replace the part after the wrap
		buf.replace(0,
					str.length() - (buf.capacity() - head),
					str.substring(buf.capacity() - head));
	  }
	else
	  { // no wrap
		buf.replace(head, head + str.length(), str);
	  }
	/* If, as the StringBuffer.replace() documentation claims, replace()
	 *   is implemented by deleting then inserting, then this is inefficient
	 *   and should not be used.  Raw access on a char[] should be used
	 *   instead.  And whoever implemented StringBuffer.replace() should
	 *   get smacked.
	 */

	// Advance head
	head += str.length();
	if (head >= buf.capacity())
	  head -= buf.capacity();
  }  

  /** Removes (in FIFO order) a char from the ring buffer.
   * See {@link #removeFromTail(int) removeFromTail(int)}.
   * @throws InsufficientData See {@link InsufficientData InsufficientData}.
   */
  public char removeCharFromTail()
	throws InsufficientData
  {
	return removeFromTail(1).charAt(0);
  }  

  /** Removes (in FIFO order) a String of the given size from the ring buffer.
   * @throws InsufficientData See {@link InsufficientData InsufficientData}.
   */
  public String removeFromTail(int size)
	throws InsufficientData
  {
	String ret_val;

	ret_val = peekAtTail(size);

	// Advance tail
	tail += size;
	if (tail >= buf.capacity())
	  tail -= buf.capacity();

	return ret_val;
  }  

  /** Tells whether the data in this RingBuffer is equivalent to the content
   * of the given String.
   * <p>
   * Equivalent to <code> this.toString().equals(str) </code>
   */
  public boolean contentEquals(String str)
  {
	return this.toString().equals(str);

	/* We are forced to use the inefficient implementation above because
	 *   StringBuffer does not provide a regionMatches() function as
	 *   String does.  Because of this, we should be using a char[] instead
	 *   of a StringBuffer as our underlying data holder, but that would be
	 *   a lot more coding.
	 */
  }  

  /** Exactly like {@link #removeFromTail(int) removeFromTail}, except the
   * data is not removed.
   */
  public String peekAtTail(int size)
	throws InsufficientData
  {
	String ret_val;
	if (charsUsed() < size)
	  throw new InsufficientData();
	if (size > buf.capacity() - tail)
	  { // needs to wrap
		// Get the part before the wrap
		ret_val = buf.substring(tail);
		// Get the part after the wrap
		ret_val += buf.substring(0, size - (buf.capacity() - tail));
	  }
	else
	  { // no wrap
		ret_val = buf.substring(tail, tail + size);
	  }
	return ret_val;
  }  

  /** Return the content of this RingBuffer as a String.
   */
  public String toString()
  {
	try {
	  return peekAtTail(charsUsed());
	} catch (InsufficientData impossible_error) {
	  // There is no way for this error to occur
	  return new String();
	}
  }  

  /** Unit-test driver.
   */
  public static void main(String[] args)
  {
	RingBuffer rb = new RingBuffer(9);
	try {
	  for (int i = 0; i < 20; i++)
		{
		  rb.addToHead("hello");
		  System.out.println(i + ": " + rb.removeFromTail(5));
		}
	  rb.addToHead("abcdefgh");
	  System.out.println(rb);
	  rb.removeFromTail(3);
	  rb.addToHead('i');
	  System.out.println(rb);
	} catch (Exception e) {
	  e.printStackTrace(System.out);
	}
  }  
}
