#include <stdio.h>
#include "pretty.h"

void prettyEXP(EXP *e)
{
switch (e->kind) {
    case idK:
         printf("%s",e->val.idE);
         break;
    case intconstK:
         printf("%i",e->val.intconstE);
         break;
    case timesK:
         printf("(");
         prettyEXP(e->val.timesE.left);
         printf("*");
         prettyEXP(e->val.timesE.right);
         printf(")");
         break;
    case divK:
         printf("(");
         prettyEXP(e->val.divE.left);
         printf("/");
         prettyEXP(e->val.divE.right);
         printf(")");
         break;
    case modK:
         printf("(");
         prettyEXP(e->val.modE.left);
         printf("%%");
         prettyEXP(e->val.modE.right);
         printf(")");
         break;
    case absK:
         printf("abs(");
         prettyEXP(e->val.absE.right);
         printf(")");
         break;
    case exponK:
         printf("(");
         prettyEXP(e->val.exponE.left);
         printf("**");
         prettyEXP(e->val.exponE.right);
         printf(")");
         break;
    case plusK:
         /*printf("(");*/
         prettyEXP(e->val.plusE.left);
         printf("+");
         prettyEXP(e->val.plusE.right);
         /*printf(")");*/
         break;
    case minusK:
         /*printf("(");*/
         prettyEXP(e->val.minusE.left);
         printf("-");
         prettyEXP(e->val.minusE.right);
         /*printf(")");*/
         break;
    default:
         printf("Wrong kind");
         break;
  }
}
