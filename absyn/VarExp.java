package absyn;

/*
  Program Information
  Authors: Nicholas Baker & Garrett Holmes
  File Name: VarExp.java
*/

public class VarExp extends Exp {
  public Var variable;

  public VarExp( int row, int col, Var variable ) {
    this.row = row;
    this.col = col;
    this.variable = variable;
  }

  public void accept( AbsynVisitor visitor, int level, boolean flag, int ptrOffset ) {
    visitor.visit( this, level, flag, ptrOffset );
  }
}
