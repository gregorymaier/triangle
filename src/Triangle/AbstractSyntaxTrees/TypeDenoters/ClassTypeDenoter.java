package Triangle.AbstractSyntaxTrees.TypeDenoters;

import Triangle.AbstractSyntaxTrees.Visitor;
import Triangle.SyntacticAnalyzer.SourcePosition;

public class ClassTypeDenoter extends TypeDenoter {

	public ClassTypeDenoter(SourcePosition thePosition, String name) {
		super(thePosition);
		ClassName = name;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof ErrorTypeDenoter)
		      return true;
		    else
		      return (obj != null && 
		  			obj instanceof ClassTypeDenoter && 
					ClassName.equals(((ClassTypeDenoter)obj).ClassName));
	}

	@Override
	public Object visit(Visitor v, Object o) {
		System.out.println("Visit ClassTypeDenoter");
		return v.visitClassTypeDenoter(this, o);
	}

	public String ClassName;
}
