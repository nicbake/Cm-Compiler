package absyn;

/*
  Program Information
  Authors: Nicholas Baker & Garrett Holmes
  File Name: FunctionDec.java
*/

public class FunctionDec extends Dec {
  public String func;
  public int funaddr;
  public VarDecList params;
  public Exp body;

  public FunctionDec(int row, int col, NameTy typ, String func, VarDecList params, Exp body) {
    this.row = row;
    this.col = col;
    this.typ = typ;
    this.func = func;
    this.params = params;
    this.body = body;
  }

  public void accept( AbsynVisitor visitor, int level, boolean flag, int ptrOffset ) {
    visitor.visit( this, level, flag, ptrOffset );
  }

  /*public int getParamSize() {
    if (params != null) {
      return params.getSize();
    }
    return 0;
  }*/
}