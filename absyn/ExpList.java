package absyn;

import java.util.ArrayList;

/*
  Program Information
  Authors: Nicholas Baker & Garrett Holmes
  File Name: ExpList.java
*/

public class ExpList extends Absyn {
  public Exp head;
  public ExpList tail;
  public boolean isAddr;

  public ExpList(Exp head, ExpList tail) {
    this.head = head;
    this.tail = tail;
  }

  public void accept( AbsynVisitor visitor, int level, boolean flag, int ptrOffset ) {
    visitor.visit( this, level, flag, ptrOffset );
  }

  // returns a list of types in the exp list, used for error checking
  public ArrayList<TYPE> get_types() {

    if (head == null) {
      return null;
    }
    ArrayList<TYPE> ret = new ArrayList<TYPE>();

    ret.add(head.dtype);
    if (tail != null && tail.get_types() != null) {
      ret.addAll(tail.get_types());
    }

    return ret;

  }

}
