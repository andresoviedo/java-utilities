package org.andresoviedo.util.tree;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a TreeNode in a generic data tree structure. Each
 * TreeNode has basically a parent, may have children and some data associated.
 * 
 * @author andresoviedo
 * 
 * @param <T>
 *            the type of the associated data
 */
public class TreeNode<T> {

	private TreeNode<T> parent;
	private List<TreeNode<T>> children;
	private T data;

	public TreeNode(T data) {
		this(null, data, null);
	}

	public TreeNode(T data, TreeNode<T> parent) {
		this(parent, data, null);
	}

	public TreeNode(TreeNode<T> parent, T obj, List<TreeNode<T>> children) {
		this.parent = parent;
		this.data = obj;
		this.children = children;
	}

	public TreeNode<T> getParent() {
		return parent;
	}

	public T getData() {
		return data;
	}

	public List<TreeNode<T>> getChildren() {
		return children;
	}

	public boolean isRoot() {
		return (this.parent == null);
	}

	public boolean isLeaf() {
		return this.children == null;
	}

	public TreeNode<T> addChild(T name) {
		if (children == null) {
			children = new ArrayList<TreeNode<T>>();
		}
		TreeNode<T> child = new TreeNode<T>(name);
		child.parent = this;
		this.children.add(child);
		return child;
	}

	@Override
	public String toString() {
		StringBuilder sbuilder = new StringBuilder();
		print("", true, sbuilder);
		return sbuilder.toString();
	}

	private void print(String prefix, boolean isTail, StringBuilder sbuilder) {
		sbuilder.append(prefix).append((isTail ? "└── " : "├── ")).append(data).append("\n");
		if (children == null) {
			return;
		}
		for (int i = 0; i < children.size() - 1; i++) {
			children.get(i).print(prefix + (isTail ? "    " : "│   "), false, sbuilder);
		}
		if (children.size() > 0) {
			children.get(children.size() - 1).print(prefix + (isTail ? "    " : "│   "), true, sbuilder);
		}
	}

}