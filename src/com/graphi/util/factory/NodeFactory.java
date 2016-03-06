//=========================================
//  Kyle Russell
//  AUT University 2015
//  https://github.com/denkers/graphi
//=========================================

package com.graphi.util.factory;

import com.graphi.util.Node;

public class NodeFactory extends GraphObjFactory<Node>
{
    public NodeFactory()
    {
        super();
    }
    
    public NodeFactory(int lastID)
    {
        super(lastID);
    }
    
    public NodeFactory(int lastID, int incAmount)
    {
        super(lastID, incAmount);
    }
    
    @Override
    public Node create()
    {
        lastID  +=  incAmount;
        return new Node(lastID, Integer.toHexString(lastID));
    }
}