//	$Id$
//	$Source$

package eu.cassiel.deltagraph;

public interface IDiff {
	enum ModType { VERTEX_ADDED,
				   VERTEX_REMOVED,
				   EDGE_ADDED,
				   EDGE_REMOVED,
				   PROPERTY_ADDED,
				   PROPERTY_REMOVED,
				   PROPERTY_CHANGED
				 };
				 
	/**	Single modification. The type parameters are not rigorously
	 	enforced on the Clojure side.
	 */

	interface Modification<Node, PropType> {
		ModType getModType();
		/**	Previous node value, if any. */
		Node getOld();
		/**	Next node value, if any. (At least one of previous or next present.) */
		Node getNew();
		/** Key for property change, if any. */
		String getKey();
		/** Previous property value, if any. */
		PropType getOldValue();
		/** Subsequent property value, if any. */
		PropType getNewValue();
	};
}
