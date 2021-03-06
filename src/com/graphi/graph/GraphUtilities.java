//=========================================
//  Kyle Russell
//  AUT University 2015
//  https://github.com/denkers/graphi
//=========================================

package com.graphi.graph;

import edu.uci.ics.jung.algorithms.cluster.EdgeBetweennessClusterer;
import edu.uci.ics.jung.algorithms.filters.Filter;
import edu.uci.ics.jung.algorithms.filters.KNeighborhoodFilter;
import edu.uci.ics.jung.algorithms.filters.KNeighborhoodFilter.EdgeType;
import edu.uci.ics.jung.algorithms.layout.AggregateLayout;
import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseMultigraph;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.Set;
import javax.swing.JOptionPane;

public class GraphUtilities 
{
    public static void groupCluster(AggregateLayout layout, Set<Node> cluster)
    {
        Point2D center              =   layout.transform(cluster.iterator().next());
        Graph<Node, Edge> sGraph    =   SparseMultigraph.<Node, Edge>getFactory().create();

        for(Node node : cluster)
            sGraph.addVertex(node);

        Layout subLayout =   new CircleLayout(sGraph);
        subLayout.setSize(new Dimension(50, 50));
        layout.put(subLayout, center);
    }
    
    public static void cluster(AggregateLayout layout, Graph graph, int numRemoved, boolean group)
    {
        if(numRemoved > graph.getEdgeCount())
        {
            JOptionPane.showMessageDialog(null, "[Error] Insufficient edges");
            return;
        }
        
        EdgeBetweennessClusterer clusterer = new EdgeBetweennessClusterer(numRemoved);
        Set<Set<Node>> clusterSet  = clusterer.transform(graph); 

        Color[] colors = { Color.GREEN, Color.RED, Color.YELLOW, Color.BLACK, Color.MAGENTA, Color.BLUE, Color.GRAY };
        int colorIndex =   0;
        
        layout.reset();
        
        if(clusterSet.size() > 1)
        {
            for(Set<Node> set : clusterSet)
            {
                if(group) groupCluster((AggregateLayout) layout, set);
                
                Color setColor = colors[++colorIndex % colors.length];

                for(Node node : set)
                    node.setFill(setColor);
            } 
        } 
    }
    
    public static Graph<Node, Edge> copyNewGraph(Graph<Node, Edge> source, boolean copyState)
    {
        Graph<Node, Edge> dest  =   new SparseMultigraph<>();
        copyGraph(source, dest, copyState);
        return dest;
    }
    
    public static void copyGraph(Graph<Node, Edge> source, Graph<Node, Edge> dest, boolean copyState)
    {
        Collection<Node> nodes  =   source.getVertices();
        for(Node node : nodes)
            dest.addVertex(copyState? node.copyGraphObject() : node);
        
        Collection<Edge> edges  =   source.getEdges();
        for(Edge edge : edges)
            dest.addEdge(copyState? edge.copyGraphObject() : edge, source.getIncidentVertices(edge), source.getEdgeType(edge));
    }
    
    public static int degreeSum(Graph<Node, Edge> graph)
    {
        Collection<Node> vertices   =   graph.getVertices();
        int sum =  0;
        
        for(Node node : vertices)
            sum += graph.degree(node);
        
        return sum;
    }
    
    public static Graph<Node, Edge> getNeighbourhood(Graph<Node, Edge> graph, Node node, int dist)
    {
        Filter<Node, Edge> filter   =   new KNeighborhoodFilter<>(node, dist, EdgeType.IN_OUT);
        return filter.transform(graph);
    }
}
