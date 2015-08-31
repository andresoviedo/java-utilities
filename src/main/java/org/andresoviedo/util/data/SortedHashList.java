package org.andresoviedo.util.data;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * This class extends the <tt>HashList</tt> class and adds support for sorting the elements by means of a <tt>java.util.Comparator</tt>.
 * Each time an element is added or removed the <tt>List</tt> is sorted. To create <tt>SortedHashList</tt> with sorting capability, a
 * non-null comparator must be set using the <tt>SortedHashList(Comparator)</tt> constructor or the <tt>setComparator(Comparator)</tt>
 * method.
 * 
 * @see SortedHashList
 * @see Comparator
 * @see List
 * 
 */
public class SortedHashList extends HashList {

	private Comparator comparator;

	private boolean autoSort;

	/**
	 * Creates a new <code>SortedHashList</code>.
	 */
	public SortedHashList() {
		this(null);
		this.autoSort = true;
	}

	/**
	 * Creates a new <code>SortedHashList</code> with the specified comparator.
	 * 
	 * @param comparator
	 *            the comparator.
	 */
	public SortedHashList(Comparator comparator) {
		super();
		this.comparator = comparator;
	}

	public Object put(Object key, Object value) {
		Object ret = super.put(key, value);
		if (autoSort) {
			sort();
		}
		return ret;
	}

	public Object remove(Object key) {
		Object ret = super.remove(key);
		if (autoSort) {
			sort();
		}
		return ret;
	}

	/**
	 * Returns the comparator to sort the values.
	 * 
	 * @return the comparator to sort the values.
	 */
	public Comparator getComparator() {
		return comparator;
	}

	/**
	 * Sets the comparator to sort the values.
	 * 
	 * @param comparator
	 *            the comparator.
	 */
	public void setComparator(Comparator comparator) {
		this.comparator = comparator;
		if (autoSort) {
			sort();
		}
	}

	/**
	 * Returns wether autosorting is enabled or not.
	 * 
	 * @return <code>true</code> if autosorting is enabled, <code>false</code> otherwise.
	 */
	public boolean isAutoSort() {
		return autoSort;
	}

	/**
	 * Specifies whether the List must be sorted automatically or not after adding/removing elements.
	 * 
	 * @param autoSort
	 */
	public void setAutoSort(boolean autoSort) {
		this.autoSort = autoSort;
	}

	/**
	 * Sorts the values. If the comparator is <code>null</code>, this method does nothing.
	 */
	public void sort() {
		if (comparator != null) {
			Collections.sort(data, comparator);
		}
	}

}