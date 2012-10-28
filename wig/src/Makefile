# assumes sablecc script is on your PATH

#.PHONY code

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
	python check.py

# test weeder
weed:
	python weed.py

# test pretty printer
pretty:
	python pretty.py
