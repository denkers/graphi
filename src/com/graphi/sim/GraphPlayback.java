//=========================================
//  Kyle Russell
//  AUT University 2015
//  https://github.com/denkers/graphi
//=========================================

package com.graphi.sim;

import com.graphi.util.Edge;
import com.graphi.util.Node;
import edu.uci.ics.jung.algorithms.layout.AggregateLayout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

public class GraphPlayback implements Iterator<Graph<Node, Edge>>, Serializable
{
    private List<Graph<Node, Edge>> graphData;
    private VisualizationViewer<Node, Edge> viewer;
    private AggregateLayout<Node, Edge> layout;
    private int index;
    
    
    public GraphPlayback(VisualizationViewer<Node, Edge> viewer, List<Graph<Node, Edge>> graphData, AggregateLayout<Node, Edge> layout)
    {
        this.viewer     =   viewer;
        this.graphData  =   graphData;
        this.layout     =   layout;
        index           =   0;
    }
    
    public void add(Graph<Node, Edge> graph)
    {
        graphData.add(graph);
    }
    
    public void  add(Graph<Node, Edge> graph, int i)
    {
        graphData.add(i, graph);
    }
    
    @Override
    public void remove()
    {
        graphData.remove(index);
    }
    
    public void remove(int i)
    {
        graphData.remove(i);
    }
    
    @Override
    public boolean hasNext()
    {
        return index < graphData.size();
    }
    
    public boolean hasPrevious()
    {
        return index > 0;
    }
    
    @Override
    public Graph<Node, Edge> next()
    {
        if(!hasNext()) return null;
        return graphData.get(index++);
    }
    
    public Graph<Node, Edge> previous()
    {
        if(!hasPrevious()) return null;
        return graphData.get(index--);
    }
    
    public void display()
    {
        Graph<Node, Edge> current   =   graphData.get(index);
        
        if(current != null)
        {
            layout.setGraph(current);
            viewer.repaint();
        }
    }
    
    public void display(int i)
    {
        index = i;
        display();
    }
    
    public void setViewer(VisualizationViewer<Node, Edge> viewer)
    {
        this.viewer =   viewer;
    }
    
    public void setGraphData(List<Graph<Node, Edge>>  graphData)
    {
        this.graphData  =   graphData;
    }
    
    public List<Graph<Node, Edge>> getGraphData()
    {
        return graphData;
    }
    
    public VisualizationViewer<Node, Edge> getViewer()
    {
        return viewer;
    }
    
    public AggregateLayout<Node, Edge> getLayout()
    {
        return layout;
    }
    
    public void setLayout(AggregateLayout<Node, Edge> layout)
    {
        this.layout =   layout;
    }
}
