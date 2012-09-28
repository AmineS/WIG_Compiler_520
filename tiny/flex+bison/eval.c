#include <stdio.h>
#include <math.h>
#include "eval.h"

void checkForZeroDivision(EXP *);

EXP evalEXP(EXP *e)
{ switch (e->kind) {
    case idK:
         printf("I can't evaluate the value of an identifier!");
	 return(0);
         break;
    case intconstK:
	 return(e->val.intconstE);
         break;
    case timesK:
	 return(evalEXP(e->val.timesE.left) * 
	        evalEXP(e->val.timesE.right));
         break;
    case divK:
     checkForZeroDivision(e);
     return (evalEXP(e->val.divE.left)/
             evalEXP(e->val.divE.right));
         break;
    case modK:
     return (evalEXP(e->val.modE.left)%
             evalEXP(e->val.modE.right));
         break;
    case absK:
     return fabs(evalEXP(e->val.absE.right));
         break;
    case exponK:
     return pow(evalEXP(e->val.exponE.left),evalEXP(e->val.exponE.right));
         break;
    case plusK:
	 return(evalEXP(e->val.plusE.left) + 
	        evalEXP(e->val.plusE.right));
         break;
    case minusK:
	 return(evalEXP(e->val.minusE.left) -
	         evalEXP(e->val.minusE.right));
         break;  
    default: 
	 printf("ERROR: Impossible type for an expression node.");
	 return(0);
  }
}

void checkForZeroDivision(EXP *e)
{
    if(evalEXP(e->val.divE.right) == 0)
    {
        printf("ERROR: Division by zero!\n"); 
        exit(-1);
    }
}