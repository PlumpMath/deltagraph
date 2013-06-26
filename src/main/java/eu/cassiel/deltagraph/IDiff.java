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
				 
	interface Modification<Node, PropType> {
		ModType getModType();
		Node getOld();
		Node getNew();
		IProperty<PropType> getKey();
		PropType getOldValue();
		PropType getNewValue();
	};
}
