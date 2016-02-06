//=========================================
//  Kyle Russell
//  AUT University 2015
//  https://github.com/denkers/graphi
//=========================================

package com.graphi.plugins;

import com.graphi.display.LayoutPanel;
import com.graphi.display.Window;

public class DefaultPlugin extends AbstractPlugin
{
    public DefaultPlugin() 
    {
        super("Default", "Default plugin");
    }

    @Override
    public void attachPanel(Window window) 
    {
        panel  =   new LayoutPanel(window);
    }
}
