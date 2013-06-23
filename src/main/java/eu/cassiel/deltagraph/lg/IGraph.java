//	$Id$
//	$Source$

package eu.cassiel.deltagraph.lg;

import java.util.List;

import eu.cassiel.deltagraph.IWrapped;

public interface IGraph extends IWrapped {
	/**	Add a new vertex to a graph.

		@return the new graph paired with the new vertex
	 */

	IGraphPlus<IVertex> addVertex();
	
	/**	Remove a vertex from a graph (as well as any edges on that vertex).

		@return a new graph
	 */
	
	IGraph removeVertex(IVertex v);

	/**	Return a list of the vertices in this graph.

		@return the vertices
	 */

	List<IVertex> getVertices();
	
	/**	Add a new edge to a graph (the vertices must be present already).

		@return the new graph paired with the new edge.
	 */
	
	IGraphPlus<IEdge> addEdge(IVertex from, IVertex to);

	/**	Remove an edge from a graph.

		@return a new graph
	 */
	
	IGraph removeEdge(IEdge e);

	/**	Return a list of the edges in this graph.

		@return the edges
	 */

	List<IEdge> getEdges();
}
