//	$Id$
//	$Source$

package eu.cassiel.deltagraph.lg;

/**	Vertex, carrying property map.

	@author Nick Rothwell, nick@cassiel.com / nick@loadbang.net
 */

public interface IVertex {
	/**	Get the vertex's ID - either globally unique, or (depending on underlying
 		implementation) referring to the graph.
 	
		@return the ID
	 */

	int getId();
}
