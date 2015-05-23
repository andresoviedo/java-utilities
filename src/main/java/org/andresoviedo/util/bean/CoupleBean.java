package org.andresoviedo.util.bean;

/**
 * A couple object encapsulates 2 objects, possibly being related.
 * 

 */
public class CoupleBean {

	/**
	 * The first member.
	 */
	private Object member1;

	/**
	 * The second member.
	 */
	private Object member2;

	/**
	 * Creates a new couple.
	 * 
	 * @param member1
	 *          the first member.
	 * @param member2
	 *          the second member.
	 */
	public CoupleBean(Object member1, Object member2) {
		this.member1 = member1;
		this.member2 = member2;
	}

	/**
	 * Returns the first member.
	 * 
	 * @return the first member.
	 */
	public Object getMember1() {
		return member1;
	}

	/**
	 * Sets the first member.
	 * 
	 * @param member1
	 *          the first member.
	 */
	public void setMember1(Object member1) {
		this.member1 = member1;
	}

	/**
	 * Returns the second member.
	 * 
	 * @return the second member.
	 */
	public Object getMember2() {
		return member2;
	}

	/**
	 * Sets the second member.
	 * 
	 * @param member2
	 *          the second member.
	 */
	public void setMember2(Object member2) {
		this.member2 = member2;
	}

}