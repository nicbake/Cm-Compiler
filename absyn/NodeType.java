package absyn;

/*
  Program Information
  Authors: Nicholas Baker & Garrett Holmes
  File Name: IfExp.java
*/

public class NodeType {
  public String name;
  public int level;
  public Dec def;

  public NodeType(String name, int level, Dec def ) {
    this.name = name;
    this.level = level;
    this.def = def;
  }
}

