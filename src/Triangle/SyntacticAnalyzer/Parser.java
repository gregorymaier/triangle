/*
 * @(#)Parser.java                        2.1 2003/10/07
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

package Triangle.SyntacticAnalyzer;

import Triangle.ErrorReporter;
import Triangle.AbstractSyntaxTrees.Classes;
import Triangle.AbstractSyntaxTrees.Program;
import Triangle.AbstractSyntaxTrees.Aggregates.ArrayAggregate;
import Triangle.AbstractSyntaxTrees.Aggregates.MultipleArrayAggregate;
import Triangle.AbstractSyntaxTrees.Aggregates.MultipleRecordAggregate;
import Triangle.AbstractSyntaxTrees.Aggregates.RecordAggregate;
import Triangle.AbstractSyntaxTrees.Aggregates.SingleArrayAggregate;
import Triangle.AbstractSyntaxTrees.Aggregates.SingleRecordAggregate;
import Triangle.AbstractSyntaxTrees.Commands.AssignCommand;
import Triangle.AbstractSyntaxTrees.Commands.CallCommand;
import Triangle.AbstractSyntaxTrees.Commands.Command;
import Triangle.AbstractSyntaxTrees.Commands.EmptyCommand;
import Triangle.AbstractSyntaxTrees.Commands.IfCommand;
import Triangle.AbstractSyntaxTrees.Commands.LetCommand;
import Triangle.AbstractSyntaxTrees.Commands.MethodCallCommand;
import Triangle.AbstractSyntaxTrees.Commands.SequentialCommand;
import Triangle.AbstractSyntaxTrees.Commands.WhileCommand;
import Triangle.AbstractSyntaxTrees.Declarations.ClassDeclaration;
import Triangle.AbstractSyntaxTrees.Declarations.ConstDeclaration;
import Triangle.AbstractSyntaxTrees.Declarations.Declaration;
import Triangle.AbstractSyntaxTrees.Declarations.EmptyClassDeclaration;
import Triangle.AbstractSyntaxTrees.Declarations.FuncDeclaration;
import Triangle.AbstractSyntaxTrees.Declarations.ProcDeclaration;
import Triangle.AbstractSyntaxTrees.Declarations.SequentialClassDeclaration;
import Triangle.AbstractSyntaxTrees.Declarations.SequentialDeclaration;
import Triangle.AbstractSyntaxTrees.Declarations.TypeDeclaration;
import Triangle.AbstractSyntaxTrees.Declarations.VarDeclaration;
import Triangle.AbstractSyntaxTrees.Expressions.ArrayExpression;
import Triangle.AbstractSyntaxTrees.Expressions.BinaryExpression;
import Triangle.AbstractSyntaxTrees.Expressions.CallExpression;
import Triangle.AbstractSyntaxTrees.Expressions.CharacterExpression;
import Triangle.AbstractSyntaxTrees.Expressions.Expression;
import Triangle.AbstractSyntaxTrees.Expressions.IfExpression;
import Triangle.AbstractSyntaxTrees.Expressions.IntegerExpression;
import Triangle.AbstractSyntaxTrees.Expressions.LetExpression;
import Triangle.AbstractSyntaxTrees.Expressions.MethodCallExpression;
import Triangle.AbstractSyntaxTrees.Expressions.RecordExpression;
import Triangle.AbstractSyntaxTrees.Expressions.UnaryExpression;
import Triangle.AbstractSyntaxTrees.Expressions.VnameExpression;
import Triangle.AbstractSyntaxTrees.Parameters.ActualParameter;
import Triangle.AbstractSyntaxTrees.Parameters.ActualParameterSequence;
import Triangle.AbstractSyntaxTrees.Parameters.ConstActualParameter;
import Triangle.AbstractSyntaxTrees.Parameters.ConstFormalParameter;
import Triangle.AbstractSyntaxTrees.Parameters.EmptyActualParameterSequence;
import Triangle.AbstractSyntaxTrees.Parameters.EmptyFormalParameterSequence;
import Triangle.AbstractSyntaxTrees.Parameters.FormalParameter;
import Triangle.AbstractSyntaxTrees.Parameters.FormalParameterSequence;
import Triangle.AbstractSyntaxTrees.Parameters.FuncActualParameter;
import Triangle.AbstractSyntaxTrees.Parameters.FuncFormalParameter;
import Triangle.AbstractSyntaxTrees.Parameters.MultipleActualParameterSequence;
import Triangle.AbstractSyntaxTrees.Parameters.MultipleFormalParameterSequence;
import Triangle.AbstractSyntaxTrees.Parameters.ProcActualParameter;
import Triangle.AbstractSyntaxTrees.Parameters.ProcFormalParameter;
import Triangle.AbstractSyntaxTrees.Parameters.SingleActualParameterSequence;
import Triangle.AbstractSyntaxTrees.Parameters.SingleFormalParameterSequence;
import Triangle.AbstractSyntaxTrees.Parameters.VarActualParameter;
import Triangle.AbstractSyntaxTrees.Parameters.VarFormalParameter;
import Triangle.AbstractSyntaxTrees.Terminals.CharacterLiteral;
import Triangle.AbstractSyntaxTrees.Terminals.ClassIdentifier;
import Triangle.AbstractSyntaxTrees.Terminals.Identifier;
import Triangle.AbstractSyntaxTrees.Terminals.IntegerLiteral;
import Triangle.AbstractSyntaxTrees.Terminals.Operator;
import Triangle.AbstractSyntaxTrees.TypeDenoters.ArrayTypeDenoter;
import Triangle.AbstractSyntaxTrees.TypeDenoters.ClassTypeDenoter;
import Triangle.AbstractSyntaxTrees.TypeDenoters.FieldTypeDenoter;
import Triangle.AbstractSyntaxTrees.TypeDenoters.MultipleFieldTypeDenoter;
import Triangle.AbstractSyntaxTrees.TypeDenoters.RecordTypeDenoter;
import Triangle.AbstractSyntaxTrees.TypeDenoters.SimpleTypeDenoter;
import Triangle.AbstractSyntaxTrees.TypeDenoters.SingleFieldTypeDenoter;
import Triangle.AbstractSyntaxTrees.TypeDenoters.TypeDenoter;
import Triangle.AbstractSyntaxTrees.Vname.DotVname;
import Triangle.AbstractSyntaxTrees.Vname.MethodCallVname;
import Triangle.AbstractSyntaxTrees.Vname.SimpleVname;
import Triangle.AbstractSyntaxTrees.Vname.SubscriptVname;
import Triangle.AbstractSyntaxTrees.Vname.Vname;
import Triangle.CodeGenerator.ClassRecord;
import Triangle.CodeGenerator.Encoder;

public class Parser {

  private Scanner lexicalAnalyser;
  private ErrorReporter errorReporter;
  private Token currentToken;
  private SourcePosition previousTokenPosition;

  public Parser(Scanner lexer, ErrorReporter reporter) {
    lexicalAnalyser = lexer;
    errorReporter = reporter;
    previousTokenPosition = new SourcePosition();
  }

// accept checks whether the current token matches tokenExpected.
// If so, fetches the next token.
// If not, reports a syntactic error.

  void accept (int tokenExpected) throws SyntaxError {
    if (currentToken.kind == tokenExpected) {
      previousTokenPosition = currentToken.position;
      currentToken = lexicalAnalyser.scan();
    } else {
      syntacticError("\"%\" expected here", Token.spell(tokenExpected));
    }
  }

  void acceptIt() {
    previousTokenPosition = currentToken.position;
    currentToken = lexicalAnalyser.scan();
  }

// start records the position of the start of a phrase.
// This is defined to be the position of the first
// character of the first token of the phrase.

  void start(SourcePosition position) {
    position.start = currentToken.position.start;
  }

// finish records the position of the end of a phrase.
// This is defined to be the position of the last
// character of the last token of the phrase.

  void finish(SourcePosition position) {
    position.finish = previousTokenPosition.finish;
  }

  void syntacticError(String messageTemplate, String tokenQuoted) throws SyntaxError {
    SourcePosition pos = currentToken.position;
    errorReporter.reportError(messageTemplate, tokenQuoted, pos);
    throw(new SyntaxError());
  }
  
///////////////////////////////////////////////////////////////////////////////
//
// PROGRAMS
//
///////////////////////////////////////////////////////////////////////////////

  public Program parseProgram() {

    Program programAST = null;

    previousTokenPosition.start = 0;
    previousTokenPosition.finish = 0;
    currentToken = lexicalAnalyser.scan();

    try {
      // clAST will be null if no classes are defined
      Classes clAST = parseClasses();
      // ./.println("DONE PARSING CLASSES");
      Command cAST = parseCommand();
      programAST = new Program(clAST, cAST, previousTokenPosition);
      if (currentToken.kind != Token.EOT) {
        syntacticError("\"%\" not expected after end of program",
          currentToken.spelling);
      }
    }
    catch (SyntaxError s) { return null; }
    return programAST;
  }
  
///////////////////////////////////////////////////////////////////////////////
//
//CLASSES
//
///////////////////////////////////////////////////////////////////////////////
  
// parseClasses constructs ASTs to represent classes, or returns null if the
// current token is not CLASS
  
  Classes parseClasses() throws SyntaxError
  {
	  Classes classesAST = null;
	  SourcePosition commandPos = new SourcePosition();
	  
	  if(currentToken.kind == Token.CLASS)
	  {
		  start(commandPos);
		  acceptIt();
		  
		  classesAST = parseSingleClass();
		  // System.out.println("CLASS FOUND**********");
		  while(currentToken.kind == Token.SEMICOLON)
		  {
			  acceptIt();
			  accept(Token.CLASS);
			  Classes c2AST = parseSingleClass();
			  
			  finish(commandPos);
			  classesAST = new SequentialClassDeclaration(classesAST, c2AST, commandPos);
			  //	  System.out.println("CLASS FOUND**********");
		  }
	  }
	  else {
		  classesAST = new EmptyClassDeclaration(commandPos);
	  }
	  return classesAST;
  }
  
  private boolean inClass = false;
  private ClassRecord newClassRecord;
  // There is only one form of a class declaration so this is pretty simple
  Classes parseSingleClass() throws SyntaxError
  {
	  
	  SourcePosition commandPos = new SourcePosition();
	  start(commandPos);
	  
	  ClassIdentifier CI = parseClassIdentifier();
	  accept(Token.IS); // ~
	  
	  
	  newClassRecord = new ClassRecord();
	  
	  // Gurad to only allow vars, procs, and funcs
	  inClass = true;
	  Declaration D = parseDeclaration();
	  inClass = false;
	  
	  accept(Token.END); // end
	  
	  finish(commandPos);
	  
	  Encoder.classRecords.put(CI.spelling, newClassRecord);
	  return new ClassDeclaration(CI, D, commandPos);
  }

///////////////////////////////////////////////////////////////////////////////
//
// LITERALS
//
///////////////////////////////////////////////////////////////////////////////

// parseIntegerLiteral parses an integer-literal, and constructs
// a leaf AST to represent it.

  IntegerLiteral parseIntegerLiteral() throws SyntaxError {
    IntegerLiteral IL = null;

    if (currentToken.kind == Token.INTLITERAL) {
      previousTokenPosition = currentToken.position;
      String spelling = currentToken.spelling;
      IL = new IntegerLiteral(spelling, previousTokenPosition);
      currentToken = lexicalAnalyser.scan();
    } else {
      IL = null;
      syntacticError("integer literal expected here", "");
    }
    return IL;
  }

// parseCharacterLiteral parses a character-literal, and constructs a leaf
// AST to represent it.

  CharacterLiteral parseCharacterLiteral() throws SyntaxError {
    CharacterLiteral CL = null;

    if (currentToken.kind == Token.CHARLITERAL) {
      previousTokenPosition = currentToken.position;
      String spelling = currentToken.spelling;
      CL = new CharacterLiteral(spelling, previousTokenPosition);
      currentToken = lexicalAnalyser.scan();
    } else {
      CL = null;
      syntacticError("character literal expected here", "");
    }
    return CL;
  }

// parseIdentifier parses an identifier, and constructs a leaf AST to
// represent it.

  Identifier parseIdentifier() throws SyntaxError {
    Identifier I = null;

    if (currentToken.kind == Token.IDENTIFIER) {
      previousTokenPosition = currentToken.position;
      String spelling = currentToken.spelling;
      I = new Identifier(spelling, previousTokenPosition);
      currentToken = lexicalAnalyser.scan();
    } else {
      I = null;
      syntacticError("identifier expected here", "");
    }
    return I;
  }
  
  private String className = "";
  ClassIdentifier parseClassIdentifier() throws SyntaxError {
	  ClassIdentifier I = null;

	    if (currentToken.kind == Token.IDENTIFIER) {
	      previousTokenPosition = currentToken.position;
	      String spelling = currentToken.spelling;
	      I = new ClassIdentifier(spelling, previousTokenPosition);
	      // For auto inserted params
	      className = spelling;
	      currentToken = lexicalAnalyser.scan();
	    } else {
	      I = null;
	      syntacticError("class identifier expected here", "");
	    }
	    return I;
  }

// parseOperator parses an operator, and constructs a leaf AST to
// represent it.

  Operator parseOperator() throws SyntaxError {
    Operator O = null;

    if (currentToken.kind == Token.OPERATOR) {
      previousTokenPosition = currentToken.position;
      String spelling = currentToken.spelling;
      O = new Operator(spelling, previousTokenPosition);
      currentToken = lexicalAnalyser.scan();
    } else {
      O = null;
      syntacticError("operator expected here", "");
    }
    return O;
  }

///////////////////////////////////////////////////////////////////////////////
//
// COMMANDS
//
///////////////////////////////////////////////////////////////////////////////

// parseCommand parses the command, and constructs an AST
// to represent its phrase structure.

  Command parseCommand() throws SyntaxError {
    Command commandAST = null; // in case there's a syntactic error

    SourcePosition commandPos = new SourcePosition();

    start(commandPos);
    commandAST = parseSingleCommand();
    while (currentToken.kind == Token.SEMICOLON) {
      acceptIt();
      Command c2AST = parseSingleCommand();
      finish(commandPos);
      commandAST = new SequentialCommand(commandAST, c2AST, commandPos);
    }
    return commandAST;
  }

  Command parseSingleCommand() throws SyntaxError {
    Command commandAST = null; // in case there's a syntactic error

    SourcePosition commandPos = new SourcePosition();
    start(commandPos);

    switch (currentToken.kind) {

    case Token.IDENTIFIER:
      {
        Identifier iAST = parseIdentifier();
        if (currentToken.kind == Token.LPAREN) {
          acceptIt();
          ActualParameterSequence apsAST = parseActualParameterSequence();
          accept(Token.RPAREN);
          finish(commandPos);
          commandAST = new CallCommand(iAST, apsAST, commandPos);

        } else {

          /*
           * Here we can have
           * V-name := Expression or 
           * V-name.Identifier ( Actual-Parameter-Sequence )
           */
          Vname vAST = parseRestOfVname(iAST);
          if(vAST.getClass() == MethodCallVname.class)
          {
        	  finish(commandPos);
        	  commandAST = new MethodCallCommand((MethodCallVname)vAST, commandPos);
          }
          else
          {
        	  accept(Token.BECOMES);
        	  Expression eAST = parseExpression();
        	  finish(commandPos);
        	  commandAST = new AssignCommand(vAST, eAST, commandPos);
          }
        }
      }
      break;

    case Token.BEGIN:
      acceptIt();
      commandAST = parseCommand();
      accept(Token.END);
      break;

    case Token.LET:
      {
        acceptIt();
        Declaration dAST = parseDeclaration();
        accept(Token.IN);
        Command cAST = parseSingleCommand();
        finish(commandPos);
        commandAST = new LetCommand(dAST, cAST, commandPos);
      }
      break;

    case Token.IF:
      {
        acceptIt();
        Expression eAST = parseExpression();
        accept(Token.THEN);
        Command c1AST = parseSingleCommand();
        accept(Token.ELSE);
        Command c2AST = parseSingleCommand();
        finish(commandPos);
        commandAST = new IfCommand(eAST, c1AST, c2AST, commandPos);
      }
      break;

    case Token.WHILE:
      {
        acceptIt();
        Expression eAST = parseExpression();
        accept(Token.DO);
        Command cAST = parseSingleCommand();
        finish(commandPos);
        commandAST = new WhileCommand(eAST, cAST, commandPos);
      }
      break;

    case Token.SEMICOLON:
    case Token.END:
    case Token.ELSE:
    case Token.IN:
    case Token.EOT:

      finish(commandPos);
      commandAST = new EmptyCommand(commandPos);
      break;

    default:
      syntacticError("\"%\" cannot start a command",
        currentToken.spelling);
      break;

    }

    return commandAST;
  }

///////////////////////////////////////////////////////////////////////////////
//
// EXPRESSIONS
//
///////////////////////////////////////////////////////////////////////////////

  Expression parseExpression() throws SyntaxError {
    Expression expressionAST = null; // in case there's a syntactic error

    SourcePosition expressionPos = new SourcePosition();

    start (expressionPos);

    switch (currentToken.kind) {

    case Token.LET:
      {
        acceptIt();
        Declaration dAST = parseDeclaration();
        accept(Token.IN);
        Expression eAST = parseExpression();
        finish(expressionPos);
        expressionAST = new LetExpression(dAST, eAST, expressionPos);
      }
      break;

    case Token.IF:
      {
        acceptIt();
        Expression e1AST = parseExpression();
        accept(Token.THEN);
        Expression e2AST = parseExpression();
        accept(Token.ELSE);
        Expression e3AST = parseExpression();
        finish(expressionPos);
        expressionAST = new IfExpression(e1AST, e2AST, e3AST, expressionPos);
      }
      break;

    default:
      expressionAST = parseSecondaryExpression();
      break;
    }
    return expressionAST;
  }

  Expression parseSecondaryExpression() throws SyntaxError {
    Expression expressionAST = null; // in case there's a syntactic error

    SourcePosition expressionPos = new SourcePosition();
    start(expressionPos);

    expressionAST = parsePrimaryExpression();
    while (currentToken.kind == Token.OPERATOR) {
      Operator opAST = parseOperator();
      Expression e2AST = parsePrimaryExpression();
      expressionAST = new BinaryExpression (expressionAST, opAST, e2AST,
        expressionPos);
    }
    return expressionAST;
  }

  Expression parsePrimaryExpression() throws SyntaxError {
    Expression expressionAST = null; // in case there's a syntactic error

    SourcePosition expressionPos = new SourcePosition();
    start(expressionPos);

    switch (currentToken.kind) {

    case Token.INTLITERAL:
      {
        IntegerLiteral ilAST = parseIntegerLiteral();
        finish(expressionPos);
        expressionAST = new IntegerExpression(ilAST, expressionPos);
      }
      break;

    case Token.CHARLITERAL:
      {
        CharacterLiteral clAST= parseCharacterLiteral();
        finish(expressionPos);
        expressionAST = new CharacterExpression(clAST, expressionPos);
      }
      break;

    case Token.LBRACKET:
      {
        acceptIt();
        ArrayAggregate aaAST = parseArrayAggregate();
        accept(Token.RBRACKET);
        finish(expressionPos);
        expressionAST = new ArrayExpression(aaAST, expressionPos);
      }
      break;

    case Token.LCURLY:
      {
        acceptIt();
        RecordAggregate raAST = parseRecordAggregate();
        accept(Token.RCURLY);
        finish(expressionPos);
        expressionAST = new RecordExpression(raAST, expressionPos);
      }
      break;

    case Token.IDENTIFIER:
      {
        Identifier iAST= parseIdentifier();
        if (currentToken.kind == Token.LPAREN) {
          acceptIt();
          ActualParameterSequence apsAST = parseActualParameterSequence();
          accept(Token.RPAREN);
          finish(expressionPos);
          expressionAST = new CallExpression(iAST, apsAST, expressionPos);

        } else {
          // Here we have V-name or V-name.Identifier ( Actual-Parameter-Sequence )
          Vname vAST = parseRestOfVname(iAST);
          finish(expressionPos);
          
          if(vAST.getClass() == MethodCallVname.class)
          {
        	  expressionAST = new MethodCallExpression((MethodCallVname)vAST, expressionPos);
          }
          else
          {
        	  expressionAST = new VnameExpression(vAST, expressionPos);
          }
        }
      }
      break;

    case Token.OPERATOR:
      {
        Operator opAST = parseOperator();
        Expression eAST = parsePrimaryExpression();
        finish(expressionPos);
        expressionAST = new UnaryExpression(opAST, eAST, expressionPos);
      }
      break;

    case Token.LPAREN:
      acceptIt();
      expressionAST = parseExpression();
      accept(Token.RPAREN);
      break;

    default:
      syntacticError("\"%\" cannot start an expression",
        currentToken.spelling);
      break;

    }
    return expressionAST;
  }

  RecordAggregate parseRecordAggregate() throws SyntaxError {
    RecordAggregate aggregateAST = null; // in case there's a syntactic error

    SourcePosition aggregatePos = new SourcePosition();
    start(aggregatePos);

    Identifier iAST = parseIdentifier();
    accept(Token.IS);
    Expression eAST = parseExpression();

    if (currentToken.kind == Token.COMMA) {
      acceptIt();
      RecordAggregate aAST = parseRecordAggregate();
      finish(aggregatePos);
      aggregateAST = new MultipleRecordAggregate(iAST, eAST, aAST, aggregatePos);
    } else {
      finish(aggregatePos);
      aggregateAST = new SingleRecordAggregate(iAST, eAST, aggregatePos);
    }
    return aggregateAST;
  }

  ArrayAggregate parseArrayAggregate() throws SyntaxError {
    ArrayAggregate aggregateAST = null; // in case there's a syntactic error

    SourcePosition aggregatePos = new SourcePosition();
    start(aggregatePos);

    Expression eAST = parseExpression();
    if (currentToken.kind == Token.COMMA) {
      acceptIt();
      ArrayAggregate aAST = parseArrayAggregate();
      finish(aggregatePos);
      aggregateAST = new MultipleArrayAggregate(eAST, aAST, aggregatePos);
    } else {
      finish(aggregatePos);
      aggregateAST = new SingleArrayAggregate(eAST, aggregatePos);
    }
    return aggregateAST;
  }

///////////////////////////////////////////////////////////////////////////////
//
// VALUE-OR-VARIABLE NAMES
//
///////////////////////////////////////////////////////////////////////////////

  Vname parseVname () throws SyntaxError {
    Vname vnameAST = null; // in case there's a syntactic error
    Identifier iAST = parseIdentifier();
    vnameAST = parseRestOfVname(iAST);
    return vnameAST;
  }

  private Vname methodVName;
  private boolean inMethodCall = false;
  Vname parseRestOfVname(Identifier identifierAST) throws SyntaxError {
    SourcePosition vnamePos = new SourcePosition();
    vnamePos = identifierAST.position;
    Vname vAST = new SimpleVname(identifierAST, vnamePos);

    while (currentToken.kind == Token.DOT ||
           currentToken.kind == Token.LBRACKET) {

      if (currentToken.kind == Token.DOT) {
        acceptIt();
        Identifier iAST = parseIdentifier();
        
        if(currentToken.kind == Token.LPAREN) // We have a method call
        {
        	methodVName = vAST;
        	inMethodCall = true;
        	acceptIt();
        	ActualParameterSequence apsAST = parseActualParameterSequence();
        	accept(Token.RPAREN);
        	finish(vnamePos);
        	inMethodCall = false;
        	methodVName = null;
        	return new MethodCallVname(vAST, iAST, apsAST, vnamePos);
        }
        // Otherwise do what would have been done before any modifications
        
        vAST = new DotVname(vAST, iAST, vnamePos);
      } else {
        acceptIt();
        Expression eAST = parseExpression();
        accept(Token.RBRACKET);
        finish(vnamePos);
        vAST = new SubscriptVname(vAST, eAST, vnamePos);
      }
    }
    return vAST;
  }

///////////////////////////////////////////////////////////////////////////////
//
// DECLARATIONS
//
///////////////////////////////////////////////////////////////////////////////

  Declaration parseDeclaration() throws SyntaxError {
    Declaration declarationAST = null; // in case there's a syntactic error

    SourcePosition declarationPos = new SourcePosition();
    start(declarationPos);
    declarationAST = parseSingleDeclaration();
    while (currentToken.kind == Token.SEMICOLON) {
      acceptIt();
      Declaration d2AST = parseSingleDeclaration();
      finish(declarationPos);
      declarationAST = new SequentialDeclaration(declarationAST, d2AST,
        declarationPos);
    }
    return declarationAST;
  }

  private boolean addClassParam = false;
  Declaration parseSingleDeclaration() throws SyntaxError {
    Declaration declarationAST = null; // in case there's a syntactic error

    SourcePosition declarationPos = new SourcePosition();
    start(declarationPos);

    switch (currentToken.kind) {

    case Token.CONST:
      {
    	if(inClass) 
    	{
    		syntacticError("Only var, proc and func declarations are allowed in class definitions",
    		        "");
    		break;
    	}
        acceptIt();
        Identifier iAST = parseIdentifier();
        accept(Token.IS);
        Expression eAST = parseExpression();
        finish(declarationPos);
        declarationAST = new ConstDeclaration(iAST, eAST, declarationPos);
      }
      break;

    case Token.VAR:
      {
    	  boolean addMember = false;
        	if(inClass) {
        		addMember = true;
        		inClass = false;
        	}
        acceptIt();
        Identifier iAST = parseIdentifier();
        accept(Token.COLON);
        TypeDenoter tAST = parseTypeDenoter();
        finish(declarationPos);
        declarationAST = new VarDeclaration(iAST, tAST, declarationPos);
        
        if(addMember) {
        	newClassRecord.addMember(iAST.spelling);
        	inClass = true;
        }
      }
      break;

    case Token.PROC:
      {
    	boolean addMember = false;
      	if(inClass) {
      		addMember = true;
      		inClass = false;
      	}
        acceptIt();
        Identifier iAST = parseIdentifier();
        accept(Token.LPAREN);
        if(addMember)
        	addClassParam = true;
        FormalParameterSequence fpsAST = parseFormalParameterSequence();
        if(addMember)
        	addClassParam = false;
        
        accept(Token.RPAREN);
        accept(Token.IS);
        Command cAST = parseSingleCommand();
        finish(declarationPos);
        declarationAST = new ProcDeclaration(iAST, fpsAST, cAST, declarationPos);
        
        if(addMember) {
        	newClassRecord.addMember(iAST.spelling);
        	inClass = true;
        }
      }
      break;

    case Token.FUNC:
      {
    	  boolean addMember = false;
        	if(inClass) {
        		addMember = true;
        		inClass = false;
        	}
        acceptIt();
        Identifier iAST = parseIdentifier();
        accept(Token.LPAREN);
        if(addMember)
        	addClassParam = true;
        FormalParameterSequence fpsAST = parseFormalParameterSequence();
        if(addMember)
        	addClassParam = false;
        accept(Token.RPAREN);
        accept(Token.COLON);
        TypeDenoter tAST = parseTypeDenoter();
        accept(Token.IS);
        Expression eAST = parseExpression();
        finish(declarationPos);
        declarationAST = new FuncDeclaration(iAST, fpsAST, tAST, eAST,
          declarationPos);
        
        if(addMember) {
        	newClassRecord.addMember(iAST.spelling);
        	inClass = true;
        }
      }
      break;

    case Token.TYPE:
      {
    	if(inClass) 
      	{
      		syntacticError("Only var, proc and func declarations are allowed in class definitions",
      		        "");
      		break;
      	}
        acceptIt();
        Identifier iAST = parseIdentifier();
        accept(Token.IS);
        TypeDenoter tAST = parseTypeDenoter();
        finish(declarationPos);
        declarationAST = new TypeDeclaration(iAST, tAST, declarationPos);
      }
      break;

    default:
      syntacticError("\"%\" cannot start a declaration",
        currentToken.spelling);
      break;

    }
    return declarationAST;
  }

///////////////////////////////////////////////////////////////////////////////
//
// PARAMETERS
//
///////////////////////////////////////////////////////////////////////////////
  //  var instance: className parameter will be added to func/proc defs by
  // another program that I will write
  /**
   * 
   * @return
   * @throws SyntaxError
   */
  FormalParameterSequence parseFormalParameterSequence() throws SyntaxError {
    FormalParameterSequence formalsAST;

    SourcePosition formalsPos = new SourcePosition();

    start(formalsPos);
    if (currentToken.kind == Token.RPAREN) {
      finish(formalsPos);
      
      if(addClassParam) {
    	  formalsAST = new SingleFormalParameterSequence(
    			new VarFormalParameter(
    					  (new Identifier("this", formalsPos)),
    					  (new ClassTypeDenoter(formalsPos, className)),
    					   formalsPos),
    			formalsPos);
      } else {
    	  formalsAST = new EmptyFormalParameterSequence(formalsPos);
      }

    } else {
      if(!addClassParam) {
    	  formalsAST = parseProperFormalParameterSequence();
      } else {
    	  formalsAST = new MultipleFormalParameterSequence(
    			  /////////////////////////////////
    			  new VarFormalParameter(
    			      (new Identifier("this", formalsPos)),
    			      
    			      
    			      (new ClassTypeDenoter(formalsPos, className)),
    			      formalsPos) // VarFormalParam
    			  //////////////////////////////////
    			  
    			  , parseProperFormalParameterSequence()
    			  , formalsPos);
      }
    }
    return formalsAST;
  }

  /*
   * case Token.VAR:
      {
        acceptIt();
        Identifier iAST = parseIdentifier();
        accept(Token.COLON);
        TypeDenoter tAST = parseTypeDenoter();
        finish(formalPos);
        formalAST = new VarFormalParameter(iAST, tAST, formalPos);
      }
      break;
      
      TypeDenoter typeAST = null;
      Identifier iAST = parseIdentifier();
        finish(typePos);
        typeAST = new SimpleTypeDenoter(iAST, typePos);
        
            Identifier I = null;

    if (currentToken.kind == Token.IDENTIFIER) {
      previousTokenPosition = currentToken.position;
      String spelling = currentToken.spelling;
      I = new Identifier(spelling, previousTokenPosition);
   */
  FormalParameterSequence parseProperFormalParameterSequence() throws SyntaxError {
    FormalParameterSequence formalsAST = null; // in case there's a syntactic error;

    SourcePosition formalsPos = new SourcePosition();
    start(formalsPos);
    FormalParameter fpAST = parseFormalParameter();
    if (currentToken.kind == Token.COMMA) {
      acceptIt();
      FormalParameterSequence fpsAST = parseProperFormalParameterSequence();
      finish(formalsPos);
      formalsAST = new MultipleFormalParameterSequence(fpAST, fpsAST,
        formalsPos);

    } else { // case there was only 1 formal parameter
      finish(formalsPos);
      /*formalsAST = new MultipleFormalParameterSequence(
    		  new VarFormalParameter(
					  (new Identifier("this", formalsPos)),
					  (new SimpleTypeDenoter(
							  new Identifier(className, formalsPos),
					   formalsPos)),
					   formalsPos),*/
    		  formalsAST = new SingleFormalParameterSequence(fpAST, formalsPos);
    		  //formalsPos);
    }
    return formalsAST;
  }

  // Syntax forces this not to be a method call
  FormalParameter parseFormalParameter() throws SyntaxError {
    FormalParameter formalAST = null; // in case there's a syntactic error;

    SourcePosition formalPos = new SourcePosition();
    start(formalPos);

    switch (currentToken.kind) {

    case Token.IDENTIFIER:
      {
        Identifier iAST = parseIdentifier();
        accept(Token.COLON);
        TypeDenoter tAST = parseTypeDenoter();
        finish(formalPos);
        formalAST = new ConstFormalParameter(iAST, tAST, formalPos);
      }
      break;

    case Token.VAR:
      {
        acceptIt();
        Identifier iAST = parseIdentifier();
        accept(Token.COLON);
        TypeDenoter tAST = parseTypeDenoter();
        finish(formalPos);
        formalAST = new VarFormalParameter(iAST, tAST, formalPos);
      }
      break;

    case Token.PROC:
      {
        acceptIt();
        Identifier iAST = parseIdentifier();
        accept(Token.LPAREN);
        FormalParameterSequence fpsAST = parseFormalParameterSequence();
        accept(Token.RPAREN);
        finish(formalPos);
        formalAST = new ProcFormalParameter(iAST, fpsAST, formalPos);
      }
      break;

    case Token.FUNC:
      {
        acceptIt();
        Identifier iAST = parseIdentifier();
        accept(Token.LPAREN);
        FormalParameterSequence fpsAST = parseFormalParameterSequence();
        accept(Token.RPAREN);
        accept(Token.COLON);
        TypeDenoter tAST = parseTypeDenoter();
        finish(formalPos);
        formalAST = new FuncFormalParameter(iAST, fpsAST, tAST, formalPos);
      }
      break;

    default:
      syntacticError("\"%\" cannot start a formal parameter",
        currentToken.spelling);
      break;

    }
    return formalAST;
  }


  /** these are commands/expressions NEED TO PASS IN CLASS REF
   * When we are parsing a methodcallvname we get here
   * @return
   * @throws SyntaxError
   */
  ActualParameterSequence parseActualParameterSequence() throws SyntaxError {
    ActualParameterSequence actualsAST;

    SourcePosition actualsPos = new SourcePosition();

    start(actualsPos);
    if (currentToken.kind == Token.RPAREN) {
      finish(actualsPos);
      
      /**
       * We need to pass reference to class
       * case Token.VAR:
      {
        acceptIt();
        Vname vAST = parseVname();
        finish(actualPos);
        actualAST = new VarActualParameter(vAST, actualPos);
      }
      break;
       */
      if(inMethodCall) {
    	  // Put in the Reference
    	  actualsAST = new SingleActualParameterSequence(new VarActualParameter(this.methodVName, actualsPos), actualsPos);
      } else {
    	  actualsAST = new EmptyActualParameterSequence(actualsPos);
      }

    } else { // We have at least 1
      if(!inMethodCall) {
    	  actualsAST = parseProperActualParameterSequence();
      } else {
    	  actualsAST = new MultipleActualParameterSequence(
			  new VarActualParameter(this.methodVName, actualsPos),
			  parseProperActualParameterSequence(),
			  actualsPos);
      }
    }
    return actualsAST;
  }

  /**
   * Called from above
   * @return
   * @throws SyntaxError
   */
  ActualParameterSequence parseProperActualParameterSequence() throws SyntaxError {
    ActualParameterSequence actualsAST = null; // in case there's a syntactic error

    SourcePosition actualsPos = new SourcePosition();

    start(actualsPos);
    ActualParameter apAST = parseActualParameter();
    if (currentToken.kind == Token.COMMA) {
      acceptIt();
      ActualParameterSequence apsAST = parseProperActualParameterSequence();
      finish(actualsPos);
      actualsAST = new MultipleActualParameterSequence(apAST, apsAST,
        actualsPos);
    } else { // case there is only 1 parameter there normally so now 2 if in class
      finish(actualsPos);
      actualsAST = new SingleActualParameterSequence(apAST, actualsPos);
    }
    return actualsAST;
  }

  // TODO: don't need to mess with formalparameter just actual
  ActualParameter parseActualParameter() throws SyntaxError {
    ActualParameter actualAST = null; // in case there's a syntactic error

    SourcePosition actualPos = new SourcePosition();

    start(actualPos);

    switch (currentToken.kind) {

    case Token.IDENTIFIER:
    case Token.INTLITERAL:
    case Token.CHARLITERAL:
    case Token.OPERATOR:
    case Token.LET:
    case Token.IF:
    case Token.LPAREN:
    case Token.LBRACKET:
    case Token.LCURLY:
      {
    	  //TODO: this will end up as the methodcallvname
        Expression eAST = parseExpression();
        finish(actualPos);
  	  //System.out.println("~~~~~~~~PARSER~~~~~~~~~~~~~~~~~VISITCONSTACTUALPARAMETER~~~~~~~~~~~~~~~~~~~~~~~~~~~");

        actualAST = new ConstActualParameter(eAST, actualPos);
        // System.out.println("~~~~~~~~~PARSER~~~~~~~~~~~~~~~~VISITCONSTACTUALPARAMETER~~~~~~~~~~~~~~~~~~~~~~~~~~~");

      }
      break;

    case Token.VAR:
      {
        acceptIt();
        Vname vAST = parseVname();
        finish(actualPos);
        actualAST = new VarActualParameter(vAST, actualPos);
      }
      break;

    case Token.PROC:
      {
        acceptIt();
        Identifier iAST = parseIdentifier();
        finish(actualPos);
        actualAST = new ProcActualParameter(iAST, actualPos);
      }
      break;

    case Token.FUNC:
      {
        acceptIt();
        Identifier iAST = parseIdentifier();
        finish(actualPos);
        actualAST = new FuncActualParameter(iAST, actualPos);
      }
      break;

    default:
      syntacticError("\"%\" cannot start an actual parameter",
        currentToken.spelling);
      break;

    }
    return actualAST;
  }

///////////////////////////////////////////////////////////////////////////////
//
// TYPE-DENOTERS
//
///////////////////////////////////////////////////////////////////////////////

  TypeDenoter parseTypeDenoter() throws SyntaxError {
    TypeDenoter typeAST = null; // in case there's a syntactic error
    SourcePosition typePos = new SourcePosition();

    start(typePos);

    switch (currentToken.kind) {

    case Token.IDENTIFIER:
      {
        Identifier iAST = parseIdentifier();
        finish(typePos);
        typeAST = new SimpleTypeDenoter(iAST, typePos);
      }
      break;

    case Token.ARRAY:
      {
        acceptIt();
        IntegerLiteral ilAST = parseIntegerLiteral();
        accept(Token.OF);
        TypeDenoter tAST = parseTypeDenoter();
        finish(typePos);
        typeAST = new ArrayTypeDenoter(ilAST, tAST, typePos);
      }
      break;

    case Token.RECORD:
      {
        acceptIt();
        FieldTypeDenoter fAST = parseFieldTypeDenoter();
        accept(Token.END);
        finish(typePos);
        typeAST = new RecordTypeDenoter(fAST, typePos);
      }
      break;

    default:
      syntacticError("\"%\" cannot start a type denoter",
        currentToken.spelling);
      break;

    }
    return typeAST;
  }

  FieldTypeDenoter parseFieldTypeDenoter() throws SyntaxError {
    FieldTypeDenoter fieldAST = null; // in case there's a syntactic error

    SourcePosition fieldPos = new SourcePosition();

    start(fieldPos);
    Identifier iAST = parseIdentifier();
    accept(Token.COLON);
    TypeDenoter tAST = parseTypeDenoter();
    if (currentToken.kind == Token.COMMA) {
      acceptIt();
      FieldTypeDenoter fAST = parseFieldTypeDenoter();
      finish(fieldPos);
      fieldAST = new MultipleFieldTypeDenoter(iAST, tAST, fAST, fieldPos);
    } else {
      finish(fieldPos);
      fieldAST = new SingleFieldTypeDenoter(iAST, tAST, fieldPos);
    }
    return fieldAST;
  }
}
