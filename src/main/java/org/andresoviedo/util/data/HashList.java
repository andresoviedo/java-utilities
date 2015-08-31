package org.andresoviedo.util.data;

import java.util.AbstractList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * <p>
 * This class acts as a hashtable but provides a <code>java.util.List</code> interface for getting elements by their index position.
 * </p>
 * <p>
 * To get the <code>List</code> interface call the <code>getListInterface()</code> method. The first item in the returned list if no index
 * is specified when adding it to this <code>HashList</code> is the first item added by the <code>put(Object, Object)</code> method.
 * </p>
 * <p>
 * An item can be added at a specified position by calling <code>put(Object, Object, int)</code> method. This <code>List</code>
 * implementation does not support adding items.
 * 
 */
public class HashList<K, V> extends HashMap<K, V> {

	private static final long serialVersionUID = -3907761576605203693L;

	protected Vector<V> data = new Vector<V>();

	protected ListInterface listInterface = new ListInterface();

	/**
	 * Construtcs a new <code>HashList</code>.
	 */
	public HashList() {
		super();
	}

	/**
	 * Construtcs a new <code>HashList</code> and initializes it with the specified map.
	 */
	public HashList(Map<K, V> t, Vector<V> data) {
		super(t);
		this.data = data;
	}

	/*
	 * @see java.util.Hashtable#clear()
	 */
	@Override
	public void clear() {
		super.clear();
		data.clear();
	}

	@Override
	public V put(K key, V value) {
		V obj = super.put(key, value);
		if (obj != null) {
			data.remove(obj);
		}
		data.add(value);
		return obj;
	}

	/**
	 * Maps the specified <code>key</code> to the specified <code>value</code> at the specified <code>index</code> in this HashList.<br>
	 * Neither the key nor the value can be <code>null</code>. The value can be retrieved by calling the <code>get</code><br>
	 * method with a key that is equal to the original key or by calling the <code>elementAt(int)</code> method<br>
	 * of the List interface.
	 * 
	 * @param key
	 *            the hashtable key.
	 * @param value
	 *            the value.
	 * @return the previous value of the specified key in this hashtable, or <code>null</code> if it did not have one.
	 * @exception NullPointerException
	 *                if the key or value is <code>null</code>.
	 * @exception ArrayIndexOutOfBoundsException
	 *                if <code>index</code> is out of range ( <code>index &lt; 0</code> || <code>index &gt; size()</code>).
	 */
	public V put(K key, V value, int index) {
		V ret = super.put(key, value);
		if (ret != null) {
			data.remove(ret);
		}
		data.add(index, value);
		return ret;
	}

	/*
	 * @see java.util.Hashtable#remove(java.lang.Object)
	 */
	@Override
	public V remove(Object key) {
		V ret = super.remove(key);
		if (ret != null) {
			data.remove(ret);
		}
		return ret;
	}

	/**
	 * Returns the value at the specified index.
	 * 
	 * @param index
	 *            the index of the value to be returned.
	 * @return the value at the specified index.
	 */
	public V elementAt(int index) {
		return data.elementAt(index);
	}

	/**
	 * Shuffles the order of the elements in the access List.
	 */
	public void shuffle() {
		Collections.shuffle(data);
	}

	/**
	 * Returns a {@link List} of the components of this HashList. The first item in the returned <tt>List</tt> is the item at index
	 * <tt>0</tt> ,<br>
	 * then the item at index <tt>1</tt>, and so on. The first item, if no index is specified when adding it to this <tt>HashList</tt>, it's
	 * the first<br>
	 * item added by the {@link #put(Object, Object)} method. This <tt>List</tt> implementation does not support adding items.
	 * 
	 * @return a List of the components of this HashList.
	 * @see List
	 */
	public List<V> getListInterface() {
		return listInterface;
	}

	class ListInterface extends AbstractList<V> {

		/*
		 * @see java.util.AbstractList#get(int)
		 */
		public V get(int index) {
			return data.elementAt(index);
		}

		/*
		 * @see java.util.AbstractCollection#size()
		 */
		public int size() {
			return data.size();
		}
	}
}
