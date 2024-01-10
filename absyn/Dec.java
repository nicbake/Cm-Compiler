package absyn;

/*
  Program Information
  Authors: Nicholas Baker & Garrett Holmes
  File Name: Dec.java
*/

abstract public class Dec extends Absyn {
  public NameTy typ;
  public boolean isAddr;
  public int offset;
  public int nestLevel;
}
