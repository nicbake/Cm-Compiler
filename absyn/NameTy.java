package absyn;

/*
  Program Information
  Authors: Nicholas Baker & Garrett Holmes
  File Name: NameTy.java
*/

public class NameTy extends Absyn {

  public TYPE typ;

  public NameTy(int row, int col, TYPE typ) {
    this.row = row;
    this.col = col;
    this.typ = typ;
  }

  public void accept( AbsynVisitor visitor, int level, boolean flag, int ptrOffset ) {
    visitor.visit( this, level, flag, ptrOffset );
  }
}
