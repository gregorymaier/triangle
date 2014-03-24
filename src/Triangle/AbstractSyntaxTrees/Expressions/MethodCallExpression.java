package Triangle.AbstractSyntaxTrees.Expressions;

import Triangle.AbstractSyntaxTrees.Visitor;
import Triangle.AbstractSyntaxTrees.Vname.MethodCallVname;
import Triangle.SyntacticAnalyzer.SourcePosition;

public class MethodCallExpression extends Expression {

	public MethodCallExpression(MethodCallVname mAST, SourcePosition thePosition) {
		super(thePosition);
		M = mAST;
	}

	@Override
	public Object visit(Visitor v, Object o) {
		return v.visitMethodCallExpression(this, o);
	}

	public MethodCallVname M;
}
