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

public class SemanticAnalyzer implements AbsynVisitor {

    /* ------------------------ Checkpoint 2 ------------------------ */

    final static int SPACES = 4;
    public boolean fatalError = false;
    //public final static int BOOL = 0;
    //public final static int INT = 1;
    //public final static int VOID = 2;
    public HashMap<String, ArrayList<NodeType>> table;
    public ArrayList<String> table_output;

    public SemanticAnalyzer() {
        this.table = new HashMap<String, ArrayList<NodeType>>();
        this.table_output = new ArrayList<String>();
        
        createSymbol("input");
        insert("input", new NodeType("input", 0, new FunctionDec(0, 0, new NameTy(0,0,TYPE.INT), "input", null, null)));
        
        FunctionDec outputFunc = createOutput();
        createSymbol("output");
        insert("output", new NodeType("output", 0, outputFunc));

    }

    // prints the contents of the tree_output arraylist
    public void print_output() {
        for (String s : this.table_output) {
            System.out.print(s);
        }
    }

    // prints the contents of the tree_output arraylist to a file
    public void write_output(String filename) {

        // create file if not exists (https://www.w3schools.com/java/java_files_create.asp)
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

            for (String s : this.table_output) {
                myWriter.write(s);
            }
            
            myWriter.close();

        } catch (IOException e) {
            System.err.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public FunctionDec createOutput() {
        
        SimpleDec dec = new SimpleDec(0,0,new NameTy(0,0,TYPE.INT),"x");
        VarDecList list = new VarDecList(dec,null);

        FunctionDec func = new FunctionDec(0,0,new NameTy(0,0,TYPE.VOID),"output",list,null);

        return func;
    }

    public void print_table(int level){

        Iterator<HashMap.Entry<String, ArrayList<NodeType>>> iter = table.entrySet().iterator();
        while(iter.hasNext()) {
            // get next item
            HashMap.Entry<String, ArrayList<NodeType>> entry = iter.next();
            String key = entry.getKey();
            ArrayList<NodeType> stack = entry.getValue();

            int count = 0;
            while (count < stack.size()){
                indent( level );
                // print type
                table_output.add(key + ": " + stack.get(count).def.typ.typ + "\n");
                count++;
            }
        }
    }

    // debug function
    public void print_table_out(){

        Iterator<HashMap.Entry<String, ArrayList<NodeType>>> iter = table.entrySet().iterator();
        while(iter.hasNext()) {
            // get next item
            HashMap.Entry<String, ArrayList<NodeType>> entry = iter.next();
            String key = entry.getKey();
            ArrayList<NodeType> stack = entry.getValue();

            System.out.print("\t" + key + ": ");

            int count = 0;
            while (count < stack.size()){
                // Print Type
                System.out.print( stack.get(count).def.typ.typ + ", ");
                count++;
            }
            System.out.println();
        }
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
        // System.out.println("insert " + name);
        // print_table_out();
    }

    // deletes all variables in the hashmap at the given level
    public void deleteLevel(int level) {
        // create an iterator
        Iterator<HashMap.Entry<String, ArrayList<NodeType>>> iter = table.entrySet().iterator();
        while(iter.hasNext()) {
            // get next item
            HashMap.Entry<String, ArrayList<NodeType>> entry = iter.next();

            // if the end of the list is at the given level, remove the element
            ArrayList<NodeType> stack = entry.getValue();

            if(stack.size() != 0) {
                if (stack.get(stack.size() - 1).level == level) {
                    stack.remove(stack.size() - 1);
                }
            }


        }
    }

    // deletes all variables in the hashmap at the given level
    public void deleteKey() {
        
        ArrayList<String> list = new ArrayList<String>();
        // create an iterator
        Iterator<HashMap.Entry<String, ArrayList<NodeType>>> iter = table.entrySet().iterator();
        while(iter.hasNext()) {
            // get next item
            HashMap.Entry<String, ArrayList<NodeType>> entry = iter.next();

            ArrayList<NodeType> stack = entry.getValue();
            if(stack.size() == 0) {
                // remove the element at that level
                list.add(entry.getKey());
            }
        }
        for (int i = 0; i < (list.size() - 1); i++) {
            table.remove(list.get(i));
        }
    }

    private void indent( int level ) {
        for( int i = 0; i < level * SPACES; i++ ) table_output.add( " " );
    }

    /* NameTy */
    public void visit( NameTy ty, int level, boolean flag, int ptrOffset ) {
    }

    /* Variables */
    public void visit( SimpleVar var, int level, boolean flag, int ptrOffset ) { // Print SimpleVar
    }

    public void visit( IndexVar var, int level, boolean flag, int ptrOffset ) { // Print IndexVar
        var.index.accept( this, level, false, 0 );

        if(var.index instanceof VarExp && (var.index.dtype != TYPE.INT)){
            System.err.println("Error (row " + (var.index.row + 1) + ", column " + (var.index.col + 1) + "): variable "
            + ((VarExp)var.index).variable.name + " is not an INT.");
            fatalError = true;
        } else if (!(var.index instanceof VarExp) && !(var.index instanceof IntExp)){
            System.err.println("Error (row " + (var.index.row + 1) + ", column " + (var.index.col + 1) + "): index is not an INT.");
            fatalError = true;
        } else if ((var.index instanceof IntExp) && ((IntExp)var.index).value < 0){
            System.err.println(
                    "Error (row " + (var.index.row + 1) + ", column " + (var.index.col + 1) + "): index is negative");
            fatalError = true;
        }
        
    }

    /* Expressions */
    public void visit( NilExp exp, int level, boolean flag, int ptrOffset ) { // Print NilExp
    }

    public void visit( IntExp exp, int level, boolean flag, int ptrOffset ) { // Print IntExp
        exp.dtype = TYPE.INT;
    }

    public void visit( BoolExp exp, int level, boolean flag, int ptrOffset ) { // Print BoolExp
        exp.dtype = TYPE.BOOL;
    }

    public void visit( VarExp exp, int level, boolean flag, int ptrOffset ) { // Print VarExp
        exp.variable.accept(this, level, false, 0);

        NodeType id = lookup(exp.variable.name);
        if (id != null) {
            exp.dtype = id.def.typ.typ;
        }
    }

    public void visit( CallExp exp, int level, boolean flag, int ptrOffset ) { // Print CallExp

        NodeType id = lookup(exp.func);
        if (id != null) {
            exp.dtype = id.def.typ.typ;
            VarDecList params = ((FunctionDec) id.def).params;
            
            ExpList list = exp.args;

            if((list == null && params != null)){
                System.err.println("Error (row " + (exp.row + 1) + ", column " + (exp.col + 1) + "): "
                        + "Function call requires parameters and there were none: " + exp.func);
                fatalError = true;
            }

            if ((list != null && params == null)) {
                System.err.println("Error (row " + (exp.row + 1) + ", column " + (exp.col + 1) + "): "
                        + "Function call has no parameters and parameters were included: " + exp.func);
                fatalError = true;
            }

            while( list != null) { // Check if list is null
                list.head.accept( this, level, false, 0 );
                list = list.tail;
            }

        } else {
            System.err.println("Error (row " + (exp.row + 1) + ", column " + (exp.col + 1) + "): "
                    + "Function has not been declared: " + exp.func);
            fatalError = true;

        }

    }

    public void visit( OpExp exp, int level, boolean flag, int ptrOffset ) { // Print OpExp and operation
        if (exp.left != null) // Check if null
            exp.left.accept( this, level, false, 0 );
        exp.right.accept( this, level, false, 0 );

        if (exp.left.dtype != exp.right.dtype) {
            // indent( level+1 );
            System.err.println( "Error (row " + (exp.row+1) + ", column " + (exp.col+1) + "): " + "Both expressions must have matching types: " + exp.left.dtype + " & " + exp.right.dtype);
            fatalError = true;
        }
        
        if (exp.op == OpExp.PLUS || exp.op == OpExp.MINUS || exp.op == OpExp.UMINUS || exp.op == OpExp.MUL || exp.op == OpExp.UMINUS || exp.op == OpExp.DIV) {
            exp.dtype = TYPE.INT;
        } else if (exp.op == OpExp.EQ || exp.op == OpExp.NE || exp.op == OpExp.LT || exp.op == OpExp.LTEQ || exp.op == OpExp.GT || exp.op == OpExp.GTEQ || exp.op == OpExp.NOT || exp.op == OpExp.AND || exp.op == OpExp.OR) {
            exp.dtype = TYPE.BOOL;
        }
    }

    public void visit( AssignExp exp, int level, boolean flag, int ptrOffset ) { // Print AssignExp
        
        exp.lhs.accept( this, level, false, 0 );
        exp.rhs.accept( this, level, false, 0 );

        if (lookup(exp.lhs.variable.name) == null) {
            // indent( level+1 );
            System.err.println( "Error (row " + (exp.lhs.row+1) + ", column " + (exp.lhs.col+1) + "): " + exp.lhs.variable.name +" Variable was not declared.");
            fatalError = true;
        } else {
            if (exp.lhs.dtype != exp.rhs.dtype) {
                // indent( level+1 );
                System.err.println( "Error (row " + (exp.lhs.row+1) + ", column " + (exp.lhs.col+1) + "): Both expressions must have matching types: " + exp.lhs.variable.name + ":" + exp.lhs.dtype + " & " + exp.rhs.dtype);
                fatalError = true;
            }
        }
        
        exp.dtype = exp.lhs.dtype;
    }

    public void visit( IfExp exp, int level, boolean flag, int ptrOffset ) { // Print IfExp chain

        level++;
        indent( level );
        table_output.add( "Entering a new if block\n" );


        exp.test.accept( this, level, false, 0 );

        if(exp.test.dtype != TYPE.BOOL){
            System.err.println("Error (row " + (exp.row + 1) + ", column " + (exp.col + 1)
                    + "): " + "Test expression must evaluate to a boolean" );
            fatalError = true;
        }

        exp.thenpart.accept( this, level, false, 0 );
        if (exp.elsepart != null ) // Check if null
            exp.elsepart.accept( this, level, false, 0 );

        print_table(level+1);
        deleteLevel(level);
        deleteKey();

        indent( level );
        table_output.add( "Leaving the if block\n" );
        
    }

    public void visit( WhileExp exp, int level, boolean flag, int ptrOffset ) { // Print WhileExp

        level++;
        indent( level );
        table_output.add( "Entering a new while block\n" );

        exp.test.accept( this, level, false, 0 );
        exp.body.accept( this, level, false, 0 );

        print_table(level+1);
        deleteLevel(level); 
        deleteKey();

        indent( level );
        table_output.add( "Leaving the while block\n" );
    }

    public void visit( ReturnExp exp, int level, boolean flag, int ptrOffset ) { // Print ReturnExp
        exp.exp.accept( this, level, false, 0 );
        exp.dtype = exp.exp.dtype;        
    }

    public void visit( CompoundExp exp, int level, boolean flag, int ptrOffset ) { // Print CompoundExp
       
        VarDecList vlist = exp.decs;
        while( vlist != null) { // Check if null
            vlist.head.accept( this, level, false, 0 );
            vlist = vlist.tail;
        }

        ExpList elist = exp.exps;
        while( elist != null) { // Check if Null
            elist.head.accept( this, level, false, 0 );
            elist = elist.tail;
        }

        exp.dtype = TYPE.VOID;
    }

    /* Dec */
    public void visit( FunctionDec dec, int level, boolean flag, int ptrOffset ) { // Print FunctionDec

        if (lookup(dec.func) == null) {
            createSymbol(dec.func);
        }
        insert(dec.func, new NodeType(dec.func, level, dec));
        
        level++;
        indent( level );
        table_output.add( "Entering the scope for function " + dec.func + ":\n" );
        dec.typ.accept( this, level, false, 0 );
        
        VarDecList list = dec.params;
        while( list != null && list.head != null ) { // Check if null
            list.head.accept( this, level, false, 0 );
            list = list.tail;
        }
        
        if (dec.body != null) { // Check if null
            dec.body.accept( this, level, false, 0 );
        }
        
        print_table(level+1);
        deleteLevel(level);
        deleteKey();
        

        indent( level );
        table_output.add( "Leaving the function scope\n" );
    }

    /* VarDec */
    public void visit( SimpleDec vardec, int level, boolean flag, int ptrOffset ) { // Print SimpleDec

        if (vardec.typ.typ == TYPE.VOID) { // Print VOID
            // indent( level+1 );
            System.err.println( "Error (row " + (vardec.row+1) + ", column " + (vardec.col+1) +"): " + vardec.name +" can not be declared as void.");
            fatalError = true;
        } else {
            if (lookup(vardec.name) != null){
                // allow redefining variables in different scopes but not on the same level/scope
                if (lookup(vardec.name).level == level){
                    // indent( level+1 );
                    System.err.println( "Error (row " + (vardec.row+1) + ", column " + (vardec.col+1) + "): " + vardec.name +" has already been declared." );
                    fatalError = true;
                } else {
                    // different scope, insert
                    insert(vardec.name, new NodeType(vardec.name, level, vardec));
                }
            } else {
                // if not created, create and insert
                createSymbol(vardec.name);
                insert(vardec.name, new NodeType(vardec.name, level, vardec));
            }
        }
    }

    public void visit( ArrayDec vardec, int level, boolean flag, int ptrOffset ) { // Print ArrayDec
        // dont allow void types
        if (vardec.typ.typ == TYPE.VOID) { // Print VOID
            // indent( level+1 );
            System.err.println("Error (row " + (vardec.row + 1) + ", column " + (vardec.col + 1) + "): " + vardec.name
                    + " can not be declared as void.");
            fatalError = true;
        } else {   
            
            // don't allow invalid sizes
            if(vardec.size < 0){
                System.err.println("Error (row " + (vardec.row + 1) + ", column " + (vardec.col + 1) + "): "
                        + vardec.name + " has an invalid size of " + vardec.size + ".");
                fatalError = true;
            }

            if (lookup(vardec.name) != null) {
                // allow redefining variables in different scopes but not on the same
                // level/scope
                if (lookup(vardec.name).level == level) {
                    // indent( level+1 );
                    System.err.println("Error (row " + (vardec.row + 1) + ", column " + (vardec.col + 1) + "): "
                            + vardec.name + " has already been declared.");
                    fatalError = true;
                } else {
                    // different scope, insert
                    insert(vardec.name, new NodeType(vardec.name, level, vardec));
                }
            } else {
                // if not created, create and insert
                createSymbol(vardec.name);
                insert(vardec.name, new NodeType(vardec.name, level, vardec));
            }
        }

    }

    /* Lists */
    public void visit( DecList decList, int level, boolean flag, int ptrOffset ) { // Visiting DecList
        while( decList != null ) { // Check if null
            decList.head.accept( this, level, false, 0 );
            decList = decList.tail;
        } 
    }

    public void visit( VarDecList vardecList, int level, boolean flag, int ptrOffset ) { // Visiting VarDecList
        while( vardecList != null && vardecList.head != null ) { // Check if null
            vardecList.head.accept( this, level, false, 0 );
            vardecList = vardecList.tail;
        } 
    }

    public void visit( ExpList expList, int level, boolean flag, int ptrOffset ) { // Visiting ExpList
        while( expList != null && expList.head != null ) { // Check if null
            expList.head.accept( this, level, false, 0 );
            expList = expList.tail;
        } 
    }
}
