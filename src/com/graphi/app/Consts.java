//=========================================
//  Kyle Russell
//  AUT University 2015
//  https://github.com/denkers/graphi
//=========================================

package com.graphi.app;

import edu.uci.ics.jung.graph.util.EdgeType;
import java.awt.Toolkit;
import java.awt.Color;
import java.io.File;

/**
 * Contains all application wide constants 
 * includes paths, settings, layout definitions, names and more
 */
public final class Consts 
{
    private Consts() {}
    
    //----------------------------------------------------------------------------------
    //  APP PATHS
    //----------------------------------------------------------------------------------
    public static final String SEPARATOR        =   File.separator;
    public static final String DATA_DIR         =   "data" + SEPARATOR;
    public static final String RES_DIR          =   DATA_DIR + "resources" + SEPARATOR;
    public static final String IMG_DIR          =   RES_DIR + "images" + SEPARATOR;
    public static final String TEST_DIR         =   DATA_DIR + "test";
    public static final String CONF_DIR         =   DATA_DIR + "conf" + SEPARATOR;
    public static final String APP_CONF_FILE    =   CONF_DIR + "app-config.xml";
    public static final String PLUGIN_CONF_FILE =   CONF_DIR + "plugin-config.xml";
    public static final String ERROR_DUMP_DIR   =   DATA_DIR + "error" + SEPARATOR;
    public static final String UPDATER_DIR      =   DATA_DIR + "updater" + SEPARATOR;
    //----------------------------------------------------------------------------------
    


    //---------------------------------------------------------------------------------------------------------------
    //  APP SETTINGS
    //---------------------------------------------------------------------------------------------------------------
    public static final int DEFAULT_PB_DELAY    =   500;
    public static final int WINDOW_HEIGHT       =   (int) (Toolkit.getDefaultToolkit().getScreenSize().height * 0.85);
    public static final int WINDOW_WIDTH        =   (int) (Toolkit.getDefaultToolkit().getScreenSize().width * 0.7);
    public static final String LNF_PACKAGE      =   "de.javasoft.plaf.synthetica.SyntheticaAluOxideLookAndFeel";
    public static final boolean DEBUG_MODE      =   false;
    //---------------------------------------------------------------------------------------------------------------
    
    
    
    //-----------------------------------------------------------------------
    //  LAYOUT CONSTS
    //-----------------------------------------------------------------------
    public static final Color TRANSPARENT_COL   =   new Color(255, 255, 255, 0);
    public static final Color PRESET_COL        =   new Color(200, 200, 200);
    public static final float MAIN_SPLIT_WG     =   0.7f;
    //-----------------------------------------------------------------------
    
    
    
    //-----------------------------------------------------------------------
    //  GRAPH DEFAULTS
    //-----------------------------------------------------------------------
    public static final EdgeType DEFAULT_EDGETYPE   =   EdgeType.UNDIRECTED;
    public static final Color DEFAULT_NODE_COLOUR   =   Color.GREEN;
    public static final Color DEFAULT_EDGE_COLOUR   =   Color.BLACK;
    public static final Color SELECTED_OBJ_COLOUR   =   Color.RED;
    //-----------------------------------------------------------------------
    
    
    
    //------------------------------------------------------------------------
    //  PANEL CARD NAMES
    //------------------------------------------------------------------------
    //MainPanel/GraphPanel@gpControlsWrapper
    public static final String RECORD_CARD            =   "rec";
    public static final String PLAYBACK_CARD          =   "pb";
    
    //MainPanel/ControlPanel@genPanel
    public static final String BA_PANEL_CARD          =   "ba_panel";
    public static final String KL_PANEL_CARD          =   "kl_panel";
    public static final String RA_PANEL_CARD          =   "ra_panel";
    
    //MainPanel/ControlPanel@computeInnerPanel
    public static final String CLUSTER_PANEL_CARD     =   "cluster_panel";
    public static final String SPATH_PANEL_CARD       =   "spath_panel";
    public static final String CENTRALITY_PANEL_CARD  =   "centrality_panel";
    
    //GraphPanel
    public static final String DISPLAY_GRAPH_CARD     =   "disp_graph";
    public static final String DISPLAY_TRANSIT_CARD   =   "disp_transit";
    //------------------------------------------------------------------------
    
    
    
    //----------------------------------------------------------------
    //  ABOUT DETAILS
    //----------------------------------------------------------------
    public static final String ABOUT_NAME        =   "Kyle Russell";
    public static final String ABOUT_UNIVERSITY  =   "AUT University";
    public static final String ABOUT_LICENSE     =   "MIT License";
    public static final String REPO_LINK         =   "Github.com/denkers/graphi";
    //----------------------------------------------------------------
}
