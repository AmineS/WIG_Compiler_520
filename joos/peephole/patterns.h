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

/* iload x        iload x        iload x
 * ldc 0          ldc 1          ldc 2
 * imul           imul           imul
 * ------>        ------>        ------>
 * ldc 0          iload x        iload x
 *                               dup
 *                               iadd
 */

int simplify_multiplication_right(CODE **c)
{ int x,k;
  if (is_iload(*c,&x) && 
      is_ldc_int(next(*c),&k) && 
      is_imul(next(next(*c)))) {
     if (k==0) return replace(c,3,makeCODEldc_int(0,NULL));
     else if (k==1) return replace(c,3,makeCODEiload(x,NULL));
     else if (k==2) return replace(c,3,makeCODEiload(x,
                                       makeCODEdup(
                                       makeCODEiadd(NULL))));
     return 0;
  }
  return 0;
}

/* dup
 * astore x
 * pop
 * -------->
 * astore x
 */
int simplify_astore(CODE **c)
{ int x;
  if (is_dup(*c) &&
      is_astore(next(*c),&x) &&
      is_pop(next(next(*c)))) {
     return replace(c,3,makeCODEastore(x,NULL));
  }
  return 0;
}

/* iload x
 * ldc k   (0<=k<=127)
 * iadd
 * istore x
 * --------->
 * iinc x k
 */ 
int positive_increment(CODE **c)
{ int x,y,k;
  if (is_iload(*c,&x) &&
      is_ldc_int(next(*c),&k) &&
      is_iadd(next(next(*c))) &&
      is_istore(next(next(next(*c))),&y) &&
      x==y && 0<=k && k<=127) {
     return replace(c,4,makeCODEiinc(x,k,NULL));
  }
  return 0;
}

/* goto L1
 * ...
 * L1:
 * goto L2
 * ...
 * L2:
 * --------->
 * goto L2
 * ...
 * L1:    (reference count reduced by 1)
 * goto L2
 * ...
 * L2:    (reference count increased by 1)  
 */
int simplify_goto_goto(CODE **c)
{ int l1,l2;
  if (is_goto(*c,&l1) && is_goto(next(destination(l1)),&l2) && l1>l2) {
     droplabel(l1);
     copylabel(l2);
     return replace(c,1,makeCODEgoto(l2,NULL));
  }
  return 0;
}

/** ADDED **/

/*
 * swap
 * swap
 * ---->
 * nop
 */
int simplify_swap_swap(CODE **c)
{
  if(is_swap(*c) && is_swap(next(*c)))
  {
    return replace(c, 2, makeCODEnop(NULL));
  }
  return 0;
}

/*
 * dup
 * swap
 * ---->
 * dup
 */
int simplify_dup_swap(CODE **c)
{
  if(is_swap(*c) && is_swap(next(*c)))
  {
    return replace(c, 2, makeCODEdup(NULL));
  }
  return 0;
}

/*
 * load x
 * store x
 * ---->
 * nop
 */
int simplify_load_store(CODE **c)
{
  int x, y;
  if(is_iload(*c, &x) && is_istore(next(*c), &y) && (x == y))
  {
    return replace(c, 2, makeCODEnop(NULL));
  }
  else if(is_aload(*c, &x) && is_astore(next(*c), &y) && (x == y))
  {
    return replace(c, 2, makeCODEnop(NULL));
  }
  return 0;
}

/*
 * store x
 * load x
 * ---->
 * nop
 */
int simplify_store_load(CODE **c)
{
  int x, y;
  if(is_istore(*c, &x) && is_iload(next(*c), &y) && (x == y))
  {
    return replace(c, 2, makeCODEnop(NULL));
  }
  else if(is_astore(*c, &x) && is_aload(next(*c), &y) && (x == y))
  {
    return replace(c, 2, makeCODEnop(NULL));
  }
  return 0;
}

/*
 * ineg
 * iadd
 * ---->
 * isub
 */
int simplify_ineg_iadd(CODE **c)
{
  if(is_ineg(*c) && is_iadd(next(*c)))
  {
    return replace(c, 2, makeCODEisub(NULL));
  }
  return 0;
}

/*
 * store x
 * store x
 * ---->
 * pop
 * store x
 */
 int simplify_store_store(CODE **c)
 {
  int x, y;
  if(is_istore(*c, &x) && is_istore(next(*c), &y) && (x == y))
  {
    return replace(c, 2, makeCODEpop(makeCODEistore(x, NULL)));
  } 
  else if(is_astore(*c, &x) && is_astore(next(*c), &y) && (x == y))
  {
    return replace(c, 2, makeCODEpop(makeCODEastore(x, NULL)));
  }
  return 0;
 }


/*
 * found while looking at "while(i<count)"
 * if_icmplt true_2
 * iconst_0
 * goto stop_3
 * true_2:           -> if_icmpge stop_1
 * iconst_1
 * stop_3:
 * if_eq stop_1
*/
int simplify_condition_lt(CODE **c)
{
  int l1, l2, l1d, l2d, l3;
  int x, y;
  if(is_if_icmplt(*c, &l1) && is_ldc_int(next(*c), &x) && (x == 0)
     && is_goto(next(next(*c)), &l2) && is_label(next(next(next(*c))), &l1d)
     && (l1 == l1d) && is_ldc_int(next(next(next(next(*c)))), &y) && (y == 1)
     && is_label(next(next(next(next(next(*c))))), &l2d) && (l2 == l2d)
     && is_ifeq(next(next(next(next(next(next(*c)))))), &l3))
  {
    return replace(c, 7, makeCODEif_icmpge(l3, NULL));
  }
  return 0;
}

/*
 * found while looking at "while(i<count)"
 * if_icmple true_2
 * iconst_0
 * goto stop_3
 * true_2:           -> if_icmpgt stop_1
 * iconst_1
 * stop_3:
 * if_eq stop_1
*/
int simplify_condition_le(CODE **c)
{
  int l1, l2, l1d, l2d, l3;
  int x, y;
  if(is_if_icmple(*c, &l1) && is_ldc_int(next(*c), &x) && (x == 0)
     && is_goto(next(next(*c)), &l2) && is_label(next(next(next(*c))), &l1d)
     && (l1 == l1d) && is_ldc_int(next(next(next(next(*c)))), &y) && (y == 1)
     && is_label(next(next(next(next(next(*c))))), &l2d) && (l2 == l2d)
     && is_ifeq(next(next(next(next(next(next(*c)))))), &l3))
  {
    return replace(c, 7, makeCODEif_icmpgt(l3, NULL));
  }
  return 0;
}

/*
 * if-icmpgt true_1
 * iconst_0
 * goto stop_2 -----> if_icmple stop_0
 * true_1:            stop_0:
 * iconst_1
 * stop_2:
 * ifeq stop_0
 * stop_0
 */
int simplify_condition_gt(CODE **c)
{
  int l1, l2, l1d, l2d, l3;
  int x, y;
  if(is_if_icmpgt(*c, &l1) && is_ldc_int(next(*c), &x) && (x == 0)
     && is_goto(next(next(*c)), &l2) && is_label(next(next(next(*c))), &l1d)
     && (l1 == l1d) && is_ldc_int(next(next(next(next(*c)))), &y) && (y == 1)
     && is_label(next(next(next(next(next(*c))))), &l2d) && (l2 == l2d)
     && is_ifeq(next(next(next(next(next(next(*c)))))), &l3))
  {
    return replace(c, 7, makeCODEif_icmple(l3, NULL));
  }
  return 0;
}

/*
 * if-icmpge true_1
 * iconst_0
 * goto stop_2 -----> if_icmplt stop_0
 * true_1:            
 * iconst_1
 * stop_2:
 * ifeq stop_0
 */
int simplify_condition_ge(CODE **c)
{
  int l1, l2, l1d, l2d, l3;
  int x, y;
  if(is_if_icmpge(*c, &l1) && is_ldc_int(next(*c), &x) && (x == 0)
     && is_goto(next(next(*c)), &l2) && is_label(next(next(next(*c))), &l1d)
     && (l1 == l1d) && is_ldc_int(next(next(next(next(*c)))), &y) && (y == 1)
     && is_label(next(next(next(next(next(*c))))), &l2d) && (l2 == l2d)
     && is_ifeq(next(next(next(next(next(next(*c)))))), &l3))
  {
    return replace(c, 7, makeCODEif_icmplt(l3, NULL));
  }
  return 0;
}

/*
 * if-icmpeq true_1
 * iconst_0
 * goto stop_2 -----> if_icmpne stop_0
 * true_1:            
 * iconst_1
 * stop_2:
 * ifeq stop_0
 */
int simplify_condition_eq(CODE **c)
{
  int l1, l2, l1d, l2d, l3;
  int x, y;
  if(is_if_icmpeq(*c, &l1) && is_ldc_int(next(*c), &x) && (x == 0)
     && is_goto(next(next(*c)), &l2) && is_label(next(next(next(*c))), &l1d)
     && (l1 == l1d) && is_ldc_int(next(next(next(next(*c)))), &y) && (y == 1)
     && is_label(next(next(next(next(next(*c))))), &l2d) && (l2 == l2d)
     && is_ifeq(next(next(next(next(next(next(*c)))))), &l3))
  {
    return replace(c, 7, makeCODEif_icmpne(l3, NULL));
  }
  return 0;
}

/*
 * if-icmpne true_1
 * iconst_0
 * goto stop_2 -----> if_icmpeq stop_0
 * true_1:            
 * iconst_1
 * stop_2:
 * ifeq stop_0
 */
int simplify_condition_ne(CODE **c)
{
  int l1, l2, l1d, l2d, l3;
  int x, y;
  if(is_if_icmpne(*c, &l1) && is_ldc_int(next(*c), &x) && (x == 0)
     && is_goto(next(next(*c)), &l2) && is_label(next(next(next(*c))), &l1d)
     && (l1 == l1d) && is_ldc_int(next(next(next(next(*c)))), &y) && (y == 1)
     && is_label(next(next(next(next(next(*c))))), &l2d) && (l2 == l2d)
     && is_ifeq(next(next(next(next(next(next(*c)))))), &l3))
  {
    return replace(c, 7, makeCODEif_icmpeq(l3, NULL));
  }
  return 0;
}

/*
 * iconst_0
 * if_icmpne stop_0
 * --------->
 * ifne stop_0
 */
 int simplify_const0_condition_ne(CODE **c)
 {
  int x, l1;
  if(is_ldc_int(*c, &x) && (x == 0) && is_if_icmpne(next(*c), &l1))
  {
    return replace(c, 2, makeCODEifne(l1, NULL));
  }
  return 0;
 }

/*
 * iconst_0
 * if_icmpeq stop_0
 * --------->
 * ifeq stop_0
 */
 int simplify_const0_condition_eq(CODE **c)
 {
  int x, l1;
  if(is_ldc_int(*c, &x) && (x == 0) && is_if_icmpeq(next(*c), &l1))
  {
    return replace(c, 2, makeCODEifeq(l1, NULL));
  }
  return 0;
 }

/*
 * !label
 * nop
 * ------->
 * !label
 */ 
 int simplify_nop(CODE **c)
 {
  int i;
  if(!is_label(*c, &i) && is_nop(next(*c)))
  {
    CODE *c2 = NULL;
    c2 = nextby(*c, 2);
    (*c)->next = c2;
    return 1;
  }
  return 0;
 } 

/* dup
 * istore x
 * pop
 * -------->
 * istore x
 */
int simplify_istore(CODE **c)
{ int x;
  if (is_dup(*c) &&
      is_istore(next(*c),&x) &&
      is_pop(next(next(*c)))) {
     return replace(c, 3, makeCODEistore(x, NULL));
  }
  return 0;
}

/* iload x
 * ldc k   (0<=k<=127)
 * isub
 * istore x
 * --------->
 * iinc x -k
 */ 
int negative_increment(CODE **c)
{ int x,y,k;
  if (is_iload(*c,&x) &&
      is_ldc_int(next(*c),&k) &&
      is_isub(next(next(*c))) &&
      is_istore(next(next(next(*c))),&y) &&
      x==y && 0<=k && k<=127) {
     return replace(c,4,makeCODEiinc(x,-k,NULL));
  }
  return 0;
}

/* ldc x 
 * ldc y
 * iadd
 * ------->
 * ldc x+y
 */
int const_addition(CODE **c)
{
	int x,y;
	if (is_ldc_int(*c, &x) &&
		is_ldc_int(next(*c), &y) &&
		is_iadd(next(next(*c)))) {
		return replace(c, 3, makeCODEldc_int(x+y, NULL));
	}
	return 0;
}

/* ldc x 
 * ldc y
 * isub
 * ------->
 * ldc x-y
 */
int const_subtraction(CODE **c)
{
	int x,y;
	if (is_ldc_int(*c, &x) &&
		is_ldc_int(next(*c), &y) &&
		is_isub(next(next(*c)))) {
		return replace(c, 3, makeCODEldc_int(x-y, NULL));
	}
	return 0;
}

/* ldc x 
 * ldc y
 * imul
 * ------->
 * ldc x*y
 */
int const_multiplication(CODE **c)
{
	int x,y;
	if (is_ldc_int(*c, &x) &&
		is_ldc_int(next(*c), &y) &&
		is_imul(next(next(*c)))) {
		return replace(c, 3, makeCODEldc_int(x*y, NULL));
	}
	return 0;
}

/* ldc x 
 * ldc y
 * idiv
 * ------->
 * ldc x/y
 */
int const_division(CODE **c)
{
	int x,y;
	if (is_ldc_int(*c, &x) &&
		is_ldc_int(next(*c), &y) &&
		is_idiv(next(next(*c)))) {
		return replace(c, 3, makeCODEldc_int(x/y, NULL));
	}
	return 0;
}

/*
  iconst_0
  dup
  aload_0
  swap
  putfield Hello/f I
  pop
------>
  aload_0
  iconst_0
  putfield Hello/f I
*/
int assign_intconst_to_field(CODE **c)
{
	int x, y;
	char * a;
	if (is_ldc_int(*c, &x) &&
		is_dup(next(*c)) &&
		is_aload(next(next(*c)),&y) &&
		is_swap(next(next(next(*c)))) &&
		is_putfield(next(next(next(next(*c)))), &a) &&
		is_pop(next(next(next(next(next(*c)))))))
	{
		printf("Found the pattern - now replace");
	}
	return 0;
}

/* 
  new joos/lib/JoosIO
  dup
  invokenonvirtual joos/lib/JoosIO/<init>()V
  dup
  aload_0
  swap
  putfield Hello/f Ljoos/lib/JoosIO;
  pop
  ----------------------->
  new joos/lib/JoosIO
  dup
  invokenonvirtual joos/lib/JoosIO/<init>()V
  aload_0
  swap
  putfield Hello/f Ljoos/lib/JoosIO;
*/
int assign_object_to_field(CODE **c)
{
	return 0;
} 

/******  Old style - still works, but better to use new style. 
#define OPTS 4

OPTI optimization[OPTS] = {simplify_multiplication_right,
                           simplify_astore,
                           positive_increment,
                           simplify_goto_goto};
********/

/* new style for giving patterns */

int init_patterns()
  {
    ADD_PATTERN(simplify_multiplication_right);
    ADD_PATTERN(simplify_astore);
    ADD_PATTERN(positive_increment);
    ADD_PATTERN(simplify_goto_goto);

	  ADD_PATTERN(simplify_istore);
	  ADD_PATTERN(negative_increment);
	  ADD_PATTERN(const_addition);
	  ADD_PATTERN(const_subtraction);
	  ADD_PATTERN(const_multiplication);
	  ADD_PATTERN(const_division);	
	  ADD_PATTERN(assign_intconst_to_field);
    ADD_PATTERN(simplify_store_load);
    ADD_PATTERN(simplify_load_store);
    ADD_PATTERN(simplify_ineg_iadd);
    ADD_PATTERN(simplify_swap_swap);
    ADD_PATTERN(simplify_dup_swap);
    ADD_PATTERN(simplify_store_store);
    ADD_PATTERN(simplify_condition_lt);
    ADD_PATTERN(simplify_condition_le);
    ADD_PATTERN(simplify_condition_gt);
    ADD_PATTERN(simplify_condition_ge);
    ADD_PATTERN(simplify_condition_eq);
    ADD_PATTERN(simplify_condition_ne);
    ADD_PATTERN(simplify_const0_condition_ne);
    ADD_PATTERN(simplify_const0_condition_eq);
    ADD_PATTERN(simplify_nop);
  /*

  */
	return 1;
  }
