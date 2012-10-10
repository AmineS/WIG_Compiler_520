.class public Main

.super java/lang/Object

.method public <init>()V
  .limit locals 1
  .limit stack 25
  aload_0
  invokenonvirtual java/lang/Object/<init>()V
  return
.end method

.method public static main([Ljava/lang/String;)V
  .limit locals 13
  .limit stack 25
  new joos/lib/JoosIO
  dup
  invokenonvirtual joos/lib/JoosIO/<init>()V
  dup
  astore_1
  pop
  aload_1
  invokevirtual joos/lib/JoosIO/readBoolean()Z
  dup
  istore 12
  pop
  iload 12
  ifeq else_0
  iconst_3
  dup
  istore 4
  pop
  ldc 15
  dup
  istore 5
  pop
  ldc 100
  dup
  istore 6
  pop
  iconst_1
  dup
  istore 7
  pop
  new ShellingSimulation
  dup
  iload 4
  iload 5
  iload 6
  iload 7
  aconst_null
  invokenonvirtual ShellingSimulation/<init>(IIIILSimulationGrid;)V
  dup
  astore_2
  pop
  goto stop_1
  else_0:
  aload_1
  invokevirtual joos/lib/JoosIO/readInt()I
  dup
  istore 4
  pop
  aload_1
  invokevirtual joos/lib/JoosIO/readInt()I
  dup
  istore 5
  pop
  aload_1
  invokevirtual joos/lib/JoosIO/readInt()I
  dup
  istore 6
  pop
  aload_1
  invokevirtual joos/lib/JoosIO/readInt()I
  dup
  istore 7
  pop
  new SimulationGrid
  dup
  iload 5
  iload 5
  invokenonvirtual SimulationGrid/<init>(II)V
  dup
  astore_3
  pop
  iconst_0
  dup
  istore 8
  pop
  start_2:
  iload 8
  iload 5
  if_icmplt true_4
  iconst_0
  goto stop_5
  true_4:
  iconst_1
  stop_5:
  ifeq stop_3
  iconst_0
  dup
  istore 9
  pop
  start_6:
  iload 9
  iload 5
  if_icmplt true_8
  iconst_0
  goto stop_9
  true_8:
  iconst_1
  stop_9:
  ifeq stop_7
  aload_1
  invokevirtual joos/lib/JoosIO/readInt()I
  dup
  istore 10
  pop
  new java/lang/Character
  dup
  iload 10
  i2c
  invokenonvirtual java/lang/Character/<init>(C)V
  dup
  astore 11
  pop
  aload_3
  iload 8
  iload 9
  aload 11
  invokevirtual SimulationGrid/set(IILjava/lang/Character;)V
  iload 9
  iconst_1
  iadd
  dup
  istore 9
  pop
  goto start_6
  stop_7:
  iload 8
  iconst_1
  iadd
  dup
  istore 8
  pop
  goto start_2
  stop_3:
  new ShellingSimulation
  dup
  iload 4
  iload 5
  iload 6
  iload 7
  aload_3
  invokenonvirtual ShellingSimulation/<init>(IIIILSimulationGrid;)V
  dup
  astore_2
  pop
  stop_1:
  start_10:
  iconst_1
  ifeq stop_11
  aload_2
  invokevirtual ShellingSimulation/simulate()V
  goto start_10
  stop_11:
  return
.end method

