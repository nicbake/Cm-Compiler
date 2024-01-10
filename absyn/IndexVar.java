package absyn;

/*
  Program Information
  Authors: Nicholas Baker & Garrett Holmes
  File Name: IndexVar.java
*/

public class IndexVar extends Var {
  public Exp index;

  public IndexVar( int row, int col, String name, Exp index ) {
    this.row = row;
    this.col = col;
    this.name = name;
    this.index = index;
  }

  public void accept( AbsynVisitor visitor, int level, boolean flag, int ptrOffset ) {
    visitor.visit( this, level, flag, ptrOffset );
  }
}
