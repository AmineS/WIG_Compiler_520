CC = gcc

CFLAGS = -Wall -ansi -pedantic

main: y.tab.o lex.yy.o main.o tree.h tree.o pretty.h pretty.o eval.h eval.o memory.h memory.o
	$(CC) lex.yy.o y.tab.o tree.o pretty.o eval.o memory.o main.o -o tiny -lfl

y.tab.c y.tab.h: tiny.y
	bison --yacc --defines tiny.y

lex.yy.c: tiny.l y.tab.h
	flex tiny.l
check:
	./check
clean:
	rm y.tab.* lex.yy.*; rm *.o; rm tiny result
