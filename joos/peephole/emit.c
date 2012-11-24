/*
 * JOOS is Copyright (C) 1997 Laurie Hendren & Michael I. Schwartzbach
 *
 * Reproduction of all or part of this software is permitted for
 * educational or research use on condition that this copyright notice is
 * included in any copy. This software comes with no warranty of any
 * kind. In no event will the authors be liable for any damages resulting from
 * use of this software.
 *
 * email: hendren@cs.mcgill.ca, mis@brics.dk
 */

#include <stdio.h>
#include <string.h>
#include "memory.h"
#include "emit.h"
#include "optimize.h" 
FILE *emitFILE;

LABEL *emitlabels;

void printCODE(CODE *);

int getBranchLabel(CODE *);
int isAnIfStament(CODE *);
int ifStatementValue(CODE *);
int getStopLabel(CODE *);

char *emitname(char *name)
{ int i,j;
  char *e;
  i = strlen(name);
  for (i=strlen(name); i>0 && name[i-1]!='.'; i--);
  e = (char *)Malloc(i+2);
  for (j=0; j<=i; j++) e[j]=name[j];
  e[i] = 'j';
  e[i+1] = '\0';
  return e;
}

void emitLABEL(int label)
{ fprintf(emitFILE,"%s_%i",emitlabels[label].name,label);
}

void emitPrintLabel(int label)
{
  printf("%s_%i", emitlabels[label].name, label);
}

void localmem(char *opcode, int offset)
{ if (offset >=0 && offset <=3) {
     fprintf(emitFILE,"%s_%i",opcode,offset);
  } else {
     fprintf(emitFILE,"%s %i",opcode,offset);
  }
}

int  getBranchLimit(CODE *c)
{
  CODE *code = c; 
  int branch_stack_limit = 0; 
  int max_branch_stack_limit = 0; 
  int max_branch_limit = 0;

  int stack_change = 0;
  int stack_affected = 0; 
  int stack_used = 0;

  int next_label = getBranchLabel(code);
  int last_label = getStopLabel(code);
  int analysis_valid = 0;

  while(code->visited) 
  {
    code = code->next;
  }

  while(code != NULL && !(code->kind==labelCK && code->val.labelC==last_label) && !code->visited)
  {
    if(code->kind==labelCK && code->val.labelC==next_label && !isAnIfStament(code))
    {
       max_branch_limit = max_branch_stack_limit > max_branch_limit ? max_branch_stack_limit : max_branch_limit;
       branch_stack_limit = 0;
       max_branch_stack_limit = 0;
    }

      analysis_valid = stack_effect(code, &stack_change, &stack_affected, &stack_used);        

      branch_stack_limit += stack_change;
      max_branch_stack_limit = branch_stack_limit > max_branch_stack_limit ? branch_stack_limit : max_branch_stack_limit;

      stack_change = 0;
      stack_affected = 0; 
      stack_used = 0; 
      code->visited = 1;
      code = code->next;       
  }

  max_branch_limit = max_branch_stack_limit > max_branch_limit ? max_branch_stack_limit : max_branch_limit;

  if(code != NULL)
  {
    code->visited = 1;
  }

  return max_branch_limit;       
}

int limitCODE(CODE *c)
{
    CODE *code = c; 
    int stack_change = 0;
    int stack_affected = 0; 
    int stack_used = 0; 
    int branch_limit = 0;

    /* stack limit initializes with 1 to account for "this" */
    int stack_limit = 0;    
    int max_stack_limit = 0; 
    int analysis_invalid = 0;

    while(code != NULL)
    {

      /* check if we've entered a branch */
      if(!code->visited && getBranchLabel(code) != -1)
      {          
          analysis_invalid = stack_effect(code, &stack_change, &stack_affected, &stack_used);
          stack_limit += stack_change; 
          branch_limit = getBranchLimit(code);
          max_stack_limit = stack_limit + branch_limit > max_stack_limit ? stack_limit + branch_limit : max_stack_limit;
      }

      if(!code->visited)
      {
          analysis_invalid = stack_effect(code, &stack_change, &stack_affected, &stack_used);
        
        /* add the stack to the stack limit */            
          stack_limit += stack_change; 
          max_stack_limit = stack_limit > max_stack_limit ? stack_limit : max_stack_limit;

          /*printf("The stack limit at this point is %d\n", stack_limit);
          printf("The max stack limit at this point is %d\n", max_stack_limit);*/
          code->visited = 1;
      }

      /* re-initialize variables */
      stack_change = 0;
      stack_affected = 0; 
      stack_used = 0; 
      code = code->next;        
    }
    return max_stack_limit;
}

int getBranchLabel(CODE *c)
{
  CODE *code = c;
  int stopLabel = 0;

  if(code != NULL && isAnIfStament(code) != 0)
  {
    code->visited=1;
    code = code->next;
    if(code != NULL && code->kind == ldc_intCK && code->val.ldc_intC == 0)
    {
      code->visited=1;
      code = code->next;
      if(code != NULL && code->kind == gotoCK)
      {
        stopLabel = code->val.gotoC;
        code->visited=1;
        code = code->next;
        if(code != NULL && code->kind == labelCK)
        {
          code->visited=1;
          code = code->next;
          if(code != NULL && code->kind == ldc_intCK && code->val.ldc_intC == 1)
          {
            code->visited=1;
            code = code->next;
            if(code != NULL && code->kind == labelCK && code->val.labelC == stopLabel)
            {
              code->visited=1;
              code = code->next;              
              if(code != NULL && isAnIfStament(code) != 0)
              {
                return ifStatementValue(code);
              }
            }
          }
        }
      }
    }
  }
  return -1;
}

int getStopLabel(CODE *c)
{
  CODE *code = c;
  int branchLabel = getBranchLabel(c);
  CODE *prev;
  while(code != NULL)
  {
    if(code->kind == labelCK && code->val.labelC == branchLabel)
    {
      return prev->val.labelC;
    }
    prev = code;
    code = code->next;
  }

  return -1;
}

int isAnIfStament(CODE *c)
{
  switch(c->kind)
  {
      case ifeqCK:
      case ifneCK:
      case if_acmpeqCK:
      case if_acmpneCK:
      case ifnullCK:
      case ifnonnullCK:
      case if_icmpeqCK:
      case if_icmpgtCK:
      case if_icmpltCK:
      case if_icmpleCK:
      case if_icmpgeCK:
      case if_icmpneCK:
          return 1;
      default: 
          return 0;
  }
}

int ifStatementValue(CODE *c)
{
  switch(c->kind)
  {
      case ifeqCK:
        return c->val.ifeqC;
      case ifneCK:
        return c->val.ifneC;
      case if_acmpeqCK:
        return c->val.if_acmpeqC;
      case if_acmpneCK:
        return c->val.if_acmpneC;
      case ifnullCK:
        return c->val.ifnullC;
      case ifnonnullCK:
        return c->val.ifnonnullC;
      case if_icmpeqCK:
        return c->val.if_icmpeqC;
      case if_icmpgtCK:
        return c->val.if_icmpgtC;
      case if_icmpltCK:
        return c->val.if_icmpltC;
      case if_icmpleCK:
        return c->val.if_icmpleC;
      case if_icmpgeCK:
        return c->val.if_icmpgeC;
      case if_icmpneCK:
        return c->val.if_icmpneC;
      default: 
          return 0;
  }
}

void printCODE(CODE *c)
{
  if (c == NULL) return; 

     switch(c->kind) 
     {
       case nopCK:
            printf("nop");
            printf("\n");
            break;
       case i2cCK:
            printf("i2c");
            printf("\n");
            break;
       case newCK:
            printf("new %s",c->val.newC);
            printf("\n");
            break;
       case instanceofCK:
            printf("instanceof %s",c->val.instanceofC);
            printf("\n");
            break;
       case checkcastCK:
            printf("checkcast %s",c->val.checkcastC);
            printf("\n");
            break;
       case imulCK:
            printf("imul");
            printf("\n");
            break;
       case inegCK:
            printf("ineg");
            printf("\n");
            break;
       case iremCK:
            printf("irem");
            printf("\n");
            break;
       case isubCK:
            printf("isub");
            printf("\n");
            break;
       case idivCK:
            printf("idiv");
            printf("\n");
            break;
       case iaddCK:
            printf("iadd");
            printf("\n");
            break;
       case iincCK:
            printf("iinc %i %i",c->val.iincC.offset,c->val.iincC.amount);
            printf("\n");
            break;
       case labelCK:
            emitPrintLabel(c->val.gotoC);
            printf(":");
            printf("\n");
            break;
       case gotoCK:
            printf("goto ");
            emitPrintLabel(c->val.gotoC);
            printf("\n");
            break;
       case ifeqCK:
            printf("ifeq ");
            emitPrintLabel(c->val.gotoC);
            printf("\n");
            break;
       case ifneCK:
            printf("ifne ");
            emitPrintLabel(c->val.gotoC);
            printf("\n");
            break;
       case if_acmpeqCK:
            printf("if_acmpeq ");
            emitPrintLabel(c->val.gotoC);
            printf("\n");
            break;
       case if_acmpneCK:
            printf("if_acmpne ");
            emitPrintLabel(c->val.gotoC);
            printf("\n");
            break;
       case ifnullCK:
            printf("ifnull ");
            emitPrintLabel(c->val.gotoC);
            printf("\n");
            break;
       case ifnonnullCK:
            printf("ifnonnull ");
            emitPrintLabel(c->val.gotoC);
            printf("\n");
            break;
       case if_icmpeqCK:
            printf("if_icmpeq ");
            emitPrintLabel(c->val.gotoC);
            printf("\n");
            break;
       case if_icmpgtCK:
            printf("if_icmpgt ");
            emitPrintLabel(c->val.gotoC);
            printf("\n");
            break;
       case if_icmpltCK:
            printf("if_icmplt ");
            emitPrintLabel(c->val.gotoC);
            printf("\n");
            break;
       case if_icmpleCK:
            printf("if_icmple ");
            emitPrintLabel(c->val.gotoC);
            printf("\n");
            break;
       case if_icmpgeCK:
            printf("if_icmpge ");
            emitPrintLabel(c->val.gotoC);
            printf("\n");
            break;
       case if_icmpneCK:
            printf("if_icmpne ");
            emitPrintLabel(c->val.gotoC);
            printf("\n");
            break;
       case ireturnCK:
            printf("ireturn");
            printf("\n");
            break;
       case areturnCK:
            printf("areturn");
            printf("\n");
            break;
       case returnCK:
            printf("return");
            printf("\n");
            break;
       case aloadCK:
            localmem("aload",c->val.aloadC);
            printf("\n");
            break;
       case astoreCK:
            localmem("astore",c->val.astoreC);
            printf("\n");
            break;
       case iloadCK:
            localmem("iload",c->val.iloadC);
            printf("\n");
            break;
       case istoreCK:
            localmem("istore",c->val.istoreC);
            printf("\n");
            break;
       case dupCK:
            printf("dup");
            printf("\n");
            break;
       case popCK:
            printf("pop");
            printf("\n");
            break;
       case swapCK:
            printf("swap");
            printf("\n");
            break;
       case ldc_intCK:
            if (c->val.ldc_intC >= 0 && c->val.ldc_intC <= 5) {
               printf("iconst_%i",c->val.ldc_intC);
            } else {
               printf("ldc %i",c->val.ldc_intC);
            }
            printf("\n");
            break;
       case ldc_stringCK:
            printf("ldc \"%s\"",c->val.ldc_stringC);
            printf("\n");
            break;
       case aconst_nullCK:
            printf("aconst_null");
            printf("\n");
            break;
       case getfieldCK:
            printf("getfield %s",c->val.getfieldC);
            printf("\n");
            break;
       case putfieldCK:
            printf("putfield %s",c->val.putfieldC);
            printf("\n");
            break;
       case invokevirtualCK:
            printf("invokevirtual %s",c->val.invokevirtualC);
            printf("\n");
            break;
       case invokenonvirtualCK:
            printf("invokenonvirtual %s",c->val.invokenonvirtualC);
            printf("\n");
            break;
      }
}

void emitCODE(CODE *c)
{ if (c!=NULL) {
     fprintf(emitFILE,"  ");
     switch(c->kind) {
       case nopCK:
            fprintf(emitFILE,"nop");
            break;
       case i2cCK:
            fprintf(emitFILE,"i2c");
            break;
       case newCK:
            fprintf(emitFILE,"new %s",c->val.newC);
            break;
       case instanceofCK:
            fprintf(emitFILE,"instanceof %s",c->val.instanceofC);
            break;
       case checkcastCK:
            fprintf(emitFILE,"checkcast %s",c->val.checkcastC);
            break;
       case imulCK:
            fprintf(emitFILE,"imul");
            break;
       case inegCK:
            fprintf(emitFILE,"ineg");
            break;
       case iremCK:
            fprintf(emitFILE,"irem");
            break;
       case isubCK:
            fprintf(emitFILE,"isub");
            break;
       case idivCK:
            fprintf(emitFILE,"idiv");
            break;
       case iaddCK:
            fprintf(emitFILE,"iadd");
            break;
       case iincCK:
            fprintf(emitFILE,"iinc %i %i",
                             c->val.iincC.offset,c->val.iincC.amount);
            break;
       case labelCK:
            emitLABEL(c->val.labelC);
            fprintf(emitFILE,":");
            break;
       case gotoCK:
            fprintf(emitFILE,"goto ");
            emitLABEL(c->val.gotoC);
            break;
       case ifeqCK:
            fprintf(emitFILE,"ifeq ");
            emitLABEL(c->val.ifeqC);
            break;
       case ifneCK:
            fprintf(emitFILE,"ifne ");
            emitLABEL(c->val.ifneC);
            break;
       case if_acmpeqCK:
            fprintf(emitFILE,"if_acmpeq ");
            emitLABEL(c->val.if_acmpeqC);
            break;
       case if_acmpneCK:
            fprintf(emitFILE,"if_acmpne ");
            emitLABEL(c->val.if_acmpneC);
            break;
       case ifnullCK:
            fprintf(emitFILE,"ifnull ");
            emitLABEL(c->val.ifnullC);
            break;
       case ifnonnullCK:
            fprintf(emitFILE,"ifnonnull ");
            emitLABEL(c->val.ifnonnullC);
            break;
       case if_icmpeqCK:
            fprintf(emitFILE,"if_icmpeq ");
            emitLABEL(c->val.if_icmpeqC);
            break;
       case if_icmpgtCK:
            fprintf(emitFILE,"if_icmpgt ");
            emitLABEL(c->val.if_icmpgtC);
            break;
       case if_icmpltCK:
            fprintf(emitFILE,"if_icmplt ");
            emitLABEL(c->val.if_icmpltC);
            break;
       case if_icmpleCK:
            fprintf(emitFILE,"if_icmple ");
            emitLABEL(c->val.if_icmpleC);
            break;
       case if_icmpgeCK:
            fprintf(emitFILE,"if_icmpge ");
            emitLABEL(c->val.if_icmpgeC);
            break;
       case if_icmpneCK:
            fprintf(emitFILE,"if_icmpne ");
            emitLABEL(c->val.if_icmpneC);
            break;
       case ireturnCK:
            fprintf(emitFILE,"ireturn");
            break;
       case areturnCK:
            fprintf(emitFILE,"areturn");
            break;
       case returnCK:
            fprintf(emitFILE,"return");
            break;
       case aloadCK:
            localmem("aload",c->val.aloadC);
            break;
       case astoreCK:
            localmem("astore",c->val.astoreC);
            break;
       case iloadCK:
            localmem("iload",c->val.iloadC);
            break;
       case istoreCK:
            localmem("istore",c->val.istoreC);
            break;
       case dupCK:
            fprintf(emitFILE,"dup");
            break;
       case popCK:
            fprintf(emitFILE,"pop");
            break;
       case swapCK:
            fprintf(emitFILE,"swap");
            break;
       case ldc_intCK:
            if (c->val.ldc_intC >= 0 && c->val.ldc_intC <= 5) {
               fprintf(emitFILE,"iconst_%i",c->val.ldc_intC);
            } else {
               fprintf(emitFILE,"ldc %i",c->val.ldc_intC);
            }
            break;
       case ldc_stringCK:
            fprintf(emitFILE,"ldc \"%s\"",c->val.ldc_stringC);
            break;
       case aconst_nullCK:
            fprintf(emitFILE,"aconst_null");
            break;
       case getfieldCK:
            fprintf(emitFILE,"getfield %s",c->val.getfieldC);
            break;
       case putfieldCK:
            fprintf(emitFILE,"putfield %s",c->val.putfieldC);
            break;
       case invokevirtualCK:
            fprintf(emitFILE,"invokevirtual %s",c->val.invokevirtualC);
            break;
       case invokenonvirtualCK:
            fprintf(emitFILE,"invokenonvirtual %s",c->val.invokenonvirtualC);
            break;
     }
     fprintf(emitFILE,"\n");
     emitCODE(c->next);
  }
}

void emitPROGRAM(PROGRAM *p)
{ if (p!=NULL) {
     emitPROGRAM(p->next);
     emitCLASSFILE(p->classfile,p->name);
  } 
}

void emitCLASSFILE(CLASSFILE *c, char *name)
{ if (c!=NULL) {
     emitCLASSFILE(c->next,name);
     emitCLASS(c->class,name);
  }
}

void emitCLASS(CLASS *c, char *name)
{ if (!c->external) {
     emitFILE = fopen(emitname(name),"w");
     fprintf(emitFILE,".class public ");
     emitMODIFIER(c->modifier);
     fprintf(emitFILE,"%s\n\n",c->name);
     fprintf(emitFILE,".super %s\n\n",c->parent->signature);
     emitFIELD(c->fields);
     if (c->fields!=NULL) fprintf(emitFILE,"\n");
     emitCONSTRUCTOR(c->constructors);
     emitMETHOD(c->methods);
     fclose(emitFILE);
  }
}

void emitTYPE(TYPE *t)
{ switch (t->kind) {
    case intK:
         fprintf(emitFILE,"I");
         break;
    case boolK:
         fprintf(emitFILE,"Z");
         break;
    case charK:
         fprintf(emitFILE,"C");
         break;
    case voidK:
         fprintf(emitFILE,"V");
         break;
    case refK:
         fprintf(emitFILE,"L%s;",t->class->signature);
         break;
    case polynullK:
         break;
  }
}

void emitFIELD(FIELD *f)
{ if (f!=NULL) {
     emitFIELD(f->next);
     fprintf(emitFILE,".field protected %s ",f->name);
     emitTYPE(f->type);
     fprintf(emitFILE,"\n");
  }
}

void emitCONSTRUCTOR(CONSTRUCTOR *c)
{ if (c!=NULL) {
     emitCONSTRUCTOR(c->next);
     fprintf(emitFILE,".method public <init>%s\n",c->signature);
     fprintf(emitFILE,"  .limit locals %i\n",c->localslimit);
     emitlabels = c->labels;
     fprintf(emitFILE,"  .limit stack %i\n",limitCODE(c->opcodes));
     emitCODE(c->opcodes);
     fprintf(emitFILE,".end method\n\n");
  }
}

void emitMETHOD(METHOD *m)
{ if (m!=NULL) {
     emitMETHOD(m->next);
     if (m->modifier==staticMod) {
        fprintf(emitFILE,".method public static main([Ljava/lang/String;)V\n");
     } 
     else 
     { fprintf(emitFILE,".method public ");
        emitMODIFIER(m->modifier);
        fprintf(emitFILE,"%s%s\n",m->name,m->signature);
     }
     /* LJH - added check for abstract */
     if (m->modifier!=abstractMod)
       { fprintf(emitFILE,"  .limit locals %i\n",m->localslimit);
         emitlabels = m->labels;
         fprintf(emitFILE,"  .limit stack %i\n",limitCODE(m->opcodes));
         emitCODE(m->opcodes);
       }
     fprintf(emitFILE,".end method\n\n");
  }
}

void emitMODIFIER(ModifierKind modifier)
{ switch (modifier)
    { case noneMod:          
           break;
      case finalMod:         
           fprintf(emitFILE,"final "); 
           break;
      case abstractMod:      
           fprintf(emitFILE,"abstract "); 
           break;
      case synchronizedMod:  
           fprintf(emitFILE,"synchronized "); 
           break;
      case staticMod:  
           fprintf(emitFILE,"static "); 
           break;
    }
}

