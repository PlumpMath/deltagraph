//	$Id$
//	$Source$

package eu.cassiel.deltagraph;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import eu.cassiel.deltagraph.IDiff.Modification;
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
	public void retrieval() {
		IGraphPlus<IVertex> added1 = LG.emptyGraph.addVertex();
		IGraphPlus<IVertex> added2 = added1.getGraph().addVertex();

		IGraphPlus<IEdge> added3 =
			added2.getGraph().addEdge(added1.getItem(), added2.getItem());
		
		IVertex v = added3.getGraph().retrieveVertex(added1.getItem());
		assertEquals(added1.getItem().getId(), v.getId());
		
		IGraphPlus<IVertex> added4 =
			v.putDictionary(added3.getGraph(), v.getDictionary().putProperty("A", 99));

		assertNull(v.getDictionary().getProperty("A"));
		assertEquals(99, added4.getGraph().retrieveVertex(v).getDictionary().getProperty("A"));
		
		IEdge e = added3.getItem();
		IGraphPlus<IEdge> added5 =
			e.putDictionary(added4.getGraph(), e.getDictionary().putProperty("B", 98));

		assertNull(e.getDictionary().getProperty("B"));
		assertEquals(98, added5.getGraph().retrieveEdge(e).getDictionary().getProperty("B"));
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
	
	@Test
	public void topologyChangeHistory() {
		IGraphPlus<IVertex> added1 = LG.emptyGraph.addVertex();
		IGraphPlus<IVertex> added2 = added1.getGraph().addVertex();
		
		IGraphPlus<IEdge> added3 =
			added2.getGraph().addEdge(added1.getItem(), added2.getItem());
		
		@SuppressWarnings("rawtypes")
		List<Modification> history = added3.getGraph().getChangeHistory();

		assertEquals(3, history.size());

		assertEquals(IDiff.ModType.EDGE_ADDED, history.get(0).getModType());
		assertEquals(added3.getItem().getId(),
					 ((IEdge) history.get(0).getNewNode()).getId());
		assertEquals(IDiff.ModType.VERTEX_ADDED, history.get(1).getModType());
		assertEquals(added2.getItem().getId(),
					 ((IVertex) history.get(1).getNewNode()).getId());
	}
	
	@Test
	public void propertyChangeHistory() {
		IGraphPlus<IVertex> added1 = LG.emptyGraph.addVertex();
		IGraphPlus<IVertex> added2 =
			added1.getItem().putDictionary(added1.getGraph(),
										   LG.emptyDict.putProperty("A", 50));
		IGraphPlus<IVertex> added3 =
			added2.getItem().putDictionary(added2.getGraph(),
										   LG.emptyDict.putProperty("B", 99));
		
		@SuppressWarnings("rawtypes")
		List<Modification> history = added3.getGraph().getChangeHistory();

		assertEquals(4, history.size());

		assertEquals(IDiff.ModType.VERTEX_ADDED, history.get(3).getModType());
		assertEquals(IDiff.ModType.PROPERTY_ADDED, history.get(2).getModType());
		assertEquals(IDiff.ModType.PROPERTY_REMOVED, history.get(1).getModType());
		assertEquals(IDiff.ModType.PROPERTY_ADDED, history.get(0).getModType());
		
		@SuppressWarnings("rawtypes")
		Modification m = history.get(0);

		assertEquals("B", m.getKey());
		assertNull(m.getOldValue());
		assertEquals(99, m.getNewValue());
		assertEquals(added1.getItem().getId(), ((IVertex) m.getOldNode()).getId());
		assertEquals(added1.getItem().getId(), ((IVertex) m.getNewNode()).getId());
	}
}
