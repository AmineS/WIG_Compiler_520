# assumes sablecc script is on your PATH

#.PHONY wig

# to compile the code
#wig: clean grammar
#	javac wig/*.java wig/lexer/*.java wig/parser/*.java wig/node/*.java wig/#analysis/*.java 

# to generate the compiler code
grammar: wig.sablecc
	sablecc wig.sablecc

#check:	
#	./check

#clean:	
#	rm -rf wig/*.class wig/lexer/ wig/parser/ wig/node/ wig/analysis/ result
