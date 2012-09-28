#include <stdio.h>
#include <math.h>
#include "eval.h"

void checkForZeroDivision(EXP *);

EXP* evalEXP(EXP *e)
{
    switch (e->kind) {
        case idK:
            return e;
            break;
        case intconstK:
            return e;
            break;
        case timesK:
            if(e->val.timesE.left->kind == intconstK && e->val.timesE.right->kind == intconstK)
            {
                int prod = e->val.timesE.left->val.intconstE * e->val.timesE.right->val.intconstE;
                return makeEXPintconst(prod);
            }
            else
            {
                EXP* leftVal = evalEXP(e->val.timesE.left);
                EXP* rightVal = evalEXP(e->val.timesE.right);
                if(leftVal->kind == intconstK && rightVal->kind == intconstK)
                {
                    int prod2 = leftVal->val.intconstE * rightVal->val.intconstE;
                    return makeEXPintconst(prod2);
                }
                else
                {
                    return makeEXPtimes(leftVal, rightVal);
                }
            }
            break;
        case divK:
            checkForZeroDivision(e);
            if(e->val.divE.left->kind == intconstK && e->val.divE.right->kind == intconstK)
            {
                int quot = e->val.divE.left->val.intconstE / e->val.divE.right->val.intconstE;
                return makeEXPintconst(quot);
            }
            else
            {
                EXP* leftVal = evalEXP(e->val.divE.left);
                EXP* rightVal = evalEXP(e->val.divE.right);
                if(leftVal->kind == intconstK && rightVal->kind == intconstK)
                {
                    int quot2 = leftVal->val.intconstE / rightVal->val.intconstE;
                    return makeEXPintconst(quot2);
                }
                else
                {
                    return makeEXPdiv(leftVal, rightVal);
                }
            }
            break;
        case modK:
            if(e->val.modE.left->kind == intconstK && e->val.modE.right->kind == intconstK)
            {
                int mod = e->val.modE.left->val.intconstE % e->val.modE.right->val.intconstE;
                return makeEXPintconst(mod);
            }
            else
            {
                EXP* leftVal = evalEXP(e->val.modE.left);
                EXP* rightVal = evalEXP(e->val.modE.right);
                if(leftVal->kind == intconstK && rightVal->kind == intconstK)
                {
                    int mod2 = leftVal->val.intconstE % rightVal->val.intconstE;
                    return makeEXPintconst(mod2);
                }
                else
                {
                    return makeEXPmod(leftVal, rightVal);
                }
            }
            break;
        case absK:
            if(e->val.absE.right->kind == intconstK)
            {
                return makeEXPintconst(fabs(e->val.absE.right->val.intconstE));
            }
            else if(e->val.absE.right->kind == idK)
            {
                return e;
            }
            else
            {
                EXP* abs1 = evalEXP(e->val.absE.right);
                if(abs1->kind == intconstK)
                {
                    return makeEXPintconst(fabs(abs1->val.intconstE));
                }
                else
                {
                    return makeEXPabs(abs1);
                }
            }
            break;
        case negK:
            if(e->val.negE.right->kind == intconstK)
            {
                return makeEXPintconst(-1 * (e->val.negE.right->val.intconstE));
            }
            else if(e->val.negE.right->kind == idK)
            {
                return e;
            }
            else
            {
                EXP* neg1 = evalEXP(e->val.negE.right);
                if(neg1->kind == intconstK)
                {
                    return makeEXPintconst(-1 * (neg1->val.intconstE));
                }
                else
                {
                    return makeEXPneg(neg1);
                }                
            }
            break;
        case exponK:
            if(e->val.exponE.left->kind == intconstK && e->val.exponE.right->kind == intconstK)
            {
                int result = pow(e->val.exponE.left->val.intconstE, e->val.exponE.right->val.intconstE);
                return makeEXPintconst(result);
            }
            else
            {
                EXP* leftVal = evalEXP(e->val.exponE.left);
                EXP* rightVal = evalEXP(e->val.exponE.right);
                if(leftVal->kind == intconstK && rightVal->kind == intconstK)
                {
                    int result2 = pow(leftVal->val.intconstE, rightVal->val.intconstE);
                    return makeEXPintconst(result2);
                }
                else
                {
                    return makeEXPexpon(leftVal, rightVal);
                }
            }
            break;
        case plusK:
            if(e->val.plusE.left->kind == intconstK && e->val.plusE.right->kind == intconstK)
            {
                int sum = e->val.plusE.left->val.intconstE + e->val.plusE.right->val.intconstE;
                return makeEXPintconst(sum);
            }
            else
            {
                EXP* leftVal = evalEXP(e->val.plusE.left);
                EXP* rightVal = evalEXP(e->val.plusE.right);
                if(leftVal->kind == intconstK && rightVal->kind == intconstK)
                {
                    int sum2 = leftVal->val.intconstE + rightVal->val.intconstE;
                    return makeEXPintconst(sum2);
                }
                else
                {
                    return makeEXPplus(leftVal, rightVal);
                }
            }
            break;
        case minusK:            
            if(e->val.plusE.left->kind == intconstK && e->val.plusE.right->kind == intconstK)
            {
                int diff = e->val.plusE.left->val.intconstE - e->val.plusE.right->val.intconstE;
                return makeEXPintconst(diff);
            }
            else
            {
                EXP* leftVal = evalEXP(e->val.plusE.left);
                EXP* rightVal = evalEXP(e->val.plusE.right);
                if(leftVal->kind == intconstK && rightVal->kind == intconstK)
                {
                    int diff2 = leftVal->val.intconstE - rightVal->val.intconstE;
                    return makeEXPintconst(diff2);
                }
                else
                {
                    return makeEXPminus(leftVal, rightVal);
                }
            }
            break;
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