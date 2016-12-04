package com.tcss543.networkflow;

import java.util.Iterator;
import java.util.LinkedList;

import com.tcss543.graphcode.Edge;
import com.tcss543.graphcode.SimpleGraph;
import com.tcss543.graphcode.Vertex;

/**
 * The Class FordFulkerson implements the Ford Fulkerson algorithm and returns
 * the maximum flow. It returns the maximum flow in double format e.g. 23.0 If
 * any error occurs it returns -0.0 If source and sink node cannot be reached
 * during initialization it returns 0.0
 * 
 * @author Paranjit Singh
 */
public class FordFulkerson {

	/** A default source node name to be used if none provided. */
	private static final String DEFAULT_SOURCE_NODE = "s";

	/** A default sink node name to be used if none provided. */
	private static final String DEFAULT_SINK_NODE = "t";

	/** A default error value to return in-case any error occurs during initialization. */
	private static final Double ERROR_VALUE = -0.0;

	/** The simple graph object. */
	private SimpleGraph simpleGraph = null;

	/** The sink node index. */
	private int sinkIndex = -1;

	/** The source node index. */
	private int sourceIndex = -1;

	/**
	 * Overloaded Initialize method. Accepts simple graph object along with source and sink node index
	 * 
	 * @param sg
	 *            the SimpleGraph
	 * @param sourceNodeIndex
	 *            the source node index
	 * @param sinkNodeIndex
	 *            the sink node index
	 */
	private void initialize(SimpleGraph sg, int sourceNodeIndex, int sinkNodeIndex) {
		simpleGraph = sg;
		sourceIndex = sourceNodeIndex;
		sinkIndex = sinkNodeIndex;
	}

	/**
	 * Overloaded Initialize method. Accepts simple graph object along with source and sink node name
	 * 
	 * @param sg
	 *            the SimpleGraph
	 * @param sourceNodeName
	 *            the source node name
	 * @param sinkNodeName
	 *            the sink node name
	 */
	private void initialize(SimpleGraph sg, String sourceNodeName, String sinkNodeName) {
		simpleGraph = sg;
		sourceIndex = findVertex(sourceNodeName);
		sinkIndex = findVertex(sinkNodeName);
	}

	/**
	 * This method implements breadth first search algorithm and returns true if
	 * it finds a simple path from source to sink node. This method also updates the path it took
	 * to reach the sink node in the provided path array.
	 * 
	 * @param path
	 *            An array to update the simple path if found 
	 * @return true, if successful
	 */
	private boolean bfs(int path[]) {
		Edge edge;
		boolean visited[] = new boolean[simpleGraph.numVertices()];
		for (int i = 0; i < simpleGraph.numVertices(); ++i) {
			visited[i] = false;
		}
		LinkedList<Vertex> queue = new LinkedList<Vertex>();
		queue.add(simpleGraph.getVertexList().get(sourceIndex));
		visited[sourceIndex] = true;
		while (queue.size() != 0) {
			Vertex vertex = queue.poll();
			int currentParent = simpleGraph.getVertexList().indexOf(vertex);
			for (Iterator<Edge> iterator2 = simpleGraph.incidentEdges(vertex); iterator2.hasNext();) {
				edge = iterator2.next();
				if ((double) edge.getData() != new Double(0)) {
					Vertex endVertex = edge.getSecondEndpoint();
					int index = simpleGraph.getVertexList().indexOf(endVertex);
					if (!visited[index]) {
						queue.add(endVertex);
						visited[index] = true;
						path[index] = currentParent;
					}
				}
			}
		}
		if (visited[sinkIndex]) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * This method finds the vertex with a provided vertex name.
	 *
	 * @param vertexName
	 *            the vertex name
	 * @return the index of the node if found else -1
	 */
	private int findVertex(String vertexName) {
		Iterator<Vertex> iterator;
		int index = 0;
		Vertex vertex;
		for (iterator = simpleGraph.vertices(); iterator.hasNext(); index++) {
			vertex = iterator.next();
			if (vertex.getName().toString().equalsIgnoreCase(vertexName)) {
				return index;
			}
		}
		return -1;
	}

	/**
	 * This method finds an edge between two provided vertex.
	 *
	 * @param startNode
	 *            the start node
	 * @param endNode
	 *            the end node
	 * @return the index of the edge if found else -1
	 */
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

	/**
	 * This method finds the augmenting path in the flow of the Graph.
	 *
	 * @return the augmenting path if found else an empty array
	 */
	private int[] getAugmentingPath() {
		int path[] = new int[simpleGraph.numVertices()];
		for (int index = 0; index < path.length; index++) {
			path[index] = -1;
		}
		if (bfs(path)) {
			return path;
		}
		return new int[0];
	}

	/**
	 * This method augments the given graph with the bottleneck. It reduces
	 * the value of the forward edge by bottleneck and adds/increases the value
	 * of reverse edge by bottleneck.
	 * 
	 * @param path
	 *            the simple path in the graph
	 * @param bottleNeck
	 *            the bottle neck to increase in the flow
	 */
	private void augment(int path[], Double bottleNeck) {
		Edge forwardEdge, reverseEdge;
		Vertex startVertex, endVertex;
		int edgeIndex;
		int startNodeIndex = path[sinkIndex];
		int endNodeIndex = sinkIndex;
		while (startNodeIndex != -1) {
			startVertex = simpleGraph.getVertexList().get(startNodeIndex);
			endVertex = simpleGraph.getVertexList().get(endNodeIndex);
			edgeIndex = findEdge(startVertex, endVertex);
			/**
			 * Update Forward Edge data by reducing it with the value of
			 * bottleNeck
			 */
			forwardEdge = simpleGraph.getEdgeList().get(edgeIndex);
			double forwardEdgeData = (double) forwardEdge.getData() - bottleNeck;
			forwardEdge.setData(forwardEdgeData);
			/**
			 * Update Reverse Edge data by increasing it with the value of
			 * bottleNeck
			 */
			edgeIndex = findEdge(endVertex, startVertex);
			if (edgeIndex == -1) {
				simpleGraph.insertEdge(endVertex, startVertex, bottleNeck, "");
			} else {
				reverseEdge = simpleGraph.getEdgeList().get(edgeIndex);
				double reverseEdgeData = (double) reverseEdge.getData() + bottleNeck;
				reverseEdge.setData(reverseEdgeData);
			}

			endNodeIndex = startNodeIndex;
			startNodeIndex = path[startNodeIndex];
		}
	}

	/**
	 * This method finds the bottle neck in the graph's flow with the provided simple path.
	 *
	 * @param path
	 *            the simple path in the graph
	 * @return the bottle neck
	 */
	private double getBottleNeck(int path[]) {
		int edgeIndex;
		int startNodeIndex = path[sinkIndex];
		int endNodeIndex = sinkIndex;
		Double min = Double.MAX_VALUE;

		while (startNodeIndex != -1) {
			edgeIndex = findEdge(simpleGraph.getVertexList().get(startNodeIndex),
					simpleGraph.getVertexList().get(endNodeIndex));
			min = Math.min((double) min, (double) simpleGraph.getEdgeList().get(edgeIndex).getData());
			endNodeIndex = startNodeIndex;
			startNodeIndex = path[startNodeIndex];
		}
		augment(path, min);
		return min;
	}

	/**
	 * Overloaded getMaxFlow method. Finds the max flow in the graph. This method takes the source and sink node
	 * indexes as parameter. 
	 *
	 * @param sg
	 *            the SimpleGraph
	 * @param sourceNodeIndex
	 *            the source node index
	 * @param sinkNodeIndex
	 *            the sink node index
	 * @return the max flow
	 */
	public double getMaxFlow(SimpleGraph sg, int sourceNodeIndex, int sinkNodeIndex) {
		Double maxFlow = new Double(0);
		Double bottleNeck;
		initialize(sg, sourceNodeIndex, sinkNodeIndex);
		if (sourceIndex == -1 || sinkIndex == -1) {
			return ERROR_VALUE;
		}
		while (true) {
			int path[] = getAugmentingPath();
			if (path.length != 0) {
				bottleNeck = getBottleNeck(path);
				maxFlow = maxFlow + bottleNeck;
			} else {
				break;
			}
		}
		return maxFlow;
	}

	/**
	 * Overloaded getMaxFlow method. Finds the max flow in the graph. This method takes the source and sink node
	 * names as parameter.
	 *
	 * @param sg
	 *            the SimpleGraph
	 * @param sourceNodeName
	 *            the source node name
	 * @param sinkNodeName
	 *            the sink node name
	 * @return the max flow
	 */
	public double getMaxFlow(SimpleGraph sg, String sourceNodeName, String sinkNodeName) {
		Double maxFlow = new Double(0);
		Double bottleNeck;
		initialize(sg, sourceNodeName, sinkNodeName);
		if (sourceIndex == -1 || sinkIndex == -1) {
			return ERROR_VALUE;
		}
		while (true) {
			int path[] = getAugmentingPath();
			if (path.length != 0) {
				bottleNeck = getBottleNeck(path);
				maxFlow = maxFlow + bottleNeck;
			} else {
				break;
			}
		}
		return maxFlow;
	}

	/**
	 * Overloaded getMaxFlow method. Finds the max flow in the graph. This
	 * method uses the default source and sink node name.
	 * 
	 * @param sg
	 *            the SimpleGraph
	 * @return the max flow
	 */
	public double getMaxFlow(SimpleGraph sg) {
		Double maxFlow = new Double(0);
		Double bottleNeck;
		initialize(sg, DEFAULT_SOURCE_NODE, DEFAULT_SINK_NODE);
		if (sourceIndex == -1 || sinkIndex == -1) {
			return ERROR_VALUE;
		}
		while (true) {
			int path[] = getAugmentingPath();
			if (path.length != 0) {
				bottleNeck = getBottleNeck(path);
				maxFlow = maxFlow + bottleNeck;
			} else {
				break;
			}
		}
		return maxFlow;
	}

}
