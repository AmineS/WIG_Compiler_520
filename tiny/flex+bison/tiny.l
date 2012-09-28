%{
#include "y.tab.h"
#include <string.h>
#include <stdlib.h>

extern int lineno;
%}

a [a]
b [b]
s [s]


%%
[ \t]+    /* ignore */;
\n        lineno++;

"*"       return '*';
"/"       return '/';
"%"       return '%';
{a}{b}{s} return(ABS);
"**"	  return(STARSTAR);
"+"       return '+';
"-"       return '-';
"("       return '(';
")"       return ')';

0|([1-9][0-9]*) {
  yylval.intconst = atoi (yytext);
  return tINTCONST;
}

[a-zA-Z_][a-zA-Z0-9_]* { 
  yylval.stringconst = 
    (char *) malloc (strlen (yytext) + 1);
  sprintf (yylval.stringconst, "%s", yytext); 
  return tIDENTIFIER; 
}

.         /* ignore */;
%%
