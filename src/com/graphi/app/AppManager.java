//=========================================
//  Kyle Russell
//  AUT University 2015
//  https://github.com/denkers/graphi
//=========================================

package com.graphi.app;

import com.graphi.display.Window;
import com.graphi.plugins.PluginManager;

public final class AppManager 
{
    private final ConfigManager configManager;
    private final PluginManager pluginManager;
    private final Window window;
    
    private AppManager()
    {
        window          =   Window.getWindowInstance();
        configManager   =   new ConfigManager();
        pluginManager   =   new PluginManager(window);   
    }
    
    public ConfigManager getConfigManager()
    {
        return configManager;
    }
    
    public PluginManager getPluginManager()
    {
        return pluginManager;
    }
    
    public Window getWindow()
    {
        return window;
    }
}
