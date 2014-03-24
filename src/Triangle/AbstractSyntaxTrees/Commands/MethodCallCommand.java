package Triangle.AbstractSyntaxTrees.Commands;

import Triangle.AbstractSyntaxTrees.Visitor;
import Triangle.AbstractSyntaxTrees.Vname.MethodCallVname;
import Triangle.SyntacticAnalyzer.SourcePosition;

public class MethodCallCommand extends Command {

	public MethodCallCommand(MethodCallVname mAST, SourcePosition thePosition) {
		super(thePosition);
		M = mAST;
	}

	@Override
	public Object visit(Visitor v, Object o) {
		return v.visitMethodCallCommand(this, o);
	}

	public MethodCallVname M;
}
