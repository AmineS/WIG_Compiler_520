# assumes sablecc script is on your PATH

#.PHONY code

$junit_jar_loc = "/home/2010/dbhage/cs520/group-h/wig/src/junit-4.10.jar"

# to compile the code
code: clean grammar
	javac wig/lexer/*.java wig/parser/*.java wig/node/*.java wig/analysis/*.java wig/compiler/*.java wig/weeder/*.java wig/commons/cli/*.java wig/symboltable/*.java

# to generate the compiler code
grammar: wig.sablecc
	sablecc wig.sablecc

# to clean
clean:	
	rm -rf wig/*.class wig/lexer/ wig/parser/ wig/node/ wig/analysis/ wig/compiler/*.class wig/weeder/*.class

# tests
check: 
	python wigrun.py ch

# test weeder
weed:
	python wigrun.py wd

# test pretty printer
pretty:
	python wigrun.py pp

# unit testing
unit:
	python wigrun.py ut $(junit_jar_loc)

# test symbol phase
symbol:
	python wigrun.py st
