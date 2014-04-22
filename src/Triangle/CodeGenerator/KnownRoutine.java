/*
 * @(#)KnownRoutine.java                        2.1 2003/10/07
 *
 * Copyright (C) 1999, 2003 D.A. Watt and D.F. Brown
 * Dept. of Computing Science, University of Glasgow, Glasgow G12 8QQ Scotland
 * and School of Computer and Math Sciences, The Robert Gordon University,
 * St. Andrew Street, Aberdeen AB25 1HG, Scotland.
 * All rights reserved.
 *
 * This software is provided free for educational use only. It may
 * not be used for commercial purposes without the prior written permission
 * of the authors.
 */

package Triangle.CodeGenerator;

import TAM.Machine;
import Triangle.AbstractSyntaxTrees.Parameters.ProcActualParameter;

public class KnownRoutine extends RuntimeEntity {

  public KnownRoutine () {
    super();
    address = null;
  }

  public KnownRoutine (int size, int level, int displacement) {
    super(size);
    address = new ObjectAddress(level, displacement);
  }

  public ObjectAddress address;

}

/*

public Object visitProcActualParameter(ProcActualParameter ast, Object o) {
Frame frame = (Frame) o;
if (ast.I.decl.entity instanceof KnownRoutine) {
  ObjectAddress address = ((KnownRoutine) ast.I.decl.entity).address;
  // static link, code address
  emit(Machine.LOADAop, 0, displayRegister(frame.level, address.level), 0);
  emit(Machine.LOADAop, 0, Machine.CBr, address.displacement);
} else if (ast.I.decl.entity instanceof UnknownRoutine) {
  ObjectAddress address = ((UnknownRoutine) ast.I.decl.entity).address;
  emit(Machine.LOADop, Machine.closureSize, displayRegister(frame.level,
       address.level), address.displacement);
} else if (ast.I.decl.entity instanceof PrimitiveRoutine) {
  int displacement = ((PrimitiveRoutine) ast.I.decl.entity).displacement;
  // static link, code address
  emit(Machine.LOADAop, 0, Machine.SBr, 0);
  emit(Machine.LOADAop, 0, Machine.PBr, displacement);
}
return new Integer(Machine.closureSize);
}

*/