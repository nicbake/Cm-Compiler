package absyn;

/*
  Program Information
  Authors: Nicholas Baker & Garrett Holmes
  File Name: DecList.java
*/

public class DecList extends Absyn {
  public Dec head;
  public DecList tail;

  public DecList(Dec head, DecList tail) {
    this.head = head;
    this.tail = tail;
  }

  public void accept( AbsynVisitor visitor, int level, boolean flag, int ptrOffset ) {
    visitor.visit( this, level, flag, ptrOffset );
  }
}
