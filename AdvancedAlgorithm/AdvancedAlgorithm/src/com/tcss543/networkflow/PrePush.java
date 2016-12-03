package com.tcss543.networkflow;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.tcss543.graphcode.Edge;
import com.tcss543.graphcode.SimpleGraph;
import com.tcss543.graphcode.Vertex;

public class PrePush {
	
	private HashMap<String, Integer> heights = new HashMap<>();
	private HashMap<Edge, Double> flow = new HashMap<>();
	private HashMap<Vertex, Double> excess = new HashMap<>();
	private SimpleGraph simpleGraph;
	
	public void intializeGraph(SimpleGraph sg) {
		simpleGraph = sg;
	}
	
	public void initializeHeights() {
		Iterator<Vertex> i;
		Vertex v;
		for (i = simpleGraph.vertices(); i.hasNext();) {
			v = (Vertex) i.next();
			System.out.println("Vertex " + v.getName());
			if(v.getName().equals("s")) {
				heights.put(v.getName().toString(), simpleGraph.numVertices());
			}
			else {
				heights.put(v.getName().toString(), 0);
			}
		}
	}
	
	public void initializeFlow() {
		Iterator<Edge> i;
		Edge e;
		for (i = simpleGraph.edges(); i.hasNext();) {
			e = (Edge) i.next();
			if(e.getFirstEndpoint().getName().equals("s")) {
				flow.put(e, Double.parseDouble(e.getData().toString()));
				System.out.println("Edge " + e.getFirstEndpoint().getName() + " " + e.getSecondEndpoint().getName() + ":" + e.getData().toString());
			}
			else {
				flow.put(e, 0.0);
				System.out.println("Edge " + e.getFirstEndpoint().getName() + " " + e.getSecondEndpoint().getName() + ": 0.0");
			}
		}
	}
	
	public void showFlowStatus() {
		Iterator<Edge> i;
		Edge e;
		for (i = simpleGraph.edges(); i.hasNext();) {
			e = (Edge) i.next();
			System.out.println("Edge " + e.getFirstEndpoint().getName() + " " + e.getSecondEndpoint().getName() + ":" + flow.get(e));
		}
	}
	
	public void updateExcess() {
		Iterator<Vertex> i;
		Iterator<Edge> j;
		Vertex v;
		Edge e;
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
			excess.put(v, excessValue);
			System.out.println("Vertex " + v.getName() + " Excess:" + excessValue);
			input = output = excessValue = 0.0;
		}
	}
	
	public Vertex findExcessNode() {
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
	
	public boolean push(Edge e, Vertex v) {
		boolean res = true;
		if (excess.get(v) > 0.0 && (heights.get(v.getName().toString()) > heights.get(simpleGraph.opposite(v, e).getName().toString())) && ((e.getFirstEndpoint().equals(v) && flow.get(e) < Double.parseDouble(e.getData().toString())) || (e.getSecondEndpoint().equals(v) && flow.get(e) > 0.0))) {
			if (e.getFirstEndpoint().equals(v)) {
				double delta = Math.min(excess.get(v),(Double.parseDouble(e.getData().toString()) - Double.parseDouble(flow.get(e).toString())));
				flow.put(e, flow.get(e) + delta);
				updateExcess();
				showFlowStatus();
			}
			else {
				double delta = Math.min(excess.get(v), flow.get(e));
				flow.put(e, flow.get(e) - delta);
				updateExcess();
				showFlowStatus();
			}
		}
		else {
			res = false;
		}
		return res;
	}
	
	public void relabel(Vertex v) {
		Iterator<Edge> j;
		Edge e;
		boolean allEdgesSatisfied = true;
		for (j = simpleGraph.incidentEdges(v); j.hasNext();) {
			e = j.next();
			if ((e.getFirstEndpoint().equals(v) && (flow.get(e) < Double.parseDouble(e.getData().toString())))
					|| (e.getSecondEndpoint().equals(v) && flow.get(e) > 0)) {
				if (heights.get(v.getName()) > heights.get(simpleGraph.opposite(v, e).getName().toString())) {
					allEdgesSatisfied = false;
					break;
				}
			}
		}
		if (excess.get(v) > 0 && allEdgesSatisfied) {
			heights.put(v.getName().toString(), heights.get(v.getName().toString()) + 1);
		}
	}
	
    public double getMaxFlow(SimpleGraph sg) {
    	double maxFlow = 0;
    	Vertex v;
    	Edge e;
    	Iterator<Edge> itIncidentEdge;
    	intializeGraph(sg);
    	initializeHeights();
    	initializeFlow();
    	updateExcess();
    	while ((v = findExcessNode()) != null) {
    		System.out.println("Node " + v.getName() + "with excess!");
    		//excess.put(v, 0.0);
    		boolean pushSuccess = false;
    		for (itIncidentEdge = simpleGraph.incidentEdges(v); itIncidentEdge.hasNext();) {
    			e = itIncidentEdge.next();
    			if (push(e,v)) {
    				pushSuccess = true;
    			}
    		}
    		if (!pushSuccess) {
    			relabel(v);
    		}
    	}
    	Iterator<Edge> i;
		for (i = simpleGraph.edges(); i.hasNext();) {
			e = (Edge) i.next();
			System.out.println("Edge " + e.getFirstEndpoint().getName().toString() + " " + e.getSecondEndpoint().getName().toString() + ":" + flow.get(e));
			if(e.getFirstEndpoint().getName().equals("s")) {
				maxFlow += flow.get(e);
			}
		}
    	return maxFlow;
    }
}
