#include "tree.h"
#include "pretty.h"
#include "eval.h"
#include "stdio.h"

void yyparse();

EXP *theexpression;

int lineno;

int main()
{ lineno = 1;
  printf("Type in a tiny exp folowed by one or two Ctrl-d's:\n");
  yyparse();
  printf("\nThe result of evaluating:\n");
  prettyEXP(theexpression);
  printf("\n");
  printf("is: %d\n",evalEXP(theexpression));
  return(1);
}
