package absyn;

/*
  Program Information
  Authors: Nicholas Baker & Garrett Holmes
  File Name: WhileExp.java
*/

public class WhileExp extends Exp {
  public Exp test;
  public Exp body;

  public WhileExp( int row, int col, Exp test, Exp body) {
    this.row = row;
    this.col = col;
    this.test = test;
    this.body = body;
  }

  public void accept( AbsynVisitor visitor, int level, boolean flag, int ptrOffset ) {
    visitor.visit( this, level, flag, ptrOffset );
  }
}