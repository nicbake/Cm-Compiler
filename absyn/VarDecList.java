package absyn;

import java.util.ArrayList;
import java.util.List;

/*
  Program Information
  Authors: Nicholas Baker & Garrett Holmes
  File Name: VarDecList.java
*/

public class VarDecList extends Absyn {
  public VarDec head;
  public VarDecList tail;

  public VarDecList(VarDec head, VarDecList tail) {
    this.head = head;
    this.tail = tail;
  }

  public void accept( AbsynVisitor visitor, int level, boolean flag, int ptrOffset ) {
    visitor.visit( this, level, flag, ptrOffset );
  }

  @Override
  public String toString() {

    String ret = "";
    if(head == null){
      return "null";
    }

    ret += "|" + head.name + ":" + head.typ.typ + "|";
    if(tail != null){
      ret += tail.toString();
    }

    return ret;
  }

  public int getSize() {
    int size = 0;
    VarDecList tmp = this;

    while (tmp != null) {
      size += tmp.head.getSize();
      tmp = tmp.tail;
    }
    return size;

  }

  // returns a list of types in the vardec list, used for error checking
  public ArrayList<TYPE> get_types(){

    if (head == null) {
      return null;
    }
    ArrayList<TYPE> ret = new ArrayList<TYPE>();

    ret.add(head.typ.typ);
    if (tail != null && tail.get_types() != null) {
      ret.addAll(tail.get_types());
    }

    return ret;

  }
}
