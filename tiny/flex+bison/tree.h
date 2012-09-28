#ifndef TREE_H
#define TREE_H
 
typedef struct EXP {
  int lineno;
  enum {idK,intconstK,timesK,divK,modK,absK,exponK,plusK,minusK} kind;
  union {
    char *idE;
    int intconstE;
    struct {struct EXP *left; struct EXP *right;} timesE;
    struct {struct EXP *left; struct EXP *right;} divE;
    struct {struct EXP *left; struct EXP *right;} modE;
    struct {struct EXP *right;} absE;
    struct {struct EXP *left; struct EXP *right;} exponE;
    struct {struct EXP *left; struct EXP *right;} plusE;
    struct {struct EXP *left; struct EXP *right;} minusE;
  } val;

  union {
    char *expr; 
    int evaluation;
  } evaluated; 
  }
} EXP;
 
EXP *makeEXPid(char *id);

EXP *makeEXPintconst(int intconst);

EXP *makeEXPtimes(EXP *left, EXP *right);

EXP *makeEXPdiv(EXP *left, EXP *right);

EXP *makeEXPmod(EXP *left, EXP *right);

EXP *makeEXPabs(EXP *right);

EXP *makeEXPexpon(EXP *left, EXP* right);

EXP *makeEXPplus(EXP *left, EXP *right);

EXP *makeEXPminus(EXP *left, EXP *right);

#endif /* !TREE_H */
