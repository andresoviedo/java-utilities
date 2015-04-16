package org.andresoviedo.util.tree;

import java.util.ArrayList;
import java.util.List;

public class TreeNode<T> {

	private final T obj;
	private final List<TreeNode<T>> children;
	private TreeNode<T> parent;

	public TreeNode(T obj) {
		this(obj, new ArrayList<TreeNode<T>>());
	}

	public TreeNode(T obj, List<TreeNode<T>> children) {
		this.obj = obj;
		this.children = children;
	}

	public T getObject() {
		return obj;
	}

	public List<TreeNode<T>> getChildren() {
		return children;
	}

	public TreeNode<T> addChildren(T name) {
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
		sbuilder.append(prefix).append((isTail ? "└── " : "├── ")).append(obj).append("\n");
		for (int i = 0; i < children.size() - 1; i++) {
			children.get(i).print(prefix + (isTail ? "    " : "│   "), false, sbuilder);
		}
		if (children.size() > 0) {
			children.get(children.size() - 1).print(prefix + (isTail ? "    " : "│   "), true, sbuilder);
		}
	}

	public static void main(String[] args) {
		TreeNode<String> root = new TreeNode<String>("abuelo");
		TreeNode<String> son1 = root.addChildren("hijo 1");
		son1.addChildren("nieto 1").addChildren("biznieto 1");
		son1.addChildren("nieto 2").addChildren("biznieto 2");
		root.addChildren("hijo 2").addChildren("nieto 3");
		System.out.println(root);
	}

	public boolean isRoot() {
		return (this.parent == null);
	}

	public boolean isLeaf() {
		if (this.children.size() == 0)
			return true;
		else
			return false;
	}
}