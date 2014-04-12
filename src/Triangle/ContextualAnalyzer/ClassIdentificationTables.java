package Triangle.ContextualAnalyzer;

import java.util.Map;
import java.util.TreeMap;

/**
 * Class for storing an IdentificationTable per Class definition
 * that is needed since each class has its own scope.
 */
public class ClassIdentificationTables {
	
	private Map<String, IdentificationTable> mClassScopes;

	public ClassIdentificationTables() {
		mClassScopes = new TreeMap<String, IdentificationTable>();
	}
	
	/**
	 * Create a new IdTable for the given class
	 * @param className Name of the new class to be defined
	 * @return true iff there was no problem adding class
	 */
	public boolean addNewClass(String className) {
		boolean actionResult = false;
		// If there is not already a class defined with this name
		if(!mClassScopes.containsKey(className)) {
			// Add a IdTable for it
			mClassScopes.put(className, new IdentificationTable(true));
			actionResult = true;
		}
		return actionResult;
	}
	
	public IdentificationTable getIdTableForClass(String className) {
		return mClassScopes.get(className);
	}

}
