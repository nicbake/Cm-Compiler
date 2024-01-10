package absyn;

/*
  Program Information
  Authors: Nicholas Baker & Garrett Holmes
  File Name: ArrayDec.java
*/

public class ArrayDec extends VarDec {
 
  public int size;

  public ArrayDec(int row, int col, NameTy typ, String name, int size) {
    this.row = row;
    this.col = col;
    this.typ = typ;
    this.name = name;
    this.size = size;
  }

  public void accept( AbsynVisitor visitor, int level, boolean flag, int ptrOffset ) {
    visitor.visit( this, level, flag, ptrOffset );
  }

  public int getSize() {
    if (size == 0) {
      return -1;
    }
    return (-1 * size);
  }
}