//	$Id$
//	$Source$

package eu.cassiel.deltagraph;

import static org.junit.Assert.*;

import org.junit.Test;

import eu.cassiel.deltagraph.lg.IGraph;
import eu.cassiel.deltagraph.lg.IGraphPlus;
import eu.cassiel.deltagraph.lg.IVertex;
import eu.cassiel.deltagraph.lg.IEdge;

public class LGTest {
	@Test
	public void testEmptyGraph() {
		assertNotNull(LG.emptyGraph);
		assertTrue(LG.emptyGraph instanceof IGraph);
	}
	
	@Test
	public void addVertices() {
		IGraphPlus<IVertex> added = LG.emptyGraph.addVertex();
		assertEquals(1, added.getGraph().getVertices().size());
		assertTrue(added.getItem() instanceof IVertex);

		added = added.getGraph().addVertex();
		assertEquals(2, added.getGraph().getVertices().size());
		
		assertTrue(added.getGraph().getVertices().toArray()[0] instanceof IVertex);
	}
	
	@Test
	public void addEdges() {
		IGraphPlus<IVertex> added1 = LG.emptyGraph.addVertex();
		IGraphPlus<IVertex> added2 = added1.getGraph().addVertex();

		IGraphPlus<IEdge> added3 =
				added2.getGraph().addEdge(added1.getItem(), added2.getItem());

		assertEquals(1, added3.getGraph().getEdges().size());
		assertTrue(added3.getItem() instanceof IEdge);
		assertTrue(added3.getGraph().getEdges().toArray()[0] instanceof IEdge);
	}
	
	@Test
	public void removal() {
		IGraphPlus<IVertex> added1 = LG.emptyGraph.addVertex();
		IGraphPlus<IVertex> added2 = added1.getGraph().addVertex();

		IGraphPlus<IEdge> added3 =
				added2.getGraph().addEdge(added1.getItem(), added2.getItem());

		assertEquals(1, added3.getGraph().removeVertex(added1.getItem()).getVertices().size());
		assertEquals(0, added3.getGraph().removeEdge(added3.getItem()).getEdges().size());
	}
	
	@Test
	public void getOther() {
		IGraphPlus<IVertex> added1 = LG.emptyGraph.addVertex();
		IGraphPlus<IVertex> added2 = added1.getGraph().addVertex();

		IGraphPlus<IEdge> added3 =
				added2.getGraph().addEdge(added1.getItem(), added2.getItem());
		
		assertEquals(added1.getItem().getId(),
					 added3.getItem().getOther(added3.getGraph(), added2.getItem()).getId());
		
		assertEquals(added2.getItem().getId(),
					 added3.getItem().getOther(added3.getGraph(), added1.getItem()).getId());
	}
	
	@Test
	public void dictionaries() {
		IGraphPlus<IVertex> added1 = LG.emptyGraph.addVertex();
		IVertex v = added1.getItem();
		IDict dict = v.getDictionary();
		dict = dict.putProperty("ANSWER", 42);
		IGraphPlus<IVertex> added2 = v.putDictionary(added1.getGraph(), dict);
		
		assertNull(v.getDictionary().getProperty("ANSWER"));
		assertEquals(42, added2.getItem().getDictionary().getProperty("ANSWER"));
	}
}
