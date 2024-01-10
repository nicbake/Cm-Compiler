/*
  Program Information
  Authors: Nicholas Baker & Garrett Holmes
  File Name: Main.java
  Adapted from: Fei Song
  To Build: 
  After the Scanner.java, cm.flex, and cm.cup have been processed, do:
    javac Main.java
  
  To Run: 
    java -classpath /usr/share/java/cup.jar:. Main 1.cm -a/-s/-c

  where 1.cm is an test input file for the tiny language.
*/
   
import java.io.*;
import java.util.Arrays;

import absyn.*;
   
class Main {
  public final static boolean SHOW_TREE = true;

  static public void main(String argv[]) {    

    // isolate the filename from the input path & file type
    int startIndex = 0;
    if(argv[0].contains("/")){ startIndex = argv[0].lastIndexOf('/')+1; }

    // get the base filename
    String fname = argv[0].substring(startIndex, argv[0].lastIndexOf('.'));

    // boolean values for which operations to perform, convert to list and check if contains
    boolean abs = Arrays.asList(argv).contains("-a");
    boolean sym = Arrays.asList(argv).contains("-s");
    boolean tm = Arrays.asList(argv).contains("-c");

    // must have only one command line argument
    if(argv.length != 2 || (!abs && !sym && !tm)){
      System.err.println("Missing command line argument, choose only one of:\n\t-a : perform syntactic analysis and output an abstract syntax tree (.abs)\n\t-s : perform type checking and output symbol tables (.sym)\n\t-c : compile and output TM assembly language code (.tm)");
      return;
    }

    /* Start the parser */
    try {

      // building the abstract syntax tree using the cup parser
      parser p = new parser(new Lexer(new FileReader(argv[0])));
      Absyn result = (Absyn)(p.parse().value);      

      if (SHOW_TREE && result != null) {

        // printing abstract syntax tree
        ShowTreeVisitor tree_visitor = new ShowTreeVisitor();
        result.accept(tree_visitor, 0, false, 0); 
        
        // if -a flag, show syntax tree
        if(abs){
          tree_visitor.write_output(fname + ".abs");
        }
          
        // only continue processing if -s or -c flags are present
        if(sym || tm){

            // Analyzing semantic requirements
            SemanticAnalyzer sym_visitor = new SemanticAnalyzer();
            sym_visitor.table_output.add("\nEntering the Global scope:\n");
            result.accept(sym_visitor, 0, false, 0); 
            sym_visitor.print_table(1);
            sym_visitor.table_output.add("Leaving the Global scope:\n\n");
            
            if(sym_visitor.fatalError){
              System.err.println("Compile Stopped");
            }
            // print the symbol table if -s is present
            // if(sym){
            //   sym_visitor.write_output(fname + ".sym");
            // }

            // Run the code to compile to machine code if -c flag is present
            if(tm){
              CodeGeneration code_visitor = new CodeGeneration(result);
              code_visitor.write_output(fname + ".tm");
              // generate some code!
              
            }

        }

      }
    } catch (Exception e) {
      /* do cleanup here -- possibly rethrow e */
      e.printStackTrace();  
    }
  }
}


