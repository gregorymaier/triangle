package Triangle.AbstractSyntaxTrees;

import Triangle.SyntacticAnalyzer.SourcePosition;

public class ClassDeclaration extends Classes {

	public ClassDeclaration(Identifier iAST, Declaration dAST, SourcePosition thePosition) {
		super(thePosition);
		I = iAST;
		D = dAST;
	}

	@Override
	public Object visit(Visitor v, Object o) {
		// TODO Auto-generated method stub
		return null;
	}

	public Identifier I;
	public Declaration D;
}
