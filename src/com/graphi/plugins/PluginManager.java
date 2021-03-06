//=========================================
//  Kyle Russell
//  AUT University 2015
//  https://github.com/denkers/graphi
//=========================================

package com.graphi.plugins;

import com.graphi.config.PluginConfig;
import com.graphi.config.ConfigManager;
import com.graphi.display.menu.MainMenu;
import com.graphi.display.menu.PluginsMenu;
import com.graphi.display.ViewPort;
import com.graphi.graph.GraphData;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

public final class PluginManager
{
    private static PluginManager instance;
    
    //Current plugin in use
    private Plugin activePlugin;
    
    //Table of plugins
    //Key: plugin name
    //Value: plugin instance
    private Map<String, Plugin> plugins;

    private PluginManager()
    {
        plugins             =   new HashMap<>();
        MainMenu.getInstance().getPluginListMenu().initPluginMenuListener();
        initDefaultPlugin();
    }
    
    //Initializes the default in the application
    //Uses the user specified default or the base plugin
    private void initDefaultPlugin()
    {
        AbstractPlugin basePlugin       =   new DefaultPlugin();
        addPlugin(basePlugin);
        MainMenu.getInstance().getPluginListMenu().addPluginMenuItem("defaultPluginItem", new JMenuItem("Default"));
        
        Plugin defaultPlugin;
        PluginConfig config             =   ConfigManager.getInstance().getPluginConfig();
        int defaultPluginIndex          =   config.getDefaultPluginIndex();
        
        //No user specified default, use base
        if(defaultPluginIndex == 0)
        {
            defaultPlugin = basePlugin;
            addPlugin(defaultPlugin);
        }
        
        //Use user specified default plugin
        else
        {
            List<String> pluginPaths    =   config.getLoadedPluginPaths();
            String defaultPluginPath    =   pluginPaths.get(defaultPluginIndex - 1);
            defaultPlugin               =   fetchPlugin(new File(defaultPluginPath));
            
            if(defaultPlugin == null)
                defaultPlugin = basePlugin;
            
            addPlugin(defaultPlugin);
        }

        MainMenu.getInstance().getPluginListMenu().loadConfigPlugins(this);
        ViewPort.getInstance().getPluginPanel().initConfig(this);
        activatePluginObject(defaultPlugin);
    }
    
    public Plugin getPlugin(String name)
    {
        return plugins.get(name);
    }
    
    public Plugin fetchPlugin(File file)
    {
        if(file == null) return null;
        
        try
        {
            URL url                         =   file.toURI().toURL();
            URLClassLoader loader           =   new URLClassLoader(new URL[] { url }, this.getClass().getClassLoader());
            JarFile jar                     =   new JarFile(url.getFile());
            Enumeration<JarEntry> entries   =   jar.entries();

            while(entries.hasMoreElements())
            {
                JarEntry entry  =   entries.nextElement();
                String name = entry.getName();

                if(name.endsWith(".class"))
                {
                    name                        =   name.replaceAll("/", ".").replace(".class", "");
                    Class<?> loadedClass        =   loader.loadClass(name);
                    Class<?>[] loadedInterfaces =   loadedClass.getInterfaces();
                    
                    if(loadedInterfaces.length == 0)
                    {
                        Class<?> loadedSuper    =   loadedClass.getSuperclass();
                        
                        if(loadedSuper != null && loadedSuper.getName().equals(AbstractPlugin.class.getName()))
                        {
                            Plugin plugin   =   (Plugin) loadedClass.newInstance();
                            plugin.setLoader(loader);
                            return plugin;
                        }
                    }
                    
                    else
                    {
                        for(Class<?> loadedInterface : loadedInterfaces)
                        {
                            if(loadedInterface.getName().equals(Plugin.class.getName()))
                            {
                                Plugin plugin   =   (Plugin) loadedClass.newInstance();
                                plugin.setLoader(loader);
                                return plugin;
                            }
                        }
                    }
                }
            }
            
            return null;
        }

        catch(IOException | ClassNotFoundException | InstantiationException | IllegalAccessException e)
        {
            JOptionPane.showMessageDialog(null, "[Error] Failed to load plugin file");
            return null;
        }
    }
    
    public void activatePluginObject(Plugin plugin)
    {
        if(plugin == null) return;
        
        //Dectivate and clean up current plugin
        if(activePlugin != null)
        {
            activePlugin.onEvent(Plugin.ONDESTROY_DISPLAY_EVENT);
            activePlugin.onEvent(Plugin.ONDEACTIVATE_PLUGIN_EVENT);
        }
        
        //Activate and display current plugin
        activePlugin    =   plugin;
        activePlugin.onEvent(Plugin.ONACTIVATE_EVENT);
        activePlugin.onEvent(Plugin.ONSHOW_DISPLAY_EVENT);
        
        PluginsMenu pluginsMenu     =   MainMenu.getInstance().getPluginListMenu();
        String itemName             =   plugin.getPluginName().equals("Default")? "defaultPluginItem" : plugin.getPluginName();
        
        JMenuItem item              =    pluginsMenu.getPluginMenuItem(itemName);
        pluginsMenu.setActivePluginItem(item);
    }
    
    public void activatePlugin(String pluginName)
    {
        Plugin plugin   =   plugins.get(pluginName);
        
        if(plugin != null && !activePlugin.equals(plugin))
            activatePluginObject(plugin);
    }
    
    public Plugin getActivePlugin()
    {
        return activePlugin;
    }
    
    public Map<String, Plugin> getPlugins()
    {
        return plugins;
    }
    
    public void addPlugin(Plugin plugin)
    {
        if(plugin != null)
        {
            plugins.put(plugin.getPluginName(), plugin);
            plugin.onEvent(Plugin.ONLOAD_EVENT);
        }
    }
    
    public boolean hasPlugin(Plugin plugin)
    {
        if(plugin == null) return false;
        else return plugins.containsKey(plugin.getPluginName());
    }
    
    public void setPlugins(Map<String, Plugin> plugins)
    {
        this.plugins    =   plugins;
    }
    
    public URLClassLoader getActiveClassLoader()
    {
        return activePlugin.getLoader();
    }
    
    public static PluginManager createInstance()
    {
        if(instance == null) instance   =   new PluginManager();
        return instance;
    }
    
    public static PluginManager getInstance()
    {
        return instance;
    }
}
