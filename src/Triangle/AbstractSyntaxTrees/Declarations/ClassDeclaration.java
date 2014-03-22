package Triangle.AbstractSyntaxTrees.Declarations;

import Triangle.AbstractSyntaxTrees.Classes;
import Triangle.AbstractSyntaxTrees.Visitor;
import Triangle.AbstractSyntaxTrees.Terminals.ClassIdentifier;
import Triangle.SyntacticAnalyzer.SourcePosition;

public class ClassDeclaration extends Classes {

	public ClassDeclaration(ClassIdentifier iAST, Declaration dAST, SourcePosition thePosition) {
		super(thePosition);
		CI = iAST;
		D = dAST;
	}

	@Override
	public Object visit(Visitor v, Object o) {
		return v.visitClassDeclaration(this, o);
	}

	public ClassIdentifier CI;
	public Declaration D;
}
