package Triangle.AbstractSyntaxTrees;

import Triangle.SyntacticAnalyzer.SourcePosition;

public class MethodCallCommand extends Command {

	public MethodCallCommand(MethodCallVname mAST, SourcePosition thePosition) {
		super(thePosition);
		M = mAST;
	}

	@Override
	public Object visit(Visitor v, Object o) {
		// TODO Auto-generated method stub
		return null;
	}

	public MethodCallVname M;
}
