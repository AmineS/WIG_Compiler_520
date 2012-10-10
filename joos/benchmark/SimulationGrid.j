.class public SimulationGrid

.super java/lang/Object

.field protected xLength I
.field protected yLength I
.field protected grid Ljava/util/Vector;

.method public <init>(II)V
  .limit locals 4
  .limit stack 25
  aload_0
  invokenonvirtual java/lang/Object/<init>()V
  iload_1
  dup
  aload_0
  swap
  putfield SimulationGrid/xLength I
  pop
  iload_2
  dup
  aload_0
  swap
  putfield SimulationGrid/yLength I
  pop
  new java/util/Vector
  dup
  aload_0
  getfield SimulationGrid/xLength I
  invokenonvirtual java/util/Vector/<init>(I)V
  dup
  aload_0
  swap
  putfield SimulationGrid/grid Ljava/util/Vector;
  pop
  iconst_0
  dup
  istore_3
  pop
  start_0:
  iload_3
  aload_0
  getfield SimulationGrid/yLength I
  if_icmplt true_2
  iconst_0
  goto stop_3
  true_2:
  iconst_1
  stop_3:
  ifeq stop_1
  aload_0
  getfield SimulationGrid/grid Ljava/util/Vector;
  new java/util/Vector
  dup
  aload_0
  getfield SimulationGrid/yLength I
  invokenonvirtual java/util/Vector/<init>(I)V
  invokevirtual java/util/Vector/addElement(Ljava/lang/Object;)V
  iload_3
  iconst_1
  iadd
  dup
  istore_3
  pop
  goto start_0
  stop_1:
  return
.end method

.method public getXLength()I
  .limit locals 1
  .limit stack 25
  aload_0
  getfield SimulationGrid/xLength I
  ireturn
  nop
.end method

.method public getYLength()I
  .limit locals 1
  .limit stack 25
  aload_0
  getfield SimulationGrid/yLength I
  ireturn
  nop
.end method

.method public get(II)Ljava/lang/Character;
  .limit locals 4
  .limit stack 25
  aload_0
  getfield SimulationGrid/grid Ljava/util/Vector;
  iload_1
  invokevirtual java/util/Vector/elementAt(I)Ljava/lang/Object;
  checkcast java/util/Vector
  dup
  astore_3
  pop
  aload_3
  iload_2
  invokevirtual java/util/Vector/elementAt(I)Ljava/lang/Object;
  checkcast java/lang/Character
  areturn
  nop
.end method

.method public set(IILjava/lang/Character;)V
  .limit locals 5
  .limit stack 25
  aload_0
  getfield SimulationGrid/grid Ljava/util/Vector;
  iload_1
  invokevirtual java/util/Vector/elementAt(I)Ljava/lang/Object;
  checkcast java/util/Vector
  dup
  astore 4
  pop
  aload 4
  aload_3
  iload_2
  invokevirtual java/util/Vector/insertElementAt(Ljava/lang/Object;I)V
  return
.end method

.method public printGrid(C)V
  .limit locals 6
  .limit stack 25
  new joos/lib/JoosIO
  dup
  invokenonvirtual joos/lib/JoosIO/<init>()V
  dup
  astore_2
  pop
  iconst_0
  dup
  istore_3
  pop
  start_0:
  iload_3
  aload_0
  getfield SimulationGrid/xLength I
  if_icmplt true_2
  iconst_0
  goto stop_3
  true_2:
  iconst_1
  stop_3:
  ifeq stop_1
  iconst_0
  dup
  istore 4
  pop
  start_4:
  iload 4
  aload_0
  getfield SimulationGrid/yLength I
  if_icmplt true_6
  iconst_0
  goto stop_7
  true_6:
  iconst_1
  stop_7:
  ifeq stop_5
  aload_0
  iload_3
  iload 4
  invokevirtual SimulationGrid/get(II)Ljava/lang/Character;
  dup
  astore 5
  pop
  aload_2
  ldc ""
  dup
  ifnull null_10
  goto stop_11
  null_10:
  pop
  ldc "null"
  stop_11:
  new java/lang/Character
  dup
  aload 5
  invokevirtual java/lang/Character/charValue()C
  invokenonvirtual java/lang/Character/<init>(C)V
  invokevirtual java/lang/Character/toString()Ljava/lang/String;
  invokevirtual java/lang/String/concat(Ljava/lang/String;)Ljava/lang/String;
  dup
  ifnull null_8
  goto stop_9
  null_8:
  pop
  ldc "null"
  stop_9:
  new java/lang/Character
  dup
  iload_1
  invokenonvirtual java/lang/Character/<init>(C)V
  invokevirtual java/lang/Character/toString()Ljava/lang/String;
  invokevirtual java/lang/String/concat(Ljava/lang/String;)Ljava/lang/String;
  invokevirtual joos/lib/JoosIO/print(Ljava/lang/String;)V
  iload 4
  iconst_1
  iadd
  dup
  istore 4
  pop
  goto start_4
  stop_5:
  aload_2
  ldc "\n"
  invokevirtual joos/lib/JoosIO/print(Ljava/lang/String;)V
  iload_3
  iconst_1
  iadd
  dup
  istore_3
  pop
  goto start_0
  stop_1:
  aload_2
  ldc ""
  invokevirtual joos/lib/JoosIO/println(Ljava/lang/String;)V
  return
.end method

