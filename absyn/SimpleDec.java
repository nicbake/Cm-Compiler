package absyn;

/*
  Program Information
  Authors: Nicholas Baker & Garrett Holmes
  File Name: SimpleDec.java
*/

public class SimpleDec extends VarDec {

  public SimpleDec(int row, int col, NameTy typ, String name) {
    this.row = row;
    this.col = col;
    this.typ = typ;
    this.name = name;
  }

  public void accept( AbsynVisitor visitor, int level, boolean flag, int ptrOffset ) {
    visitor.visit( this, level, flag, ptrOffset );
  }

  public int getSize() {
    return -1;
  }
}