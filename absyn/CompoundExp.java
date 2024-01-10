package absyn;

/*
  Program Information
  Authors: Nicholas Baker & Garrett Holmes
  File Name: CompoundExp.java
*/

public class CompoundExp extends Exp {
  public VarDecList decs;
  public ExpList exps;

  public CompoundExp( int row, int col, VarDecList decs, ExpList exps) {
    this.row = row;
    this.col = col;
    this.decs = decs;
    this.exps = exps;
  }

  public void accept( AbsynVisitor visitor, int level, boolean flag, int ptrOffset ) {
    visitor.visit( this, level, flag, ptrOffset );
  }
}