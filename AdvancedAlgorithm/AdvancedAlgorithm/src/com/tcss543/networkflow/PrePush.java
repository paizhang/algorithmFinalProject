package com.tcss543.networkflow;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.tcss543.graphcode.Edge;
import com.tcss543.graphcode.SimpleGraph;
import com.tcss543.graphcode.Vertex;

/**
 * This class implement Prepush Algorithm. The getMaxFlow() method will return 
 * the max flow of a given graph. It will return -1 if there is no start node or 
 * sink node in the input graph. It will return -2 if there are some edges with 
 * negative capacity. 
 * 
 * @author Pai Zhang
 * @version 1.0
 */
public class PrePush {
	
	/* A HashMap that records the height of each vertex */
	private HashMap<String, Integer> heights = new HashMap<>();
	
	/* A HashMap that records the flow of each edge */
	private HashMap<Edge, Double> flow = new HashMap<>();
	
	/* A HashMap that records the excess value of each vertex */
	private HashMap<Vertex, Double> excess = new HashMap<>();
	
	/* A SimpleGraph object */
	private SimpleGraph simpleGraph;
	
	/* A vertex in the graph with positive excess value */
	private Vertex vExcess = null;
	
	/* The source node of the graph */
	private Vertex sNode = null;
	
	/**
	 * Initialize the input graph
	 * 
	 * @param sg
	 * 			The input SimpleGraph
	 */
	private void intializeGraph(SimpleGraph sg) {
		simpleGraph = sg;
	}
	
	/**
	 * Check the validity input graph. 
	 * 
	 * @return 
	 * 		0   The graph is validate
	 * 		-1 	There is no start node or sink node in the graph
	 * 		-2	There are some edges with negative capacity
	 */
	private int checkGraph() {
		Iterator<Vertex> i;
		Vertex v;
		boolean hasSNode = false, hasSinkNode = false;
		boolean hasNegativeEdge = false;
		for (i = simpleGraph.vertices(); i.hasNext();) {
			v = (Vertex) i.next();
			if (v.getName().equals("s"))
				hasSNode = true;
			if (v.getName().equals("t"))
				hasSinkNode = true;
		}
		if (!hasSNode || !hasSinkNode)
			return -1;
		Iterator<Edge> j;
		Edge e;
		for (j = simpleGraph.edges(); j.hasNext();) {
			e = (Edge) j.next();
			if (Double.parseDouble(e.getData().toString()) < 0) {
				hasNegativeEdge = true;
				break;
			}
		}
		if (hasNegativeEdge)
			return -2;
		else
			return 0;
	}
	
	/**
	 * Initialize the Heights hashmap. The height of the s node 
	 * should be the number of vertex in the graph. The heights 
	 * of other nodes should be zero.
	 */
	private void initializeHeights() {
		Iterator<Vertex> i;
		Vertex v;
		for (i = simpleGraph.vertices(); i.hasNext();) {
			v = (Vertex) i.next();
			//System.out.println("The vertex of the graph:");
			//System.out.println("Vertex " + v.getName());
			if(v.getName().equals("s")) {
				heights.put(v.getName().toString(), simpleGraph.numVertices());
				sNode = v;
			}
			else {
				heights.put(v.getName().toString(), 0);
			}
		}
	}
	
	/**
	 * Initialize the flow hashmap. The flow of edges which start from s node 
	 * should be equal to the capacities of the edges. The flow of other edges 
	 * should be assigned to zero.
	 */
	private void initializeFlow() {
		Iterator<Edge> i;
		Edge e;
		//System.out.println("Initial flow status");
		for (i = simpleGraph.edges(); i.hasNext();) {
			e = (Edge) i.next();
			if(e.getFirstEndpoint().getName().equals("s")) {
				flow.put(e, Double.parseDouble(e.getData().toString()));
				//System.out.println("Flow of Edge " + e.getFirstEndpoint().getName() + " " 
						//+ e.getSecondEndpoint().getName() + ":" + e.getData().toString());
			}
			else {
				flow.put(e, 0.0);
				//System.out.println("Flow of Edge " + e.getFirstEndpoint().getName() + " " 
						//+ e.getSecondEndpoint().getName() + ": 0.0");
			}
		}
	}
	
	/**
	 * Prints out the status of flow of all edges
	 */
	private void showFlowStatus() {
		Iterator<Edge> i;
		Edge e;
		//System.out.println("Flow status:");
		for (i = simpleGraph.edges(); i.hasNext();) {
			e = (Edge) i.next();
			//System.out.println("Flow of Edge " + e.getFirstEndpoint().getName() + " " 
					//+ e.getSecondEndpoint().getName() + ":" + flow.get(e));
		}
	}
	
	/**
	 * Find a node with excess value greater than zero.
	 * 
	 * @return
	 * 			vertex v 	if there exists a node with positive excess value
	 * 			null		if there is no node with positive excess value
	 */
	private Vertex findExcessNode() {
		Vertex v = null;
		Vertex tmp;
		Iterator it = excess.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry)it.next();
			tmp = (Vertex) pair.getKey();
			if (tmp.getName().toString().equals("t"))
				continue;
			if (Double.parseDouble(pair.getValue().toString()) > 0.0) {
				v = (Vertex) pair.getKey();
				return v;
			}
		}
		return v;
	}
	
	/**
	 * Update the excess status of each vertex. The method should be 
	 * called when the flow hashmap is changed. 
	 */
	private void updateExcess() {
		Iterator<Vertex> i;
		Iterator<Edge> j;
		Vertex v;
		Edge e;
		boolean existExcess = false; 
		//System.out.println("Excess status:");
		double input = 0.0, output = 0.0, excessValue = 0.0;
		for (i = simpleGraph.vertices(); i.hasNext();) {
			v = (Vertex) i.next();
			for (j = simpleGraph.incidentEdges(v); j.hasNext();) {
				e = j.next();
				if (e.getFirstEndpoint().getName().equals(v.getName()))
					output += flow.get(e);
				else
					input += flow.get(e);
			}
			excessValue = input - output;
			if (excessValue > 0.0 && existExcess == false && !v.getName().equals("t")) {
				vExcess = v;
				existExcess = true;
			}
			excess.put(v, excessValue);
			//System.out.println("Vertex " + v.getName() + " Excess:" + excessValue);
			input = output = excessValue = 0.0;
		}
		if (existExcess == false)
			vExcess = null;
	}
	
	/**
	 * Push an edge if it is applicable. It will increase the flow of the edge 
	 * if this edge is a forward edge. It will decrease the flow of the edge 
	 * if this edge is a backward edge. 
	 * 
	 * @param e
	 * 			An edge which will be pushed
	 * @param v
	 * 			The vertex which chose with positive excess value
	 * @return	
	 * 			true 	if the edge is applicable
	 * 			false 	other wise
	 */
	private boolean push(Edge e, Vertex v) {
		boolean res = true;
		Iterator<Edge> j;
		double input = 0.0, output = 0.0, excessValue = 0.0;
		if (excess.get(v) > 0.0 && (heights.get(v.getName().toString()) > heights.get(simpleGraph.opposite(v, e).getName().toString())) 
				&& ((e.getFirstEndpoint().equals(v) && flow.get(e) < Double.parseDouble(e.getData().toString())) || (e.getSecondEndpoint().equals(v) 
						&& flow.get(e) > 0.0))) {
			if (e.getFirstEndpoint().equals(v)) {
				double delta = Math.min(excess.get(v),(Double.parseDouble(e.getData().toString()) - Double.parseDouble(flow.get(e).toString())));
				flow.put(e, flow.get(e) + delta);
				Vertex v1 = e.getFirstEndpoint();
				Vertex v2 = e.getSecondEndpoint();
				excess.put(v1, excess.get(v1) - delta);
				excess.put(v2, excess.get(v2) + delta);
			}
			else {
				double delta = Math.min(excess.get(v), flow.get(e));
				flow.put(e, flow.get(e) - delta);
				Vertex v1 = e.getFirstEndpoint();
				Vertex v2 = e.getSecondEndpoint();
				excess.put(v1, excess.get(v1) + delta);
				excess.put(v2, excess.get(v2) - delta);
			}
		}
		else {
			res = false;
		}
		return res;
	}
	
	/**
	 * Relable the height of vertex v if it is applicable
	 * 
	 *  @param v
	 *  		the vertex which will be relabled
	 */
	private void relabel(Vertex v) {
		Iterator<Edge> j;
		Edge e;
		boolean allEdgesSatisfied = true;
		if (excess.get(v) > 0 && allEdgesSatisfied) {
			heights.put(v.getName().toString(), heights.get(v.getName().toString()) + 1);
		}
	}
	
	/**
	 * This is the main method in this class. It will return the max flow 
	 * of a given graph according to the Prepush Algorithm. 
	 * 
	 *  @param sg
	 *  		the input SimpleGraph
	 *  @return 
	 *  		>=0		the max flow of the given input graph
	 *  		-1		There is no s node or t node in the graph
	 *  		-2		There exists some edges with negative capacities
	 */
    public double getMaxFlow(SimpleGraph sg) {
    	double maxFlow = 0;
    	Vertex v;
    	Edge e;
    	int resOfCheck;
    	Iterator<Edge> itIncidentEdge;
    	intializeGraph(sg);
    	resOfCheck = checkGraph();
    	if (resOfCheck == -1)
    		return -1;
    	else if (resOfCheck == -2)
    		return -2;
    	initializeHeights();
    	initializeFlow();
    	updateExcess();
    	Edge minheight = null;
    	Vertex w = null;
    	while ((v = findExcessNode()) != null) {
    		//System.out.println("Node " + v.getName() + "with excess!");
    		//excess.put(v, 0.0);
    		int height = heights.get(v.getName().toString());
    		boolean pushSuccess = false;
    		for (itIncidentEdge = simpleGraph.incidentEdges(v); itIncidentEdge.hasNext();) {
    			e = itIncidentEdge.next();
    			if (e.getFirstEndpoint().equals(v) && flow.get(e) < Double.parseDouble(e.getData().toString())) {
	    			w = e.getSecondEndpoint();
	    			if (heights.get(w.getName().toString()) < height) {
	    				minheight = e;
	    				height = heights.get(w.getName().toString());
	    				break;
	    			}
    			}
    			else if (e.getSecondEndpoint().equals(v) && flow.get(e) > 0){
    				w = e.getFirstEndpoint();
    				if (heights.get(w.getName().toString()) < height) {
	    				minheight = e;
	    				height = heights.get(w.getName().toString());
	    				break;
	    			}
    			}
    		}
    		if (minheight == null || !push(minheight,v))
    			relabel(v);
    		minheight = null;
    	}
    	for (itIncidentEdge = simpleGraph.incidentEdges(sNode); itIncidentEdge.hasNext();) {
			e = itIncidentEdge.next();
			maxFlow += flow.get(e);
    	}
    	return maxFlow;
    }
}
