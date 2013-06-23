//	$Id$
//	$Source$

package eu.cassiel.deltagraph;

import static org.junit.Assert.*;

import org.junit.Test;

import eu.cassiel.deltagraph.lg.IGraph;
import eu.cassiel.deltagraph.lg.IGraphPlus;
import eu.cassiel.deltagraph.lg.IVertex;

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
		
		added = added.getGraph().addVertex();
		assertEquals(2, added.getGraph().getVertices().size());
	}
}
