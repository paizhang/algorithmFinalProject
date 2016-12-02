/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.tcss543.networkflow;

import com.tcss543.graphcode.GraphInput;
import com.tcss543.graphcode.SimpleGraph;

public class tcss543 {
	public static void main(String args[]) {

		SimpleGraph G1;
		G1 = new SimpleGraph();
		if (args.length != 0) {
			GraphInput.LoadSimpleGraph(G1, args[0]);
			FordFulkerson ff = new FordFulkerson();
			long startTime = System.nanoTime();
			int maxflow = ff.getMaxFlow(G1, "s", "t");
			long endTime = System.nanoTime();
			System.out.println("Ford Fulkerson took: " + (endTime - startTime) / 100000 + " ms");
			System.out.println("Ford-Fulkerson Maximum Flow: " + maxflow);

		} else {

		}
	}

}
