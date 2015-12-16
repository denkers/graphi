
package com.graphi.display;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class Window
{
    private final String WINDOW_TITLE   =   "Graphi - Kyle Russell 2015";
    
    private final Layout layout;
    private final MainMenu menu;
    private final JFrame frame;
    
    private Window()
    {
        initLookAndFeel();
        
        layout  =   new Layout();
        menu    =   new MainMenu();
        frame   =   new JFrame(WINDOW_TITLE);
        
        initFrame();
        initMenu();        
    }
    
    
    private void initMenu()
    {
        frame.setJMenuBar(menu);
    }
    
    private void initLayout()
    {
        
    }
    
    private void initLookAndFeel()
    {
        try
        {
            UIManager.setLookAndFeel("de.javasoft.plaf.synthetica.SyntheticaAluOxideLookAndFeel");
        } 
        
        catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e)
        {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void initFrame()
    {
        frame.getContentPane().add(layout);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
    }
    
    private void display()
    {
        frame.setVisible(true);
    }
    
    public static void main(String[] args)
    {
        Window window   =   new Window();
        window.display();
    }
}