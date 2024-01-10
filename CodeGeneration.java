import absyn.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.HashMap;

/*
  Program Information
  Authors: Nicholas Baker & Garrett Holmes
  File Name: ShowTreeVisitor.java
  Adapted from: Fei Song
*/

public class CodeGeneration implements AbsynVisitor {
    
    public HashMap<String, ArrayList<NodeType>> table;
    public ArrayList<String> tm_output;
    public int mainEntry;
    public int globalOffset;

    public final static int PC = 7;
    public final static int GP = 6;
    public final static int FP = 5;
    public final static int AC = 0;
    public final static int AC1 = 1;

    public static int emitLoc = 0;
    public static int highEmitLoc = 0;

    // add constructor and all emitting routines
    CodeGeneration(Absyn trees) {
      this.table = new HashMap<String, ArrayList<NodeType>>();
      this.tm_output = new ArrayList<String>();

      createSymbol("input");
      insert("input", new NodeType("input", 0, new FunctionDec(0, 0, new NameTy(0,0,TYPE.INT), "input", null, null)));
      
      FunctionDec outputFunc = createOutput();
      createSymbol("output");
      insert("output", new NodeType("output", 0, outputFunc));

      visit(trees);
    
    
    }

    // prints the contents of the tm_output arraylist to a file
    public void write_output(String filename) {

      // create file if not exists
      // (https://www.w3schools.com/java/java_files_create.asp)
      try {
        File myObj = new File(filename);
        myObj.createNewFile();
      } catch (IOException e) {
        System.err.println("An error occurred.");
        e.printStackTrace();
      }

      // write to the file
      try {
        FileWriter myWriter = new FileWriter(filename);

        for (String s : this.tm_output) {
          myWriter.write(s + "\n");
        }

        myWriter.close();

      } catch (IOException e) {
        System.err.println("An error occurred.");
        e.printStackTrace();
      }
    }

    // Symbol table functions
    public FunctionDec createOutput() {
        
        SimpleDec dec = new SimpleDec(0,0,new NameTy(0,0,TYPE.INT),"x");
        VarDecList list = new VarDecList(dec,null);

        FunctionDec func = new FunctionDec(0,0,new NameTy(0,0,TYPE.VOID),"output",list,null);

        return func;
    }
    // if an element exists (has been declared), return the most recent type for the current scope, otherwise returns null
    public NodeType lookup(String name) {
        if (table.containsKey(name)) {
            // check the arraylist for the element with the given name
            ArrayList<NodeType> list = table.get(name);

            int indexToGet = list.size() - 1;
            // in case the length is 0
            if(indexToGet < 0){
                return null;
            }

            // Return the last element in the list (the most recent scope)
            return list.get(list.size() - 1);
        }
        return null;
    }

    // adds an element to the hashmap/table
    public void createSymbol(String name) {
        table.put(name, new ArrayList<NodeType>());
    }

    // updates the most recent type for a node with the given name
    public void insert(String name, NodeType def) {
        table.get(name).add(def);
        // tm_output.add("insert " + name);
        // print_table_out();
    }

    // ------------------------ emit functions --------------------------

    public void emitRO( String op, int r, int s, int t, String c) { // sends instructions like sub and add
      tm_output.add(emitLoc + ": "+ op + " " + r + "," + s + "," + t + "\t" + c);
      ++emitLoc;
      if ( highEmitLoc < emitLoc) {
        highEmitLoc = emitLoc;
      }
    }

    public void emitRM( String op, int r, int d, int s, String c) { // sends an instruction to the file
      tm_output.add(emitLoc + ": "+ op + " " + r + "," + d + "(" + s + ")\t" + c);
      ++emitLoc;
      if ( highEmitLoc < emitLoc) {
        highEmitLoc = emitLoc;
      }
    }

    public void emitRM_Abs( String op, int r, int a, String c) { 
      tm_output.add(emitLoc + ": "+ op + " " + r + "," + (a - (emitLoc + 1)) + "(" + PC + ")\t" + c);
      ++emitLoc;
      if ( highEmitLoc < emitLoc) {
        highEmitLoc = emitLoc;
      }
    }

    public int emitSkip( int distance ) {
      int i = emitLoc;
      emitLoc += distance;
      if ( highEmitLoc < emitLoc) {
        highEmitLoc = emitLoc;
      }
      return i;
    }

    public void emitComment(String c) { // Sends comments to the file
      tm_output.add("* " + c);
    }

    public void emitBackup(int loc) { // Sets the line number back to the savedloc
      if (loc > highEmitLoc) {
        emitComment("BUG in emitBackup");
      }
      emitLoc = loc;
    }

    public void emitRestore() { // Reset the emitloc back to the original line number
      emitLoc = highEmitLoc;
    }

    // ------------------------------------ Visitors --------------------------------------

    public void visit(Absyn trees) { // Wrapper for post-order traversal
        // Generate the prelude

        emitComment("C-Minus Compilation to TM Code");
        emitComment("Standard prelude:");
        emitRM("LD", GP, 0, AC, "load gp with maxaddress");
        emitRM("LDA", FP, 0, GP, "copy to gp to fp");
        emitRM("ST", AC, 0 , AC, "clear location 0"); 
        
        // System.err.println("prelude");

        int savedLoc = emitSkip(1);

        // Generate the i/o routines
        emitComment("C-Minus Compilation to TM Code");
        emitComment("code for input routine");
        emitRM("ST", 0, -1, FP, "store return");
        emitRO("IN", 0, 0, 0, "input");
        emitRM("LD", PC, -1, FP, "return to caller");

        // System.err.println("Input");

        emitComment("code for output routine");
        emitRM("ST", 0, -1, FP, "store return");
        emitRM("LD", 0, -2, FP, "load output value");
        emitRO("OUT", 0, 0, 0, "output");
        emitRM("LD", 7, -1, FP, "return caller");
        // System.err.println("Output");

        int savedLoc2 = emitSkip(0);
        emitBackup(savedLoc);
        emitRM_Abs("LDA", PC, savedLoc2, "jump around i/o code");
        emitRestore();
        emitComment("End of standard prelude.");

        // System.err.println("end of prelude");
        
        // Make a request to the visit method for DecList
        trees.accept(this, 0, false, 0);

        // Generate finale
        emitComment("Finale Generation");
        emitRM("ST", FP, globalOffset, FP, "push ofp");
        emitRM("LDA", FP, globalOffset, FP, "push frame");
        emitRM("LDA", 0, 1, PC, "load ac with ret ptr");
        emitRM_Abs("LDA", PC, 0, "jump to main loc");
        emitRM("LD", FP, 0, FP, "pop frame");
        emitRO("HALT", 0, 0, 0, "HALT");

        // System.err.println("finale");

    }

    /* NameTy */
    public void visit( NameTy ty, int level, boolean flag, int ptrOffset ) {
    }

    /* Variables */
    public void visit( SimpleVar var, int level, boolean flag, int ptrOffset ) { // Print SimpleVar

      // System.err.print("visit SimpleVar: " + var.name);

      NodeType id = lookup(var.name);
      if (id != null) {
        int nestLevel = 0;
        int offset = id.def.offset;
        if (id.def.nestLevel != 0) {
          nestLevel = 1;
        }

        if (flag) {
          emitRM("LDA", AC, offset, nestLevel, "load var '"+var.name+"' addr to ac");
          emitRM("ST", AC, ptrOffset, FP, "store '"+var.name+"' addr in memory");
        } else {
          emitRM("LD", AC, offset, nestLevel, "load var '"+var.name+"' to ac");
          emitRM("ST", AC, ptrOffset, FP, "store '"+var.name+"' in memory");
        }

      }
    }

    public void visit( IndexVar var, int level, boolean flag, int ptrOffset ) { // Print IndexVar
      level++;
      var.index.accept(this, level, false, ptrOffset);
      // System.err.println("visit IndexVar: " + var.name);

    }

    /* Expressions */
    public void visit( NilExp exp, int level, boolean flag, int ptrOffset ) { // Print NilExp
    }

    public void visit( IntExp exp, int level, boolean flag, int ptrOffset ) { // Print IntExp
      // System.err.println(" <- visit IntExp: " + exp.value);
      emitRM("LDC", AC, exp.value, 0, "load integer " + exp.value + " to ac");
      emitRM("ST", AC, ptrOffset, FP, "store integer " + exp.value + " in memory");
    }

    public void visit( BoolExp exp, int level, boolean flag, int ptrOffset ) { // Print BoolExp
      // System.err.println(" <- visit BoolExp: " + exp.value);

      emitComment("-> constant");
      emitComment("<- constant");
    }

    public void visit( VarExp exp, int level, boolean flag, int ptrOffset ) { // Print VarExp
      emitComment("-> id");

      level++;
      exp.variable.accept(this, level, flag, ptrOffset);
      // System.err.print(" <- visit VarExp: " + exp.variable.name);

      emitComment("looking up id: " + exp.variable.name);
      emitRM("LDA", AC, --ptrOffset, FP, "load id address");
      emitComment("<- id");
    }

    public void visit( CallExp exp, int level, boolean flag, int ptrOffset ) { // Print CallExp
      emitComment("-> call of function: " + exp.func);

      ExpList list = exp.args;
      level++;
      while (list != null) { // Check if list is null
        list.head.accept(this, level, false, 0);
        list = list.tail;
      }
      // System.err.println(" <- visit CallExp: " + exp.func);

      emitComment("<- call");
    }

    public void visit( OpExp exp, int level, boolean flag, int ptrOffset ) { // Print OpExp and operation
      emitComment("-> op");
      if (exp.left != null) // Check if null
        exp.left.accept( this, level, false, --ptrOffset );
        emitRM("ST", AC, --ptrOffset, FP, "op: push left");
      exp.right.accept( this, level, false, ptrOffset );

      // System.err.println(" <- visit OpExp: " + exp.op);

      emitComment("<- op");
    }

    public void visit( AssignExp exp, int level, boolean flag, int ptrOffset ) { // Print AssignExp

      // System.err.print("visit AssignExp -> ");
      level++;

      emitComment("-> op");
      exp.lhs.accept(this, level, true, --ptrOffset);
      emitRM("ST", AC, ptrOffset, FP, "op: push left");
      exp.rhs.accept(this, level, false, --ptrOffset);
      emitComment("<- op");
    }

    public void visit( IfExp exp, int level, boolean flag, int ptrOffset ) { // Print IfExp chain
      level++;

      // System.err.println("visit IfExp");

      emitComment("-> if");
      exp.test.accept( this, level, false, --ptrOffset );
      exp.thenpart.accept( this, level, false, --ptrOffset );
      if (exp.elsepart != null ) // Check if null
        exp.elsepart.accept( this, level, false, --ptrOffset );
      emitComment("<- if");
    }

    public void visit( WhileExp exp, int level, boolean flag, int ptrOffset ) { // Print WhileExp
      emitComment("-> while");
      emitComment("while: jump after body comes back here");

      // System.err.println("visit WhileExp");
      level++;
      exp.test.accept(this, level, false, 0);
      exp.body.accept(this, level, false, 0);

      emitComment("<- while");
    }


    public void visit( ReturnExp exp, int level, boolean flag, int ptrOffset ) { // Print ReturnExp
      emitComment("-> return");
      exp.exp.accept( this, level, false, ptrOffset );
      emitComment("<- return");
    }

    public void visit( CompoundExp exp, int level, boolean flag, int ptrOffset ) { // Print CompoundExp
      emitComment("-> compound statement");
      level++;

      VarDecList vlist = exp.decs;
      while( vlist != null) { // Check if null
        vlist.head.accept( this, level, false, ptrOffset );
        vlist = vlist.tail;
      }

      ExpList elist = exp.exps;
      while( elist != null) { // Check if Null
        elist.head.accept( this, level, false, ptrOffset );
        elist = elist.tail;
      }
      emitComment("<- compound statement");

    }

    /* Dec */
    public void visit( FunctionDec dec, int level, boolean flag, int ptrOffset ) { // Print FunctionDec

      // System.err.println("visit FunctionDec: " + dec.func + ": " + dec.params);
      emitComment("processing function: " + dec.func);
      emitComment("jump around function body here");

      emitRM("ST", AC, -1, FP, "store return");

      /* Go to CompoundExp */
      level++;
      dec.typ.accept(this, level, false, 0);

      VarDecList list = dec.params;
      while (list != null && list.head != null) { // Check if null
        list.head.accept(this, level, false, 0);
        list = list.tail;
      }

      if (dec.body != null) { // Check if null
        dec.body.accept(this, level, false, 0);
      }

      /*level++;
      int savedLoc = emitLoc;
      emitLoc++;

      if ((dec.func).equals("main")) {
        mainEntry = emitLoc;
      }

      dec.funaddr = emitLoc;
      emitComment("processing function: " + dec.func);
      emitRM("ST", AC, -1, FP, "store return");

      int frameOffset = -2;
=======
      level++;
      emitComment("processing function: " + dec.func);
      emitComment("jump around function body here");
      //emitRM("ST", AC, -1, FP, "store return");

      dec.typ.accept( this, level, false, ptrOffset );
      /*VarDecList list = dec.params;
      while( list != null && list.head != null ) { // Check if null
        list.head.accept( this, level, false, ptrOffset );
        list = list.tail;
      }*/

      
      // This is some command
      if (dec.body != null) { // Check if null
        dec.body.accept( this, level, false, ptrOffset );
      }
    }

    /* VarDec */
    public void visit( SimpleDec vardec, int level, boolean flag, int ptrOffset ) { // Print SimpleDec
      vardec.offset = ptrOffset;
      if (level == 0) {
        vardec.nestLevel = 1;
        emitComment("allocating global var: " + vardec.name);
        emitComment("<- vardecl");
      } else {
        emitComment("processing local var: " + vardec.name);
        vardec.nestLevel = 0;
      }

      vardec.offset = ptrOffset;
      // System.err.println("visit SimpleDec: " + vardec.name);
      level++;
      vardec.typ.accept(this, level, false, 0);

    }

    public void visit( ArrayDec vardec, int level, boolean flag, int ptrOffset ) { // Print ArrayDec
      vardec.offset = ptrOffset;
      if (level == 0) {
        vardec.nestLevel = 1;
        emitComment("allocating global var: " + vardec.name);
        emitComment("<- vardecl");
      } else {
        emitComment("processing local var: " + vardec.name);
        vardec.nestLevel = 0;
      }

      vardec.offset = ptrOffset;
      // System.err.println("visit ArrayDec: " + vardec.name + " size " + vardec.size);
      level++;
      vardec.typ.accept(this, level, false, 0);


    }

    /* Lists */
    public void visit( DecList decList, int level, boolean flag, int ptrOffset ) { // Visiting DecList
      while( decList != null ) { // Check if null
        decList.head.accept( this, level, false, ptrOffset );
        decList = decList.tail;
      }
      // System.err.println("visit DecList");
    }

    public void visit( VarDecList vardecList, int level, boolean flag, int ptrOffset ) { // Visiting VarDecList
      while( vardecList != null && vardecList.head != null ) { // Check if null
        vardecList.head.accept( this, level, false, ptrOffset );
        vardecList = vardecList.tail;
      }
      // System.err.println("visit VarDecList");

    }

    public void visit( ExpList expList, int level, boolean flag, int ptrOffset ) { // Visiting ExpList
      while( expList != null && expList.head != null ) { // Check if null
        expList.head.accept( this, level, false, ptrOffset );
        expList = expList.tail;
      }
      // System.err.println("visit ExpList");

    }

}