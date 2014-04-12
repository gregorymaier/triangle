package Triangle.CodeGenerator;

import java.util.HashMap;
import java.util.Map;

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
	private final Map<String, Integer> _map = new HashMap<String, Integer>();
	// Jump locations for methods
	private final Map<String, Integer> _jumpAdresses = new HashMap<String, Integer>();
	// Keep track of offset
	private int _offset = 0;
	
	public ClassRecord() {
	}
	
	public void addMember(String name) {
		_map.put(name, _offset++);
	}
	
	public int getOffset(String name) {
		return _map.get(name);
	}
	
	public int size() {
		return _offset;
	}
	
	public void addMethodAddress(String method, Integer address) {
		_jumpAdresses.put(method, address);
	}
	
	public int getMethodAddress(String method) {
		return _jumpAdresses.get(method);
	}
}
