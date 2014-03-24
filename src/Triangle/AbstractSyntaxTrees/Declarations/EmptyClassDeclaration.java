package Triangle.AbstractSyntaxTrees.Declarations;

import Triangle.AbstractSyntaxTrees.Classes;
import Triangle.AbstractSyntaxTrees.Visitor;
import Triangle.SyntacticAnalyzer.SourcePosition;

public class EmptyClassDeclaration extends Classes {

	public EmptyClassDeclaration(SourcePosition thePosition) {
		super(thePosition);
	}

	@Override
	public Object visit(Visitor v, Object o) {
		return null;
	}

}
