package absyn;

/*
  Program Information
  Authors: Nicholas Baker & Garrett Holmes
  File Name: NilExp.java
*/

public class NilExp extends Exp {

  public NilExp(int row, int col) {
    this.row = row;
    this.col = col;
  }

  public void accept( AbsynVisitor visitor, int level, boolean flag, int ptrOffset ) {
    visitor.visit( this, level, flag, ptrOffset );
  }
}
