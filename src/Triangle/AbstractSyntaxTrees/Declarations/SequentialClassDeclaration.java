package Triangle.AbstractSyntaxTrees.Declarations;

import Triangle.AbstractSyntaxTrees.Classes;
import Triangle.AbstractSyntaxTrees.Visitor;
import Triangle.SyntacticAnalyzer.SourcePosition;

public class SequentialClassDeclaration extends Classes {

	public SequentialClassDeclaration(Classes c1AST, Classes c2AST, SourcePosition thePosition)
	{
		super(thePosition);
		C1 = c1AST;
		C2 = c2AST;
	}
	
	public Classes C1, C2;

	@Override
	public Object visit(Visitor v, Object o) {
		return v.visitSequentialClassDeclaration(this, o);
	}
}
