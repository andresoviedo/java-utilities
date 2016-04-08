package org.andresoviedo.util.tree;

import org.junit.Test;

public class TreeNodeTest {

	@Test
	public void testToString() {
		TreeNode<String> root = new TreeNode<String>("abuelo");
		TreeNode<String> son1 = root.addChild("hijo 1");
		son1.addChild("nieto 1").addChild("biznieto 1");
		son1.addChild("nieto 2").addChild("biznieto 2");
		root.addChild("hijo 2").addChild("nieto 3");
		System.out.println(root);
	}

}
