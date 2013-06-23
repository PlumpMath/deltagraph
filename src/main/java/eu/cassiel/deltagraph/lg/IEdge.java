//	$Id$
//	$Source$

package eu.cassiel.deltagraph.lg;

import eu.cassiel.deltagraph.IWrapped;

/**	Directed edge between vertices.

	@author Nick Rothwell, nick@cassiel.com / nick@loadbang.net

 */
public interface IEdge extends IWrapped {
	/**	Get the edge's ID - either globally unique, or (depending on underlying
	 	implementation) referring to the graph. Currently used only in tests.
	 	
		@return the ID
	 */

	int getId();
	
	/**	Get the "other" vertex on this edge from the specified vertex in the given
	 	graph.

		@return the other vertex
	 */
	
	IVertex getOther(IGraph g, IVertex thisVertex);
}
