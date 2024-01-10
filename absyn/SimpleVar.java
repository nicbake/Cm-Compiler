package absyn;

/*
  Program Information
  Authors: Nicholas Baker & Garrett Holmes
  File Name: SimpleVar.java
*/

public class SimpleVar extends Var {

  public SimpleVar(int row, int col, String name) {
    this.row = row;
    this.col = col;
    this.name = name;
  }

  public void accept( AbsynVisitor visitor, int level, boolean flag, int ptrOffset ) {
    visitor.visit( this, level, flag, ptrOffset );
  }
}
