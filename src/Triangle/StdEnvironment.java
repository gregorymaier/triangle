/*
 * @(#)StdEnvironment.java                        2.1 2003/10/07
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

package Triangle;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import Triangle.AbstractSyntaxTrees.Declarations.BinaryOperatorDeclaration;
import Triangle.AbstractSyntaxTrees.Declarations.ConstDeclaration;
import Triangle.AbstractSyntaxTrees.Declarations.FuncDeclaration;
import Triangle.AbstractSyntaxTrees.Declarations.ProcDeclaration;
import Triangle.AbstractSyntaxTrees.Declarations.TypeDeclaration;
import Triangle.AbstractSyntaxTrees.Declarations.UnaryOperatorDeclaration;
import Triangle.AbstractSyntaxTrees.TypeDenoters.TypeDenoter;

public final class StdEnvironment {

  // These are small ASTs representing standard types.

  public static TypeDenoter
    booleanType, charType, integerType, anyType, errorType;

  public static TypeDeclaration
    booleanDecl, charDecl, integerDecl;
  
  public static List<TypeDeclaration> classDeclarations =
  											 new ArrayList<TypeDeclaration>();
  
  public static Map<String, TypeDenoter> classTypeDenoters = new TreeMap<String, TypeDenoter>();

  // These are small ASTs representing "declarations" of standard entities.

  public static ConstDeclaration
    falseDecl, trueDecl, maxintDecl;

  public static UnaryOperatorDeclaration
    notDecl;

  public static BinaryOperatorDeclaration
    andDecl, orDecl,
    addDecl, subtractDecl, multiplyDecl, divideDecl, moduloDecl,
    equalDecl, unequalDecl, lessDecl, notlessDecl, greaterDecl, notgreaterDecl;

  public static ProcDeclaration
    getDecl, putDecl, getintDecl, putintDecl, geteolDecl, puteolDecl;

  public static FuncDeclaration
    chrDecl, ordDecl, eolDecl, eofDecl;
  
  public static String[] names = {"Boolean", "false", "true", "\\", "/\\", "\\/", "Integer", "maxint", "+", "-", "*", "/", "//", "<", "<=", ">", ">=", "Char", "chr", "ord", "eof", "eol","get", "put", "getint", "putint", "geteol", "puteol", "=", "\\="};
  public static Set<String> StdEnvNames = new HashSet<String>();
}
