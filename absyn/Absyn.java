package absyn;

/*
  Program Information
  Authors: Nicholas Baker & Garrett Holmes
  File Name: Absyn.java
*/

abstract public class Absyn {
  public int row, col;

  abstract public void accept( AbsynVisitor visitor, int level, boolean flag, int ptrOffset );
}
