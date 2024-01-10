import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import absyn.*;

/*
  Program Information
  Authors: Nicholas Baker & Garrett Holmes
  File Name: ShowTreeVisitor.java
  Adapted from: Fei Song
*/

public class ShowTreeVisitor implements AbsynVisitor {

    final static int SPACES = 4;

    // arraylist for showing the syntax tree output
    public ArrayList<String> tree_output;

    // constructor
    public ShowTreeVisitor() {
        // initialize the output arraylist
        this.tree_output = new ArrayList<String>();
    }

    private void indent( int level ) {
        for( int i = 0; i < level * SPACES; i++ ) tree_output.add( " " );
    }

    // prints the contents of the tree_output arraylist
    public void print_output(){
        for (String s : this.tree_output) {
            System.out.print(s);
        }
    }

    // prints the contents of the tree_output arraylist to a file
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

            for (String s : this.tree_output) {
                myWriter.write(s);
            }

            myWriter.close();

        } catch (IOException e) {
            System.err.println("An error occurred.");
            e.printStackTrace();
        }
    }

    /* NameTy */
    public void visit( NameTy ty, int level, boolean flag, int ptrOffset ) {
        indent( level );
        // Print type
        tree_output.add( "NameTy: " + ty.typ + "\n");
    }

    /* Variables */
    public void visit( SimpleVar var, int level, boolean flag, int ptrOffset ) { // Print SimpleVar
        indent( level );
        tree_output.add( "SimpleVar: " + var.name + "\n");
    }

    public void visit( IndexVar var, int level, boolean flag, int ptrOffset ) { // Print IndexVar
        indent( level );
        tree_output.add( "IndexVar: " + var.name + "\n");
        level++;
        var.index.accept( this, level, false, 0 );
    }

    /* Expressions */
    public void visit( NilExp exp, int level, boolean flag, int ptrOffset ) { // Print NilExp
        indent( level );
        tree_output.add( "NilExp:\n" );
    }

    public void visit( IntExp exp, int level, boolean flag, int ptrOffset ) { // Print IntExp
        indent( level );
        tree_output.add( "IntExp: " + exp.value + "\n"); 
    }

    public void visit( BoolExp exp, int level, boolean flag, int ptrOffset ) { // Print BoolExp
        indent( level );
        tree_output.add( "BoolExp: " + exp.value + "\n"); 
    }

    public void visit( VarExp exp, int level, boolean flag, int ptrOffset ) { // Print VarExp
        indent( level );
        tree_output.add( "VarExp: \n");
        level++;
        exp.variable.accept( this, level, false, 0 );
    }

    public void visit( CallExp exp, int level, boolean flag, int ptrOffset ) { // Print CallExp
        indent( level );
        tree_output.add( "CallExp: " + exp.func + "\n");
        level++;
        
        ExpList list = exp.args;
        while( list != null) { // Check if list is null
            list.head.accept( this, level, false, 0 );
            list = list.tail;
        }
    }

    public void visit( OpExp exp, int level, boolean flag, int ptrOffset ) { // Print OpExp and operation
        indent( level );
        tree_output.add( "OpExp:" ); 
        switch( exp.op ) {
            case OpExp.PLUS:
                tree_output.add( " + \n" );
                break;
            case OpExp.MINUS:
                tree_output.add( " - \n" );
                break;
            case OpExp.UMINUS:
                tree_output.add( " - \n" );
                break;
            case OpExp.MUL:
                tree_output.add( " * \n" );
                break;
            case OpExp.DIV:
                tree_output.add( " / \n" );
                break;
            case OpExp.EQ:
                tree_output.add( " == \n" );
                break;
            case OpExp.NE:
                tree_output.add( " != \n" );
                break;
            case OpExp.LT:
                tree_output.add( " < \n" );
                break;
            case OpExp.LTEQ:
                tree_output.add( " <= \n" );
                break;
            case OpExp.GT:
                tree_output.add( " > \n" );
                break;
            case OpExp.GTEQ:
                tree_output.add( " >= \n" );
                break;
            case OpExp.NOT:
                tree_output.add( " ~ \n" );
                break;
            case OpExp.AND:
                tree_output.add( " && \n" );
                break;
            case OpExp.OR:
                tree_output.add( " || \n" );
                break;
            default:
                System.err.println( "Unrecognized operator at line " + exp.row + " and column " + exp.col);
        }
        level++;
        if (exp.left != null) // Check if null
            exp.left.accept( this, level, false, 0 );
        exp.right.accept( this, level, false, 0 );
    }

    public void visit( AssignExp exp, int level, boolean flag, int ptrOffset ) { // Print AssignExp
        indent( level );
        tree_output.add( "AssignExp:\n" );
        level++;
        exp.lhs.accept( this, level, false, 0 );
        exp.rhs.accept( this, level, false, 0 );
    }

    public void visit( IfExp exp, int level, boolean flag, int ptrOffset ) { // Print IfExp chain
        indent( level );
        tree_output.add( "IfExp:\n" );
        level++;
        exp.test.accept( this, level, false, 0 );
        exp.thenpart.accept( this, level, false, 0 );
        if (exp.elsepart != null ) // Check if null
            exp.elsepart.accept( this, level, false, 0 );
    }

    public void visit( WhileExp exp, int level, boolean flag, int ptrOffset ) { // Print WhileExp
        indent( level );
        tree_output.add( "WhileExp:\n" );
        level++;
        exp.test.accept( this, level, false, 0 );
        exp.body.accept( this, level, false, 0 ); 
    }

    public void visit( ReturnExp exp, int level, boolean flag, int ptrOffset ) { // Print ReturnExp
        indent( level );
        tree_output.add( "ReturnExp:\n" );
        level++;
        exp.exp.accept( this, level, false, 0 );
    }

    public void visit( CompoundExp exp, int level, boolean flag, int ptrOffset ) { // Print CompoundExp
        indent( level );
        tree_output.add( "CompoundExp:\n" );
        level++;

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
    }

    /* Dec */
    public void visit( FunctionDec dec, int level, boolean flag, int ptrOffset ) { // Print FunctionDec
        indent( level );
        tree_output.add( "FunctionDec: " + dec.func + "\n");
        level++;
        dec.typ.accept( this, level, false, 0 );

        VarDecList list = dec.params;
        while( list != null && list.head != null ) { // Check if null
            list.head.accept( this, level, false, 0 );
            list = list.tail;
        }

        if (dec.body != null) { // Check if null
            dec.body.accept( this, level, false, 0 );
        }
    }

    /* VarDec */
    public void visit( SimpleDec vardec, int level, boolean flag, int ptrOffset ) { // Print SimpleDec
        indent( level );
        tree_output.add( "SimpleDec: " + vardec.name + "\n");
        level++;
        vardec.typ.accept( this, level, false, 0 );
    }

    public void visit( ArrayDec vardec, int level, boolean flag, int ptrOffset ) { // Print ArrayDec
        indent( level );
        tree_output.add( "ArrayDec: " + vardec.name + "\n");
        if (vardec.size != 0) { // Check if the arry is empty
            tree_output.add( "size: " + vardec.size + "\n");
        }
        
        level++;
        vardec.typ.accept( this, level, false, 0 );
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
