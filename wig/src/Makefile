# assumes sablecc script is on your PATH

#.PHONY wig

# to compile the code
wig: clean grammar
	javac wig/lexer/*.java wig/parser/*.java wig/node/*.java wig/analysis/*.java 

# to generate the compiler code
grammar: wig.sablecc
	sablecc wig.sablecc

# check results
check:	
	./check

# to clean
clean:	
	rm -rf wig/*.class wig/lexer/ wig/parser/ wig/node/ wig/analysis/ 
