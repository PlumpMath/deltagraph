//	$Id$
//	$Source$

package eu.cassiel.deltagraph.lg;

/**	Directed edge between vertices.

	@author Nick Rothwell, nick@cassiel.com / nick@loadbang.net

 */
public interface IEdge {
	/**	Get the edge's ID - either globally unique, or (depending on underlying
	 	implementation) referring to the graph.
	 	
		@return the ID
	 */

	int getId();
}
