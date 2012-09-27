# assumes sablecc script is on your PATH

#.PHONY tiny

# to compile the code
tiny: clean grammar
	javac tiny/*.java tiny/lexer/*.java tiny/parser/*.java tiny/node/*.java tiny/analysis/*.java 

# to generate the compiler code
grammar: tiny.sablecc
	sablecc tiny.sablecc

check:	
	./check

clean:	
	rm -rf tiny/*.class tiny/lexer/ tiny/parser/ tiny/node/ tiny/analysis/ result
