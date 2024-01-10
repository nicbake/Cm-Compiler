package absyn;

/*
  Program Information
  Authors: Nicholas Baker & Garrett Holmes
  File Name: ReturnExp.java
*/

public class ReturnExp extends Exp {
  public Exp exp;

  public ReturnExp( int row, int col, Exp exp) {
    this.row = row;
    this.col = col;
    this.exp = exp;
  }

  public void accept( AbsynVisitor visitor, int level, boolean flag, int ptrOffset ) {
    visitor.visit( this, level, flag, ptrOffset );
  }
}