package Triangle.AbstractSyntaxTrees;

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
		// TODO Auto-generated method stub
		return null;
	}

	public Vname V;
	public Identifier I;
	public ActualParameterSequence A;
}
