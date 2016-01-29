package com.graphi.plugins.st;

public class StalkingUtils 
{
    public static final int COMPUTE_P1      =   0;
    public static final int COMPUTE_P2      =   1;
    public static final int COMPUTE_P3      =   2;
    public static final int COMPUTE_P4      =   3;
    
    private static void setDPoint(Object obj, Stalking stInstance, int point, boolean stalking)
    {
        StalkingConfig config   =   stInstance.getConfig();
        
        for(int time = 1; time < config.getTime(); time++)
        {
            int[][] post            =   stInstance.generatePost(time);
            int[] copyTime          =   { 1, 1, 1, 0, 0 };
            int[][] retrieve        =   stInstance.generateRetrieve(post);
            float[][] weight;
            float[][] vector;

            if(!stalking)
            {
                weight      =   stInstance.getWeight(post, retrieve);
                vector      =   stInstance.createNewMatrix1(weight);
            }

            else
            {
                retrieve    =   stInstance.generateStalkingRetrieve(post, copyTime, retrieve);   
                weight      =   stInstance.getWeight(post, retrieve);
                vector      =   stInstance.createNewMatrix1(weight);
            }
            
            switch(point)
            {
                case COMPUTE_P1: ((float[][][]) obj)[time - 1]  =   stInstance.getDpoint_1(vector); break;
                    
                case COMPUTE_P2: ((float[][][]) obj)[time - 1]  =   stInstance.getDpoint_2(vector); break;
                    
                case COMPUTE_P3: ((float[][]) obj)[time - 1]    =   stInstance.getDpoint_3(vector); break;
                    
                case COMPUTE_P4: ((float[][][]) obj)[time - 1]  =   stInstance.getDpoint_4(vector); break;
            }
        }
    }
    
    public static Object getGPoint(int point, boolean stalking, Stalking stInstance)
    {
        Object dPoint, gPoint;
        StalkingConfig config   =   stInstance.getConfig();
        
        if(point == COMPUTE_P3)
        {
            dPoint      =   new float[config.getTime()][config.getNumNodes()];
            gPoint      =   new float[config.getTime()][config.getNumNodes()][config.getNumNodes()];
        }
            
        else
            dPoint  =   gPoint  =   new float[config.getTime()][config.getNumNodes()][config.getNumNodes()];
        
        setDPoint(dPoint, stInstance, point, stalking);
        
        switch(point)
        {
            case COMPUTE_P1:    gPoint  =   stInstance.getG_dPoint((float[][][]) gPoint, (float[][][]) dPoint); break;
                
            case COMPUTE_P2:    gPoint  =   stInstance.getG_dPoint((float[][][]) gPoint, (float[][][]) dPoint); break;
                
            case COMPUTE_P3:    gPoint  =   stInstance.getG_2((float[][][]) gPoint, (float[][]) dPoint); break;
                
            case COMPUTE_P4:    gPoint  =   stInstance.getG_1((float[][][]) gPoint, (float[][][]) dPoint); break;
        }
        
        return gPoint;
    }
}
