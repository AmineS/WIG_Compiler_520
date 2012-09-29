#include <stdio.h>
#include "pretty.h"


typedef enum {false = 0, true = 1 } bool;

bool isLowerPrecedence(EXP *, EXP *);
int calculatePrecedence(EXP *);

void prettyEXP(EXP *e, EXP *parent)
{
switch (e->kind) {
    case idK:
         printf("%s",e->val.idE);
         break;
    case intconstK:
         printf("%i",e->val.intconstE);
         break;
    case timesK:
         if(isLowerPrecedence(e, parent)) 
         {
            printf("(");
         }
         prettyEXP(e->val.timesE.left, e);
         printf("*");
         prettyEXP(e->val.timesE.right, e);
         if(isLowerPrecedence(e, parent)) 
         {
            printf(")");
         }
         break;
    case divK:
         if(isLowerPrecedence(e, parent)) 
         {
            printf("(");
         }
         prettyEXP(e->val.divE.left, e);
         printf("/");
         prettyEXP(e->val.divE.right, e);
         if(isLowerPrecedence(e, parent)) 
         {
            printf(")");
         }
         break;
    case modK:
         if(isLowerPrecedence(e, parent)) 
         {
            printf("(");
         }
         prettyEXP(e->val.modE.left, e);
         printf("%%");
         prettyEXP(e->val.modE.right, e);
         if(isLowerPrecedence(e, parent)) 
         {
            printf(")");
         }
         break;
    case absK:
         printf("abs(");
         prettyEXP(e->val.absE.right, e);
         printf(")");
         break;
    case negK:
         printf("-");
         prettyEXP(e->val.negE.right, e);
         break;
    case exponK:
         if(isLowerPrecedence(e, parent)) 
         {
            printf("(");
         }
         prettyEXP(e->val.exponE.left, e);
         printf("**");
         prettyEXP(e->val.exponE.right, e);
         if(isLowerPrecedence(e, parent)) 
         {
            printf(")");
         }
         break;
    case plusK:
         if(isLowerPrecedence(e, parent)) 
         {
            printf("(");
         }
         prettyEXP(e->val.plusE.left, e);
         printf("+");
         prettyEXP(e->val.plusE.right, e);
         if(isLowerPrecedence(e, parent)) 
         {
            printf(")");
         }
         break;
    case minusK:
         if(isLowerPrecedence(e, parent)) 
         {
            printf("(");
         }
         prettyEXP(e->val.minusE.left, e);
         printf("-");
         prettyEXP(e->val.minusE.right, e);
         if(isLowerPrecedence(e, parent)) 
         {
            printf(")");
         }
         break;
    default:
         printf("Wrong kind");
         break;
  }
}


bool isLowerPrecedence(EXP *e1, EXP *e2)
{
    if(e2 == NULL)
    {
        return false;
    }

    return calculatePrecedence(e1) < calculatePrecedence(e2);
}

int calculatePrecedence(EXP *e)
{
    if(e->kind == minusK || e->kind == plusK)
    {
        return 1; 
    }
    else if(e->kind == divK || e->kind == timesK || e->kind == modK)
    {
        return 2; 
    }
    else if (e->kind == exponK)
    {
        return 3; 
    }
    else 
    {
        return 0;
    }
}
