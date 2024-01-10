JAVA=java
JAVAC=javac
# Adjust following paths for CUP & Jflex as necessary

# UNIX paths for JFlex and CUP
JFLEX=jflex
CLASSPATH=-cp /usr/share/java/cup.jar:.
CUP=cup
# Local paths example (ex. Mac)
# JFLEX=~/Projects/jflex/bin/jflex
# CLASSPATH=-cp ~/Projects/java-cup-11b.jar:.
# CUP=$(JAVA) $(CLASSPATH) java_cup.Main

all: Main.class

Main.class: absyn/*.java parser.java sym.java Lexer.java ShowTreeVisitor.java CodeGeneration.java SemanticAnalyzer.java Scanner.java Main.java 

%.class: %.java
	$(JAVAC) $(CLASSPATH) $^

Lexer.java: cm.flex
	$(JFLEX) cm.flex

parser.java: cm.cup
	#$(CUP) -dump -expect 3 cm.cup
	$(CUP) -expect 3 cm.cup

clean:
	rm -f parser.java Lexer.java sym.java *.class absyn/*.class *~ *.abs *.sym *.tm
