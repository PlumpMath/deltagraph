//	$Id$
//	$Source$

package eu.cassiel.deltagraph.lg;

import java.util.List;
import java.util.Set;

import eu.cassiel.deltagraph.IDiff;
import eu.cassiel.deltagraph.IWrapped;

public interface IGraph extends IWrapped {
	/**	Add a new vertex to a graph.

		@return the new graph paired with the new vertex
	 */

	IGraphPlus<IVertex> addVertex();
	
	/**	Retrieve a version (presumably the latest) of a vertex from
		a specific graph. */
	
	IVertex retrieveVertex(IVertex v);
	
	/**	Remove a vertex from a graph (as well as any edges on that vertex).

		@return a new graph
	 */
	
	IGraph removeVertex(IVertex v);

	/**	Return a list of the vertices in this graph.

		@return the vertices
	 */

	Set<IVertex> getVertices();
	
	/**	Add a new edge to a graph (the vertices must be present already).

		@return the new graph paired with the new edge.
	 */
	
	IGraphPlus<IEdge> addEdge(IVertex from, IVertex to);

	/**	Retrieve a version (presumably the latest) of a vertex from
		a specific graph. */

	IEdge retrieveEdge(IEdge e);

	/**	Remove an edge from a graph.

		@return a new graph
	 */
	
	IGraph removeEdge(IEdge e);

	/**	Return a list of the edges in this graph.

		@return the edges
	 */

	Set<IEdge> getEdges();
	
	/**	Get change history (provisional code); head is most recent change
	 	(to this particular graph). */
	
	@SuppressWarnings("rawtypes")
	List<IDiff.Modification> getChangeHistory();
}
