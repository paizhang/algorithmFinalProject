/**
 * 
 */
package com.tcss543.networkflow;

import java.util.Iterator;

import com.tcss543.graphcode.Edge;
import com.tcss543.graphcode.SimpleGraph;
import com.tcss543.graphcode.Vertex;

/**
 * @author Paranjit Singh
 *
 */
public class FordFulkerson {
	private SimpleGraph simpleGraph = null;
	private int sinkIndex = -1;
	@SuppressWarnings("unused")
	private int sourceIndex = -1;

	public boolean dfs(Vertex node, boolean[] visited) {
		Iterator<Edge> iterator;
		Edge edge;
		Vertex vertex;
		if (visited[sinkIndex]) {
			return true;
		}
		for (iterator = simpleGraph.incidentEdges(node); iterator.hasNext();) {
			edge = iterator.next();
			vertex = edge.getSecondEndpoint();
			int ind = simpleGraph.getVertexList().indexOf(vertex);
			if (visited[ind] == false & (Double) edge.getData() > 0) {
				visited[ind] = true;
				vertex.setData(edge.getFirstEndpoint());
				if (dfs(vertex, visited)) {
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean bfs(Vertex node, boolean[] visited) {
		return false;
	}

	private int findEdge(Vertex startNode, Vertex endNode) {
		Iterator<Edge> iterator;
		Edge edge;
		for (iterator = simpleGraph.incidentEdges(startNode); iterator.hasNext();) {
			edge = iterator.next();
			if (edge.getSecondEndpoint() == endNode) {
				return simpleGraph.getEdgeList().indexOf(edge);
			}
		}
		return -1;
	}

	private int findVertex(String vertexName) {
		Iterator<Vertex> iterator;
		int index = 0;
		Vertex vertex;
		for (iterator = simpleGraph.vertices(); iterator.hasNext(); index++) {
			vertex = iterator.next();
			if (vertex.getName().toString().equalsIgnoreCase(vertexName)) {
				break;
			}
		}
		return index;
	}

	public int getBottleNeck() {
		int edgeIndex;
		Edge edge;
		Vertex vertex = (Vertex) simpleGraph.getVertexList().get(sinkIndex);
		int min = 0;
		while (vertex != null) {
			Object parentNode = vertex.getData();
			Vertex parentVertex = (Vertex) parentNode;
			if (parentVertex != null) {
				edgeIndex = findEdge(parentVertex, vertex);
				edge = (Edge) simpleGraph.getEdgeList().get(edgeIndex);
				min = Math.min(min, (int) edge.getData());
			}
			vertex = parentVertex;
		}
		vertex = (Vertex) simpleGraph.getVertexList().get(sinkIndex);
		while (vertex != null) {
			Object parentNode = vertex.getData();
			Vertex parentVertex = (Vertex) parentNode;
			if (parentVertex != null) {
				edgeIndex = findEdge(parentVertex, vertex);
				edge = (Edge) simpleGraph.getEdgeList().get(edgeIndex);
				edge.setData((Double) edge.getData() - min);
				edgeIndex = findEdge(vertex, parentVertex);
				edge = (Edge) simpleGraph.getEdgeList().get(edgeIndex);
				edge.setData((Double) edge.getData() + min);
			}
			vertex = parentVertex;
		}
		return min;
	}

	public int getMaxFlow(SimpleGraph sg, int sourceNodeIndex, int sinkNodeIndex) {
		int maxFlow = 0, bottleNeck = 0;
		initialize(sg, sourceNodeIndex, sinkNodeIndex);
		while (true) {
			if (hasAugmentingPath()) {
				bottleNeck = getBottleNeck();
				maxFlow = maxFlow + bottleNeck;
			} else {
				break;
			}
		}
		return maxFlow;
	}

	public int getMaxFlow(SimpleGraph sg, String sourceNodeName, String sinkNodeName) {
		int maxFlow = 0, bottleNeck = 0;
		initialize(sg, sourceNodeName, sinkNodeName);
		while (true) {
			if (hasAugmentingPath()) {
				bottleNeck = getBottleNeck();
				maxFlow = maxFlow + bottleNeck;
			} else {
				break;
			}
		}
		return maxFlow;
	}

	public boolean hasAugmentingPath() {
		boolean[] visited = new boolean[simpleGraph.numVertices()];
		for (int index = 0; index < simpleGraph.numVertices(); index++) {
			visited[index] = false;
		}
		visited[0] = true;
		if (!dfs((Vertex) simpleGraph.getVertexList().getFirst(), visited)) {
			return false;
		}
		return true;
	}

	private void initialize(SimpleGraph sg, int sourceNodeIndex, int sinkNodeIndex) {
		simpleGraph = sg;
		sourceIndex = sourceNodeIndex;
		sinkIndex = sinkNodeIndex;
	}

	private void initialize(SimpleGraph sg, String sourceNodeName, String sinkNodeName) {
		simpleGraph = sg;
		sourceIndex = findVertex(sourceNodeName);
		sinkIndex = findVertex(sinkNodeName);
	}

}
