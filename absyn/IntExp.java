package absyn;

/*
  Program Information
  Authors: Nicholas Baker & Garrett Holmes
  File Name: IntExp.java
*/

public class IntExp extends Exp {
  public int value;

  public IntExp( int row, int col, int value ) {
    this.row = row;
    this.col = col;
    this.value = value;
  }

  public void accept( AbsynVisitor visitor, int level, boolean flag, int ptrOffset ) {
    visitor.visit( this, level, flag, ptrOffset );
  }
}
