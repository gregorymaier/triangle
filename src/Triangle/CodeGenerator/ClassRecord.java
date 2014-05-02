package Triangle.CodeGenerator;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Stores all meta-data about a Class needed todo code generation.
 * Generated during contextual analysis. When doing code generation and
 * going through the Class definitions only code for the func/proc(s) should
 * be generated.
 * TODO: put a flag in code generator to do that
 * @author Gregory Maier
 *
 */
public final class ClassRecord {
	// Map member names to their offset in the stack dynamic blocks
	private final Map<String, Integer> _members = new HashMap<String, Integer>();
	// Map member names to their offset in the stack dynamic blocks
	private final Set<String> _methods = new HashSet<String>();
	// Jump locations for methods
	private final Map<String, RuntimeEntity> _jumpAdresses = new HashMap<String, RuntimeEntity>();
	// Keep track of offset
	private int _offset = 0;
	
	public ClassRecord() {
	}
	
	public void addMember(String name, int size) {
		_members.put(name, _offset);
		_offset += size;
	}
	
	public void addMethod(String method) {
		_methods.add(method);
	}
	
	public int getOffset(String name) {
		return _members.get(name);
	}
	
	public int size() {
		return _offset;
	}
	
	public void addMethodAddress(String method, RuntimeEntity entity) {
		_jumpAdresses.put(method, entity);
	}
	
	public RuntimeEntity getMethodAddress(String method) {
		return _jumpAdresses.get(method);
	}
}
