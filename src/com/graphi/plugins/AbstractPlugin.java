//=========================================
//  Kyle Russell
//  AUT University 2015
//  https://github.com/denkers/graphi
//=========================================

package com.graphi.plugins;

import com.graphi.app.AppManager;
import com.graphi.display.layout.MainPanel;
import com.graphi.util.GraphData;
import java.io.Serializable;

public abstract class AbstractPlugin implements Plugin, Serializable
{
    protected final String name;
    protected final String description;
    protected MainPanel panel;
    
    public AbstractPlugin(String name, String description)
    {
        this.name           =   name;
        this.description    =   description;
    }
    
    @Override
    public abstract void attachPanel(AppManager appManager);
    
    @Override
    public String getPluginName() 
    {
        return name;
    }

    @Override
    public String getPluginDescription()
    {
        return description;
    }

    @Override
    public MainPanel getPanel() 
    {
        return panel;
    }
    
    @Override
    public void passData(GraphData data)
    {
        panel.setGraphData(data);
    }
    
    @Override
    public GraphData getData()
    {
        return panel.getGraphData();
    }
    
    @Override
    public boolean equals(Object other)
    {
        if(other instanceof AbstractPlugin)
        {
            AbstractPlugin otherPlugin   =   (AbstractPlugin) other;
            return this.getPluginName().equalsIgnoreCase(otherPlugin.getPluginName());
        }
        
        else return false;
    }
    
    @Override
    public int hashCode()
    {
        return name.hashCode();
    }
}
