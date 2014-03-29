package Triangle.AbstractSyntaxTrees.Vname;

import Triangle.AbstractSyntaxTrees.Visitor;
import Triangle.AbstractSyntaxTrees.Parameters.ActualParameterSequence;
import Triangle.AbstractSyntaxTrees.Terminals.Identifier;
import Triangle.SyntacticAnalyzer.SourcePosition;

public class MethodCallVname extends Vname {

	public MethodCallVname(Vname vAST, Identifier iAST, ActualParameterSequence apsAST, SourcePosition thePosition) {
		super(thePosition);
		V = vAST;
		I = iAST;
		A = apsAST;
	}

	@Override
	public Object visit(Visitor v, Object o) {
		return v.visitMethodCallVname(this, o);
	}

	public Vname V;
	public Identifier I;
	public ActualParameterSequence A;
}
