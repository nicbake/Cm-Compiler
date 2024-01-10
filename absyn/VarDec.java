package absyn;

/*
  Program Information
  Authors: Nicholas Baker & Garrett Holmes
  File Name: VarDec.java
*/

abstract public class VarDec extends Dec {
  public String name;
  public int nestLevel;
  public int offset;
  public boolean isAddr;

  public int getSize() {
    if (this instanceof SimpleDec) {
      return ((SimpleDec)this).getSize();
    } else {
      return ((ArrayDec)this).getSize();
    }
  }
}
