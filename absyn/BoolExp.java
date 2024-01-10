package absyn;

/*
  Program Information
  Authors: Nicholas Baker & Garrett Holmes
  File Name: BoolExp.java
*/

public class BoolExp extends Exp {
  public boolean value;

  public BoolExp( int row, int col, boolean value ) {
    this.row = row;
    this.col = col;
    this.value = value;
  }

  public void accept( AbsynVisitor visitor, int level, boolean flag, int ptrOffset ) {
    visitor.visit( this, level, flag, ptrOffset );
  }
}
