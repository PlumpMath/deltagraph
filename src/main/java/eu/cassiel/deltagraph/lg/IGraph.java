//	$Id$
//	$Source$

package eu.cassiel.deltagraph.lg;

import java.util.List;

public interface IGraph {
	/**	Add a new vertex to a graph.

		@return the new graph paired with the new vertex
	 */

	IGraphPlus<IVertex> addVertex();

	/**	Return a list of the vertices in this graph.

		@return the vertices
	 */

	List<IVertex> getVertices();
}
