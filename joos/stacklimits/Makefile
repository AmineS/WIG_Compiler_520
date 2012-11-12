CC = gcc

# Select the first one when using bison,  and the second one when using byacc
YACCOPTS = bison -y -d
#YACCOPTS = byacc -d -v 

# Select the first one when compiling on a real unix sytem,  second one 
# when using gcc under Windows
CFLAGS = -Wall -ansi -pedantic -g
#CFLAGS = 

# IMPORTANT: You have to modify the my_home path
# 
my_home="/home/2010/dbhage"
joos_libs=$(my_home)/cs520/public_html/joos/joos/extern/
stacklimits_joos_dir=$(my_home)/cs520/group-h/joos/stacklimits/joos
testing_dir=$(my_home)/cs520/group-h/joos/stacklimits/testing/
joos_aplus_compiler=$(my_home)/cs520/public_html/joos/bin/joosa+ 

main:			y.tab.o lex.yy.o main.o tree.h tree.o error.h error.o memory.h memory.o weed.h weed.o symbol.h symbol.o type.h type.o defasn.h defasn.o resource.h resource.o code.h code.o optimize.h optimize.o emit.h emit.o
			$(CC) lex.yy.o y.tab.o tree.o error.o memory.o weed.o symbol.o type.o defasn.o resource.o code.o optimize.o emit.o main.o -o joos -lfl

optimize.o:	optimize.c patterns.h
	$(CC) $(CFLAGS) -c optimize.c

y.tab.c y.tab.h:	joos.y
			$(YACCOPTS) joos.y

lex.yy.c:		joos.l y.tab.h 
			flex joos.l

clean:
	rm -f *.o lex.* y.tab.*

test:
	python testing/runTests.py $(stacklimits_joos_dir) $(joos_libs) $(testing_dir) $(joos_aplus_compiler)
