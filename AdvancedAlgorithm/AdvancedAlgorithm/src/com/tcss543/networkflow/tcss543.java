/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.tcss543.networkflow;

import com.tcss543.graphcode.GraphInput;
import com.tcss543.graphcode.SimpleGraph;

/**
 * To run this program, two arguments should be given. 
 * The first one is the name of the algorithm that will 
 * be run("FFA", "SFFA" or "PPA"). 
 * The second one is the full path of the data set file 
 * which will be input. This argument is optional. If it 
 * is blank, it will load the default data set. 
 */
public class tcss543 {
	public static void main(String args[]) {
		if (args.length <= 0 || args.length > 2) {
			System.out.println("Illegal input arguments");
			return;
		}
		SimpleGraph G1;
		G1 = new SimpleGraph();
		if (args.length == 1) {
			if (args[0].equals("FFA")) {
				GraphInput.LoadSimpleGraph(G1, "./src/com/tcss543/input/random/eg5.txt");
				FordFulkerson ff = new FordFulkerson();
				long startTime = System.nanoTime();
				double maxflow = ff.getMaxFlow(G1, "s", "t");
				long endTime = System.nanoTime();
				System.out.println("Ford Fulkerson took: " + (endTime - startTime) / 100000 + " ms");
				System.out.println("Ford-Fulkerson Maximum Flow: " + maxflow);
			}
			else if (args[0].equals("SFFA")) {
				
			}
			else if (args[0].equals("PPA")) {
				GraphInput.LoadSimpleGraph(G1, "./src/com/tcss543/input/random/eg5.txt");
				PrePush pp = new PrePush();
				long startTime = System.nanoTime();
				double maxflow = pp.getMaxFlow(G1);
				long endTime = System.nanoTime();
				if (maxflow == -1) {
					System.out.println("Error: There is no s node or t node in this graph");
					return;
				}
				else if (maxflow == -2) {
					System.out.println("Error: There are some edges with negative capacities");
					return;
				}
				else {
					System.out.println("Prepush Algorithm took: " + (endTime - startTime) / 100000 + " ms");
					System.out.println("Prepush Maximum Flow: " + maxflow);
				}
			}
			else {
				System.out.println("Illegal name of algorithm, please use \"FFA\", \"SFFA\" or \"PPA\"");
				return;
			}
		}
		else {
			if (args[0].equals("FFA")) {
				GraphInput.LoadSimpleGraph(G1, args[1]);
				FordFulkerson ff = new FordFulkerson();
				long startTime = System.nanoTime();
				double maxflow = ff.getMaxFlow(G1, "s", "t");
				long endTime = System.nanoTime();
				System.out.println("Ford Fulkerson took: " + (endTime - startTime) / 100000 + " ms");
				System.out.println("Ford-Fulkerson Maximum Flow: " + maxflow);
			}
			else if (args[0].equals("SFFA")) {
				
			}
			else if (args[0].equals("PPA")) {
				GraphInput.LoadSimpleGraph(G1, args[1]);
				PrePush pp = new PrePush();
				long startTime = System.nanoTime();
				double maxflow = pp.getMaxFlow(G1);
				long endTime = System.nanoTime();
				if (maxflow == -1) {
					System.out.println("Error: There is no s node or t node in this graph");
					return;
				}
				else if (maxflow == -2) {
					System.out.println("Error: There are some edges with negative capacities");
					return;
				}
				else {
					System.out.println("Prepush Algorithm took: " + (endTime - startTime) / 100000 + " ms");
					System.out.println("Prepush Maximum Flow: " + maxflow);
				}
			}
			else {
				System.out.println("Illegal name of algorithm, please use \"FFA\", \"SFFA\" or \"PPA\"");
				return;
			}	
		}
	}

}
