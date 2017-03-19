//=========================================
//  Kyle Russell
//  AUT University 2015
//  https://github.com/denkers/graphi
//=========================================

package com.graphi.display.layout;

import com.graphi.app.Consts;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class ViewPort extends JPanel
{
    public static final String TRANSITION_SCENE =   "tr_sc";
    public static final String MAIN_SCENE       =   "main_sc";
    private static ViewPort instance;
    private TransitionPanel transitionPanel;
    private JPanel mainScenePanel;
    
    private ViewPort()
    {
        setLayout(new CardLayout());
        setPreferredSize(new Dimension(Consts.WINDOW_WIDTH, Consts.WINDOW_HEIGHT));
        initTransitionPanel();
        initMainScene();
    }
    
    public void attachMainPanel(MainPanel mainPanel)
    {
        mainScenePanel.removeAll();
        mainScenePanel.add(mainPanel);
        mainScenePanel.revalidate();
    }
    
    private void initMainScene()
    {
        mainScenePanel  =   new JPanel(new BorderLayout());
        add(mainScenePanel, MAIN_SCENE);
    }
    
    private void initTransitionPanel()
    {
        transitionPanel =   new TransitionPanel();
        JLabel transitionLabel  =   transitionPanel.getTransitionLabel();
        transitionLabel.setBorder(BorderFactory.createEmptyBorder(200, 0, 0, 0));
        add(transitionPanel, TRANSITION_SCENE);
    }
    
    public void transitionScene(String sceneName)
    {
        SwingUtilities.invokeLater(()->
        {
            Timer timer =   new Timer(2500, (ActionEvent e) -> 
            {
                setScene(Consts.DISPLAY_GRAPH_CARD);
            });
            
            timer.setRepeats(false);
            timer.start();
        });
    }
    
    public void setScene(String sceneName)
    {
        CardLayout layout   =   (CardLayout) getLayout();
        layout.show(this, sceneName);
    }
    
    public static ViewPort getInstance()
    {
        if(instance == null) instance   =   new ViewPort();
        return instance;
    }
}
