.class public ShellingSimulation

.super java/lang/Object

.field protected toleranceLevel I
.field protected gridSize I
.field protected population I
.field protected simulationSpeed I
.field protected entriesCount I
.field protected ERROR I
.field protected EMPTY Ljava/lang/Character;
.field protected TYPE1 Ljava/lang/Character;
.field protected TYPE2 Ljava/lang/Character;
.field protected DELIMITER C
.field protected RANDOM_NUMBER_RANGE I
.field protected ONE_SECOND_SPEED I
.field protected grid LSimulationGrid;
.field protected cornerToleranceLevels Ljava/util/Vector;
.field protected nonCornerToleranceLevels Ljava/util/Vector;

.method public <init>(IIIILSimulationGrid;)V
  .limit locals 6
  .limit stack 25
  aload_0
  invokenonvirtual java/lang/Object/<init>()V
  iconst_1
  dup
  aload_0
  swap
  putfield ShellingSimulation/ERROR I
  pop
  new java/lang/Character
  dup
  ldc 46
  invokenonvirtual java/lang/Character/<init>(C)V
  dup
  aload_0
  swap
  putfield ShellingSimulation/EMPTY Ljava/lang/Character;
  pop
  new java/lang/Character
  dup
  ldc 42
  invokenonvirtual java/lang/Character/<init>(C)V
  dup
  aload_0
  swap
  putfield ShellingSimulation/TYPE1 Ljava/lang/Character;
  pop
  new java/lang/Character
  dup
  ldc 64
  invokenonvirtual java/lang/Character/<init>(C)V
  dup
  aload_0
  swap
  putfield ShellingSimulation/TYPE2 Ljava/lang/Character;
  pop
  ldc 32
  dup
  aload_0
  swap
  putfield ShellingSimulation/DELIMITER C
  pop
  ldc 100
  dup
  aload_0
  swap
  putfield ShellingSimulation/RANDOM_NUMBER_RANGE I
  pop
  ldc 1000
  dup
  aload_0
  swap
  putfield ShellingSimulation/ONE_SECOND_SPEED I
  pop
  iload_1
  dup
  aload_0
  swap
  putfield ShellingSimulation/toleranceLevel I
  pop
  iload_2
  dup
  aload_0
  swap
  putfield ShellingSimulation/gridSize I
  pop
  iload_3
  dup
  aload_0
  swap
  putfield ShellingSimulation/population I
  pop
  iload 4
  dup
  aload_0
  swap
  putfield ShellingSimulation/simulationSpeed I
  pop
  aload 5
  aconst_null
  if_acmpeq true_2
  iconst_0
  goto stop_3
  true_2:
  iconst_1
  stop_3:
  ifeq else_0
  new SimulationGrid
  dup
  aload_0
  getfield ShellingSimulation/gridSize I
  aload_0
  getfield ShellingSimulation/gridSize I
  invokenonvirtual SimulationGrid/<init>(II)V
  dup
  aload_0
  swap
  putfield ShellingSimulation/grid LSimulationGrid;
  pop
  aload_0
  invokevirtual ShellingSimulation/initializeEmptyGrid()V
  aload_0
  aload_0
  getfield ShellingSimulation/population I
  invokevirtual ShellingSimulation/initializeGridEntries(I)V
  goto stop_1
  else_0:
  aload 5
  dup
  aload_0
  swap
  putfield ShellingSimulation/grid LSimulationGrid;
  pop
  stop_1:
  aload_0
  invokevirtual ShellingSimulation/initializeToleranceLevelVectors()V
  return
.end method

.method public simulate()V
  .limit locals 1
  .limit stack 25
  aload_0
  getfield ShellingSimulation/grid LSimulationGrid;
  aload_0
  getfield ShellingSimulation/DELIMITER C
  invokevirtual SimulationGrid/printGrid(C)V
  aload_0
  invokevirtual ShellingSimulation/updateLocations()V
  aload_0
  invokevirtual ShellingSimulation/sleep()V
  return
.end method

.method public updateLocations()V
  .limit locals 5
  .limit stack 25
  iconst_0
  dup
  istore_1
  pop
  start_0:
  iload_1
  aload_0
  getfield ShellingSimulation/gridSize I
  if_icmplt true_2
  iconst_0
  goto stop_3
  true_2:
  iconst_1
  stop_3:
  ifeq stop_1
  iconst_0
  dup
  istore_2
  pop
  start_4:
  iload_2
  aload_0
  getfield ShellingSimulation/gridSize I
  if_icmplt true_6
  iconst_0
  goto stop_7
  true_6:
  iconst_1
  stop_7:
  ifeq stop_5
  aload_0
  getfield ShellingSimulation/grid LSimulationGrid;
  iload_1
  iload_2
  invokevirtual SimulationGrid/get(II)Ljava/lang/Character;
  dup
  astore_3
  pop
  aload_3
  aconst_null
  if_acmpne true_11
  iconst_0
  goto stop_12
  true_11:
  iconst_1
  stop_12:
  dup
  ifeq false_10
  pop
  aload_3
  aload_0
  getfield ShellingSimulation/EMPTY Ljava/lang/Character;
  invokevirtual java/lang/Character/equals(Ljava/lang/Object;)Z
  ifeq true_13
  iconst_0
  goto stop_14
  true_13:
  iconst_1
  stop_14:
  false_10:
  dup
  ifeq false_9
  pop
  aload_0
  iload_1
  iload_2
  aload_3
  invokevirtual java/lang/Character/charValue()C
  invokevirtual ShellingSimulation/isToleranceLevelMet(IIC)Z
  ifeq true_15
  iconst_0
  goto stop_16
  true_15:
  iconst_1
  stop_16:
  false_9:
  ifeq stop_8
  aload_0
  iload_1
  iload_2
  invokevirtual ShellingSimulation/relocate(II)V
  stop_8:
  iload_2
  iconst_1
  iadd
  dup
  istore_2
  pop
  goto start_4
  stop_5:
  iload_1
  iconst_1
  iadd
  dup
  istore_1
  pop
  goto start_0
  stop_1:
  return
.end method

.method public sleep()V
  .limit locals 3
  .limit stack 25
  new java/lang/Thread
  dup
  invokenonvirtual java/lang/Thread/<init>()V
  dup
  astore_1
  pop
  new joos/lib/JoosThread
  dup
  aload_1
  invokenonvirtual joos/lib/JoosThread/<init>(Ljava/lang/Thread;)V
  dup
  astore_2
  pop
  aload_2
  invokevirtual joos/lib/JoosThread/currentThread()Ljava/lang/Thread;
  pop
  aload_2
  aload_0
  getfield ShellingSimulation/simulationSpeed I
  aload_0
  getfield ShellingSimulation/ONE_SECOND_SPEED I
  imul
  invokevirtual joos/lib/JoosThread/sleep(I)Z
  pop
  return
.end method

.method public relocate(II)V
  .limit locals 12
  .limit stack 25
  iconst_0
  dup
  istore_3
  pop
  iconst_0
  dup
  istore 4
  pop
  iconst_0
  dup
  istore 5
  pop
  iconst_0
  dup
  istore 6
  pop
  iconst_0
  dup
  istore 7
  pop
  start_0:
  iload 7
  aload_0
  getfield ShellingSimulation/gridSize I
  if_icmplt true_2
  iconst_0
  goto stop_3
  true_2:
  iconst_1
  stop_3:
  ifeq stop_1
  iconst_0
  dup
  istore 8
  pop
  start_4:
  iload 8
  aload_0
  getfield ShellingSimulation/gridSize I
  if_icmplt true_6
  iconst_0
  goto stop_7
  true_6:
  iconst_1
  stop_7:
  ifeq stop_5
  aload_0
  getfield ShellingSimulation/grid LSimulationGrid;
  iload 7
  iload 8
  invokevirtual SimulationGrid/get(II)Ljava/lang/Character;
  dup
  astore 9
  pop
  iload 4
  ifeq true_9
  iconst_0
  goto stop_10
  true_9:
  iconst_1
  stop_10:
  ifeq stop_8
  iload 7
  dup
  istore 5
  pop
  iload 8
  dup
  istore 6
  pop
  iconst_1
  dup
  istore 4
  pop
  stop_8:
  aload_0
  iload 7
  iload 8
  aload 9
  invokevirtual java/lang/Character/charValue()C
  invokevirtual ShellingSimulation/isToleranceLevelMet(IIC)Z
  ifeq stop_11
  aload_0
  getfield ShellingSimulation/grid LSimulationGrid;
  iload_1
  iload_2
  invokevirtual SimulationGrid/get(II)Ljava/lang/Character;
  dup
  astore 10
  pop
  aload_0
  getfield ShellingSimulation/grid LSimulationGrid;
  iload 7
  iload 8
  aload 10
  invokevirtual SimulationGrid/set(IILjava/lang/Character;)V
  aload_0
  getfield ShellingSimulation/grid LSimulationGrid;
  iload_1
  iload_2
  aload 9
  invokevirtual SimulationGrid/set(IILjava/lang/Character;)V
  iconst_1
  dup
  istore_3
  pop
  return
  stop_11:
  iload 8
  iconst_1
  iadd
  dup
  istore 8
  pop
  goto start_4
  stop_5:
  iload 7
  iconst_1
  iadd
  dup
  istore 7
  pop
  goto start_0
  stop_1:
  aload_0
  getfield ShellingSimulation/grid LSimulationGrid;
  iload_1
  iload_2
  invokevirtual SimulationGrid/get(II)Ljava/lang/Character;
  dup
  astore 11
  pop
  aload_0
  getfield ShellingSimulation/grid LSimulationGrid;
  iload 5
  iload 6
  aload 11
  invokevirtual SimulationGrid/set(IILjava/lang/Character;)V
  aload_0
  getfield ShellingSimulation/grid LSimulationGrid;
  iload_1
  iload_2
  aload_0
  getfield ShellingSimulation/EMPTY Ljava/lang/Character;
  invokevirtual SimulationGrid/set(IILjava/lang/Character;)V
  return
.end method

.method public isToleranceLevelMet(IIC)Z
  .limit locals 8
  .limit stack 25
  iconst_0
  dup
  istore 4
  pop
  iconst_0
  dup
  istore 5
  pop
  iconst_0
  dup
  istore 6
  pop
  iload_1
  iconst_1
  if_icmpge true_1
  iconst_0
  goto stop_2
  true_1:
  iconst_1
  stop_2:
  ifeq stop_0
  iload_2
  iconst_1
  if_icmpge true_4
  iconst_0
  goto stop_5
  true_4:
  iconst_1
  stop_5:
  ifeq stop_3
  aload_0
  getfield ShellingSimulation/grid LSimulationGrid;
  iload_1
  iconst_1
  isub
  iload_2
  iconst_1
  isub
  invokevirtual SimulationGrid/get(II)Ljava/lang/Character;
  dup
  astore 7
  pop
  iload 5
  iconst_1
  iadd
  dup
  istore 5
  pop
  iload_3
  aload 7
  invokevirtual java/lang/Character/charValue()C
  if_icmpeq true_7
  iconst_0
  goto stop_8
  true_7:
  iconst_1
  stop_8:
  ifeq stop_6
  iload 4
  iconst_1
  iadd
  dup
  istore 4
  pop
  stop_6:
  stop_3:
  aload_0
  getfield ShellingSimulation/grid LSimulationGrid;
  iload_1
  iconst_1
  isub
  iload_2
  invokevirtual SimulationGrid/get(II)Ljava/lang/Character;
  dup
  astore 7
  pop
  iload_3
  aload 7
  invokevirtual java/lang/Character/charValue()C
  if_icmpeq true_10
  iconst_0
  goto stop_11
  true_10:
  iconst_1
  stop_11:
  ifeq stop_9
  iload 5
  iconst_1
  iadd
  dup
  istore 5
  pop
  iload 4
  iconst_1
  iadd
  dup
  istore 4
  pop
  stop_9:
  iload_2
  aload_0
  getfield ShellingSimulation/gridSize I
  iconst_2
  isub
  if_icmple true_13
  iconst_0
  goto stop_14
  true_13:
  iconst_1
  stop_14:
  ifeq stop_12
  aload_0
  getfield ShellingSimulation/grid LSimulationGrid;
  iload_1
  iconst_1
  isub
  iload_2
  iconst_1
  iadd
  invokevirtual SimulationGrid/get(II)Ljava/lang/Character;
  dup
  astore 7
  pop
  iload 5
  iconst_1
  iadd
  dup
  istore 5
  pop
  iload_3
  aload 7
  invokevirtual java/lang/Character/charValue()C
  if_icmpeq true_16
  iconst_0
  goto stop_17
  true_16:
  iconst_1
  stop_17:
  ifeq stop_15
  iload 4
  iconst_1
  iadd
  dup
  istore 4
  pop
  stop_15:
  stop_12:
  stop_0:
  iload_2
  iconst_1
  if_icmpge true_19
  iconst_0
  goto stop_20
  true_19:
  iconst_1
  stop_20:
  ifeq stop_18
  aload_0
  getfield ShellingSimulation/grid LSimulationGrid;
  iload_1
  iload_2
  iconst_1
  isub
  invokevirtual SimulationGrid/get(II)Ljava/lang/Character;
  dup
  astore 7
  pop
  iload 5
  iconst_1
  iadd
  dup
  istore 5
  pop
  iload_3
  aload 7
  invokevirtual java/lang/Character/charValue()C
  if_icmpeq true_22
  iconst_0
  goto stop_23
  true_22:
  iconst_1
  stop_23:
  ifeq stop_21
  iload 4
  iconst_1
  iadd
  dup
  istore 4
  pop
  stop_21:
  stop_18:
  iload_2
  aload_0
  getfield ShellingSimulation/gridSize I
  iconst_2
  isub
  if_icmple true_25
  iconst_0
  goto stop_26
  true_25:
  iconst_1
  stop_26:
  ifeq stop_24
  aload_0
  getfield ShellingSimulation/grid LSimulationGrid;
  iload_1
  iload_2
  iconst_1
  iadd
  invokevirtual SimulationGrid/get(II)Ljava/lang/Character;
  dup
  astore 7
  pop
  iload 5
  iconst_1
  iadd
  dup
  istore 5
  pop
  iload_3
  aload 7
  invokevirtual java/lang/Character/charValue()C
  if_icmpeq true_28
  iconst_0
  goto stop_29
  true_28:
  iconst_1
  stop_29:
  ifeq stop_27
  iload 4
  iconst_1
  iadd
  dup
  istore 4
  pop
  stop_27:
  stop_24:
  iload_1
  aload_0
  getfield ShellingSimulation/gridSize I
  iconst_2
  isub
  if_icmple true_31
  iconst_0
  goto stop_32
  true_31:
  iconst_1
  stop_32:
  ifeq stop_30
  iload_2
  iconst_1
  if_icmpge true_34
  iconst_0
  goto stop_35
  true_34:
  iconst_1
  stop_35:
  ifeq stop_33
  aload_0
  getfield ShellingSimulation/grid LSimulationGrid;
  iload_1
  iconst_1
  iadd
  iload_2
  iconst_1
  isub
  invokevirtual SimulationGrid/get(II)Ljava/lang/Character;
  dup
  astore 7
  pop
  iload 5
  iconst_1
  iadd
  dup
  istore 5
  pop
  iload_3
  aload 7
  invokevirtual java/lang/Character/charValue()C
  if_icmpeq true_37
  iconst_0
  goto stop_38
  true_37:
  iconst_1
  stop_38:
  ifeq stop_36
  iload 4
  iconst_1
  iadd
  dup
  istore 4
  pop
  stop_36:
  stop_33:
  aload_0
  getfield ShellingSimulation/grid LSimulationGrid;
  iload_1
  iconst_1
  iadd
  iload_2
  invokevirtual SimulationGrid/get(II)Ljava/lang/Character;
  dup
  astore 7
  pop
  iload 5
  iconst_1
  iadd
  dup
  istore 5
  pop
  iload_3
  aload 7
  invokevirtual java/lang/Character/charValue()C
  if_icmpeq true_40
  iconst_0
  goto stop_41
  true_40:
  iconst_1
  stop_41:
  ifeq stop_39
  iload 4
  iconst_1
  iadd
  dup
  istore 4
  pop
  stop_39:
  iload_2
  aload_0
  getfield ShellingSimulation/gridSize I
  iconst_2
  isub
  if_icmple true_43
  iconst_0
  goto stop_44
  true_43:
  iconst_1
  stop_44:
  ifeq stop_42
  aload_0
  getfield ShellingSimulation/grid LSimulationGrid;
  iload_1
  iconst_1
  iadd
  iload_2
  iconst_1
  iadd
  invokevirtual SimulationGrid/get(II)Ljava/lang/Character;
  dup
  astore 7
  pop
  iload 5
  iconst_1
  iadd
  dup
  istore 5
  pop
  iload_3
  aload 7
  invokevirtual java/lang/Character/charValue()C
  if_icmpeq true_46
  iconst_0
  goto stop_47
  true_46:
  iconst_1
  stop_47:
  ifeq stop_45
  iload 4
  iconst_1
  iadd
  dup
  istore 4
  pop
  stop_45:
  stop_42:
  stop_30:
  iload 5
  iconst_3
  if_icmpeq true_51
  iconst_0
  goto stop_52
  true_51:
  iconst_1
  stop_52:
  dup
  ifne true_50
  pop
  iload 5
  iconst_5
  if_icmpeq true_53
  iconst_0
  goto stop_54
  true_53:
  iconst_1
  stop_54:
  true_50:
  ifeq else_48
  aload_0
  iload 5
  invokevirtual ShellingSimulation/getEquivalentToleranceLevel(I)I
  dup
  istore 6
  pop
  goto stop_49
  else_48:
  aload_0
  getfield ShellingSimulation/toleranceLevel I
  dup
  istore 6
  pop
  stop_49:
  iload 4
  iload 6
  if_icmpge true_57
  iconst_0
  goto stop_58
  true_57:
  iconst_1
  stop_58:
  ifeq else_55
  iconst_1
  ireturn
  goto stop_56
  else_55:
  iconst_0
  ireturn
  stop_56:
  nop
.end method

.method public getEquivalentToleranceLevel(I)I
  .limit locals 3
  .limit stack 25
  iload_1
  iconst_3
  if_icmpeq true_2
  iconst_0
  goto stop_3
  true_2:
  iconst_1
  stop_3:
  ifeq else_0
  aload_0
  getfield ShellingSimulation/cornerToleranceLevels Ljava/util/Vector;
  aload_0
  getfield ShellingSimulation/toleranceLevel I
  invokevirtual java/util/Vector/elementAt(I)Ljava/lang/Object;
  checkcast java/lang/Integer
  dup
  astore_2
  pop
  aload_2
  invokevirtual java/lang/Integer/intValue()I
  ireturn
  goto stop_1
  else_0:
  aload_0
  getfield ShellingSimulation/nonCornerToleranceLevels Ljava/util/Vector;
  aload_0
  getfield ShellingSimulation/toleranceLevel I
  invokevirtual java/util/Vector/elementAt(I)Ljava/lang/Object;
  checkcast java/lang/Integer
  dup
  astore_2
  pop
  aload_2
  invokevirtual java/lang/Integer/intValue()I
  ireturn
  stop_1:
  nop
.end method

.method public initializeEmptyGrid()V
  .limit locals 3
  .limit stack 25
  iconst_0
  dup
  istore_1
  pop
  start_0:
  iload_1
  aload_0
  getfield ShellingSimulation/gridSize I
  if_icmplt true_2
  iconst_0
  goto stop_3
  true_2:
  iconst_1
  stop_3:
  ifeq stop_1
  iconst_0
  dup
  istore_2
  pop
  start_4:
  iload_2
  aload_0
  getfield ShellingSimulation/gridSize I
  if_icmplt true_6
  iconst_0
  goto stop_7
  true_6:
  iconst_1
  stop_7:
  ifeq stop_5
  aload_0
  getfield ShellingSimulation/grid LSimulationGrid;
  iload_1
  iload_2
  aload_0
  getfield ShellingSimulation/EMPTY Ljava/lang/Character;
  invokevirtual SimulationGrid/set(IILjava/lang/Character;)V
  iload_2
  iconst_1
  iadd
  dup
  istore_2
  pop
  goto start_4
  stop_5:
  iload_1
  iconst_1
  iadd
  dup
  istore_1
  pop
  goto start_0
  stop_1:
  return
.end method

.method public initializeToleranceLevelVectors()V
  .limit locals 7
  .limit stack 25
  new java/lang/Integer
  dup
  iconst_0
  invokenonvirtual java/lang/Integer/<init>(I)V
  dup
  astore_1
  pop
  new java/lang/Integer
  dup
  iconst_1
  invokenonvirtual java/lang/Integer/<init>(I)V
  dup
  astore_2
  pop
  new java/lang/Integer
  dup
  iconst_2
  invokenonvirtual java/lang/Integer/<init>(I)V
  dup
  astore_3
  pop
  new java/lang/Integer
  dup
  iconst_3
  invokenonvirtual java/lang/Integer/<init>(I)V
  dup
  astore 4
  pop
  new java/lang/Integer
  dup
  iconst_4
  invokenonvirtual java/lang/Integer/<init>(I)V
  dup
  astore 5
  pop
  new java/lang/Integer
  dup
  iconst_5
  invokenonvirtual java/lang/Integer/<init>(I)V
  dup
  astore 6
  pop
  new java/util/Vector
  dup
  invokenonvirtual java/util/Vector/<init>()V
  dup
  aload_0
  swap
  putfield ShellingSimulation/cornerToleranceLevels Ljava/util/Vector;
  pop
  new java/util/Vector
  dup
  invokenonvirtual java/util/Vector/<init>()V
  dup
  aload_0
  swap
  putfield ShellingSimulation/nonCornerToleranceLevels Ljava/util/Vector;
  pop
  aload_0
  getfield ShellingSimulation/nonCornerToleranceLevels Ljava/util/Vector;
  aload_1
  iconst_0
  invokevirtual java/util/Vector/insertElementAt(Ljava/lang/Object;I)V
  aload_0
  getfield ShellingSimulation/nonCornerToleranceLevels Ljava/util/Vector;
  aload_2
  iconst_1
  invokevirtual java/util/Vector/insertElementAt(Ljava/lang/Object;I)V
  aload_0
  getfield ShellingSimulation/nonCornerToleranceLevels Ljava/util/Vector;
  aload_2
  iconst_2
  invokevirtual java/util/Vector/insertElementAt(Ljava/lang/Object;I)V
  aload_0
  getfield ShellingSimulation/nonCornerToleranceLevels Ljava/util/Vector;
  aload_3
  iconst_3
  invokevirtual java/util/Vector/insertElementAt(Ljava/lang/Object;I)V
  aload_0
  getfield ShellingSimulation/nonCornerToleranceLevels Ljava/util/Vector;
  aload_3
  iconst_4
  invokevirtual java/util/Vector/insertElementAt(Ljava/lang/Object;I)V
  aload_0
  getfield ShellingSimulation/nonCornerToleranceLevels Ljava/util/Vector;
  aload 4
  iconst_5
  invokevirtual java/util/Vector/insertElementAt(Ljava/lang/Object;I)V
  aload_0
  getfield ShellingSimulation/nonCornerToleranceLevels Ljava/util/Vector;
  aload 4
  ldc 6
  invokevirtual java/util/Vector/insertElementAt(Ljava/lang/Object;I)V
  aload_0
  getfield ShellingSimulation/nonCornerToleranceLevels Ljava/util/Vector;
  aload 5
  ldc 7
  invokevirtual java/util/Vector/insertElementAt(Ljava/lang/Object;I)V
  aload_0
  getfield ShellingSimulation/nonCornerToleranceLevels Ljava/util/Vector;
  aload 6
  ldc 8
  invokevirtual java/util/Vector/insertElementAt(Ljava/lang/Object;I)V
  aload_0
  getfield ShellingSimulation/cornerToleranceLevels Ljava/util/Vector;
  aload_1
  iconst_0
  invokevirtual java/util/Vector/insertElementAt(Ljava/lang/Object;I)V
  aload_0
  getfield ShellingSimulation/cornerToleranceLevels Ljava/util/Vector;
  aload_2
  iconst_1
  invokevirtual java/util/Vector/insertElementAt(Ljava/lang/Object;I)V
  aload_0
  getfield ShellingSimulation/cornerToleranceLevels Ljava/util/Vector;
  aload_2
  iconst_2
  invokevirtual java/util/Vector/insertElementAt(Ljava/lang/Object;I)V
  aload_0
  getfield ShellingSimulation/cornerToleranceLevels Ljava/util/Vector;
  aload_2
  iconst_3
  invokevirtual java/util/Vector/insertElementAt(Ljava/lang/Object;I)V
  aload_0
  getfield ShellingSimulation/cornerToleranceLevels Ljava/util/Vector;
  aload_3
  iconst_4
  invokevirtual java/util/Vector/insertElementAt(Ljava/lang/Object;I)V
  aload_0
  getfield ShellingSimulation/cornerToleranceLevels Ljava/util/Vector;
  aload_3
  iconst_5
  invokevirtual java/util/Vector/insertElementAt(Ljava/lang/Object;I)V
  aload_0
  getfield ShellingSimulation/cornerToleranceLevels Ljava/util/Vector;
  aload_3
  ldc 6
  invokevirtual java/util/Vector/insertElementAt(Ljava/lang/Object;I)V
  aload_0
  getfield ShellingSimulation/cornerToleranceLevels Ljava/util/Vector;
  aload 4
  ldc 7
  invokevirtual java/util/Vector/insertElementAt(Ljava/lang/Object;I)V
  aload_0
  getfield ShellingSimulation/cornerToleranceLevels Ljava/util/Vector;
  aload 4
  ldc 8
  invokevirtual java/util/Vector/insertElementAt(Ljava/lang/Object;I)V
  return
.end method

.method public initializeGridEntries(I)V
  .limit locals 7
  .limit stack 25
  new java/util/Random
  dup
  invokenonvirtual java/util/Random/<init>()V
  dup
  astore_2
  pop
  iload_1
  dup
  aload_0
  swap
  putfield ShellingSimulation/entriesCount I
  pop
  start_0:
  aload_0
  getfield ShellingSimulation/entriesCount I
  iconst_0
  if_icmpgt true_2
  iconst_0
  goto stop_3
  true_2:
  iconst_1
  stop_3:
  ifeq stop_1
  iconst_0
  dup
  istore_3
  pop
  start_4:
  iload_3
  aload_0
  getfield ShellingSimulation/gridSize I
  if_icmplt true_6
  iconst_0
  goto stop_7
  true_6:
  iconst_1
  stop_7:
  ifeq stop_5
  iconst_0
  dup
  istore 4
  pop
  start_8:
  iload 4
  aload_0
  getfield ShellingSimulation/gridSize I
  if_icmplt true_10
  iconst_0
  goto stop_11
  true_10:
  iconst_1
  stop_11:
  ifeq stop_9
  aload_0
  getfield ShellingSimulation/grid LSimulationGrid;
  iload_3
  iload 4
  invokevirtual SimulationGrid/get(II)Ljava/lang/Character;
  dup
  astore 6
  pop
  aload 6
  aload_0
  getfield ShellingSimulation/EMPTY Ljava/lang/Character;
  invokevirtual java/lang/Character/equals(Ljava/lang/Object;)Z
  ifeq stop_12
  aload_2
  ldc 50
  invokevirtual java/util/Random/nextInt(I)I
  dup
  istore 5
  pop
  iload 5
  iconst_3
  irem
  dup
  istore 5
  pop
  iload 5
  iconst_0
  if_icmpgt true_14
  iconst_0
  goto stop_15
  true_14:
  iconst_1
  stop_15:
  ifeq stop_13
  iload 5
  iconst_1
  if_icmpeq true_18
  iconst_0
  goto stop_19
  true_18:
  iconst_1
  stop_19:
  ifeq else_16
  aload_0
  getfield ShellingSimulation/grid LSimulationGrid;
  iload_3
  iload 4
  aload_0
  getfield ShellingSimulation/TYPE1 Ljava/lang/Character;
  invokevirtual SimulationGrid/set(IILjava/lang/Character;)V
  goto stop_17
  else_16:
  aload_0
  getfield ShellingSimulation/grid LSimulationGrid;
  iload_3
  iload 4
  aload_0
  getfield ShellingSimulation/TYPE2 Ljava/lang/Character;
  invokevirtual SimulationGrid/set(IILjava/lang/Character;)V
  stop_17:
  aload_0
  getfield ShellingSimulation/entriesCount I
  iconst_1
  isub
  dup
  aload_0
  swap
  putfield ShellingSimulation/entriesCount I
  pop
  stop_13:
  stop_12:
  iload 4
  iconst_1
  iadd
  dup
  istore 4
  pop
  goto start_8
  stop_9:
  iload_3
  iconst_1
  iadd
  dup
  istore_3
  pop
  goto start_4
  stop_5:
  goto start_0
  stop_1:
  return
.end method

