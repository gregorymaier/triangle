package Triangle.AbstractSyntaxTrees.Terminals;

import Triangle.AbstractSyntaxTrees.Visitor;
import Triangle.SyntacticAnalyzer.SourcePosition;

public class ClassIdentifier extends Terminal {

	public ClassIdentifier(String theSpelling, SourcePosition thePosition) {
		super(theSpelling, thePosition);
	}

	@Override
	public Object visit(Visitor v, Object o) {
		return v.visitClassIdentifier(this, o);
	}

}
