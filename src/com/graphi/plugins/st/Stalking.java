package com.graphi.plugins.st;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

public class Stalking 
{
    private final Random RANDOM  =   new Random();
    private StalkingConfig config;
    
    public Stalking()
    {
        
    }
    
    public void main(String[] args)
    {
        PrintStream out         =   System.out;
        
        float[][][] gPoint      =   (float[][][]) StalkingUtils.getGPoint(StalkingUtils.COMPUTE_P1, false, config);
        float[][][] gPoint_2    =   (float[][][]) StalkingUtils.getGPoint(StalkingUtils.COMPUTE_P2, false, config);
        float[][][] gPoint_3    =   (float[][][]) StalkingUtils.getGPoint(StalkingUtils.COMPUTE_P3, false, config);

        writeG("cxy.txt", gPoint);
        writeG("cxycy.txt", gPoint_2);
        writeG("average.txt", gPoint_3);
        
        System.setOut(out);
        System.out.print("FINISHED!");
    }
    
    public StalkingConfig getConfig()
    {
        return config;
    }
    
    private void createConfig()
    {
        StalkingConfig cfg   =   new StalkingConfig();
        
        System.out.println("Do you want to create from txt file/ 1--yes,2--Random");
        Scanner input = new Scanner(System.in);

        cfg.setReadIO(input.nextInt() == 1);

        if (cfg.isReadIO())
        {
             cfg.setMatrix(readTxtFile(Stalking.class.getClassLoader()
                     .getResource(".")
                     .getPath()
                     + "../mMatrix.txt"));

             cfg.setNumNodes(cfg.getMatrix().length);
        } 

        else 
        {
            do 
            {
                System.out.println("The RANDOM graph model: 1 (G(n,p)), 2 (Power Law)");
                input = new Scanner(System.in);
                cfg.setModel(input.nextInt());
            } 

            while (cfg.getModel() == 0);

            System.out.println("The number of nodes:");
            input = new Scanner(System.in);
            cfg.setNumNodes(input.nextInt());

            System.out.println("The total number of messages:");
            input = new Scanner(System.in);
            cfg.setNumMessages(input.nextInt());

            System.out.println("The max number of messages of a person:");
            input = new Scanner(System.in);
            cfg.setMessageLimit(input.nextInt());


            if (cfg.getModel() == 1) 
            {
                System.out.println("The edge probability p = ");
                input = new Scanner(System.in);
                cfg.setEdgeProb(input.nextDouble());
                cfg.setMatrix(generationGraphp());
            } 

            else 
            {
                System.out.println("The fixed number of edges m = ");
                input = new Scanner(System.in);
                cfg.setNumEdges(input.nextInt());
                cfg.setMatrix(generationGraphPowerlaw());
            }

            System.out.println("Graph generated");
        }

        // Stalking
        System.out.println("who are you(only scanner the number):");
        cfg.setStalkerIndex(input.nextInt() - 1);
        System.out.println("who do you want to stalk(only input the number):");
        cfg.setStalkingIndex(input.nextInt() - 1);


        String[] POINTS = new String[cfg.getNumNodes()];
        for (int i = 0; i < cfg.getNumNodes(); i++)
                POINTS[i] = "P" + (i + 1);


        int[][] dist = new int[cfg.getNumNodes()][cfg.getNumNodes()];
        for (int i = 0; i < cfg.getNumNodes(); i++) 
                dist[i] = Dijsktra(cfg.getMatrix(), i);


        try 
        {
            System.setOut(new PrintStream(new FileOutputStream(Stalking.class
                    .getClassLoader().getResource(".").getPath()
                    + "log.txt")));
        } 

        catch (FileNotFoundException e) 
        {
            e.printStackTrace();
        } 

        System.out.println("Start!");
        System.out.println("Generate " + cfg.getNumNodes() + " nodes");
        printDegree();

        System.out.print("nodes[" + cfg.getNumNodes() + "]={");
        String[] points = new String[cfg.getNumNodes()];
        for (int pn = 0; pn < cfg.getNumNodes(); pn++) 
        {
            points[pn] = POINTS[pn];
            if (pn < cfg.getNumNodes() - 1) 
                System.out.print(points[pn] + " , ");

             else 
                System.out.print(points[pn]);
        }

        System.out.println(" }");
        printMatrix("");
    }

    //calculate the stalking index according to the time
    protected  float[][][] getG_1(float[][][] gPoint, float[][][] dPoint) 
    {
        int num = gPoint[0].length;

        for (int time = 1; time <= config.getTime(); time++)
        {
            System.out.println();
            System.out.println("Time " + time);
            
            for (int i = 0; i < num; i++)
            {
                for (int j = 0; j < num; j++)
                {
                    float sumTmp = 0;
                    for (int k = time; k > 0; k--) 
                            sumTmp += (float) dPoint[time - k][i][j] / k;
                    
                    gPoint[time - 1][i][j] = sumTmp;
                }
            }
        }
        
        return gPoint;
    }

    protected  float[][][] getG_2(float[][][] gPoint, float dPoint[][]) 
    {
        int num = gPoint[0].length;
        for (int time = 1; time <= config.getTime(); time++) 
        {
                System.out.println();
                for (int i = 0; i < num; i++) 
                    gPoint[time - 1][0][i] = dPoint[time - 1][i];
        }

        return gPoint;
    }

    protected  float[][][] getG_dPoint(float[][][] gPoint, float[][][] dPoint)
    {
        gPoint = dPoint;
        return gPoint;
    }

    //calculate the c_xy
    protected  float[][] getDpoint_1(float[][] vectorArray) 
    {
        int num     =   config.getNumNodes();
        float[][] d = new float[num][num];
        for (int i = 0; i < num; i++)
        {
            for (int j = 0; j < num; j++) 
                d[i][j] = vectorArray[i][j];
        }

        return d;
    }

    //calculate the c_xy/average c_y
    protected  float[][] getDpoint_2(float[][] vectorArray) 
    {
        int num     =   config.getNumNodes();
        float[][] d =   new float[num][num];
        float[] sum =   new float[num];
        
        for (int i = 0; i < num; i++)
        {
            for (int j = 0; j < num; j++) 
                    sum[i] += vectorArray[j][i];

            sum[i] = sum[i] / num;
        }
        
        for (int i = 0; i < num; i++)
        {
            for (int j = 0; j < num; j++) 
                    d[i][j] = vectorArray[i][j] / sum[j];
        }

        return d;
    }

    //calculate the average c_y
    protected  float[] getDpoint_3(float[][] vectorArray) 
    {
        int num     =   config.getNumNodes();
        float[] sum =   new float[num];
        for (int i = 0; i < num; i++)
        {
                for (int j = 0; j < num; j++) 
                        sum[i] += vectorArray[j][i];

                sum[i] = sum[i] / num;
        }

        return sum;
    }

    //calculate the c_xy/average c_y -c_yx/ average c_x
    protected  float[][] getDpoint_4(float[][] vectorArray) 
    {
        int num     =   config.getNumNodes();
        float[][] d =   new float[num][num];
        float[] sum =   new float[num];

        for (int i = 0; i < num; i++) 
        {
            for (int j = 0; j < num; j++) 
                    sum[i] += vectorArray[j][i];

            sum[i] = sum[i] / num;
        }

        for (int i = 0; i < num; i++)
        {
            for (int j = 0; j < num; j++) 
                    d[i][j] = vectorArray[i][j] / sum[j] - vectorArray[j][i]/ sum[i];
        }

        return d;
    }

    protected  float[][] createNewMatrix1(float[][] weight)
    {
        int num             =   config.getNumNodes();
        float r             =   config.getR();
        float[][] vectorArray = new float[num][num];
        int[] degree;
        float[] distribution;
        float[] totalWeight1;
        float[] totalWeight2;
        float[][] subGraph;

        for (int k = 0; k < num; k++) 
        {
            subGraph = new float[num][num];
            totalWeight1 = new float[num];
            totalWeight2 = new float[num];
            distribution = new float[num];
            degree = new int[num];

            for (int i = 0; i < num; i++) 
            {
                for (int j = 0; j < num; j++) 
                {
                    if (config.getMatrix()[i][j] == 1) 
                    {
                        degree[i]++;
                        totalWeight1[i] = totalWeight1[i] + weight[k][j] + 1;
                    }

                    if (config.getMatrix()[i][j] == 0) 
                            totalWeight2[i] = totalWeight2[i] + weight[k][j];
                }

                distribution[i] = degree[i] / (degree[i] + r * (num - degree[i]));
            }


            for (int i = 0; i < num; i++) 
            {
                for (int j = 0; j < num; j++)
                {
                    if (config.getMatrix()[i][j] == 0 && weight[k][j] == 0) 
                            subGraph[i][j] = 0;

                    else if (config.getMatrix()[i][j] == 0) 
                            subGraph[i][j] = (1 - distribution[i]) * weight[k][j] / totalWeight2[i];

                    else if (config.getMatrix()[i][j] == 1)
                            subGraph[i][j] = distribution[i] * (weight[k][j] + 1) / totalWeight1[i];
                }
            }
            
            if (k == 0)
             printMatrix("Attention matrix ");

            float[][] transSubGraph = transposedMatrix(subGraph);
            float[] vec = new float[num];
            
            for (int i = 0; i < num; i++)
            {
                if (i == k) 
                    vec[i] = 1;
                else 
                    vec[i] = 0;
            }
            
            for (int i = 0; i < 10; i++) 
                    vec = matrixXvector(transSubGraph, vec);
            
            if (k == 0) printVector("Attention from 1 ", vec);
            
            vectorArray[k] = vec;
        }
        
        System.out.println();
        return vectorArray;
    }

    protected  float[][] getWeight(int[][] post, int[][] retrieve)
    {
        int num =   config.getNumNodes();
        float[][] weight = new float[num][num];
        for (int i = 0; i < num; i++)
        {
            for (int j = 0; j < num; j++)
            {
                int denominator = 0;// p+r
                int numerator = 0;
                Set<Integer> weightSet = new HashSet<Integer>();
                
                if (i == j) 
                    weight[i][j] = 0;
                
                else 
                {
                    for (Integer tmp : retrieve[i]) 
                    {
                        if (tmp != 0) 
                            weightSet.add(tmp);
                    }
                    
                    for (Integer tmp : post[j]) 
                    {
                        if (weightSet.contains(tmp)) 
                                numerator++;
                        else 
                        {
                            if (tmp != 0) 
                                weightSet.add(tmp);
                        }
                    }
                    
                    denominator = weightSet.size();
                    
                    if(i==0 && j==1)
                            System.out.println("\nP1 -> P2 UnionSize = "+denominator + " IntersectionSize = "+numerator);
                    

                    if(i==0 && j==4)
                            System.out.println("P1 -> P5 UnionSize = "+denominator + " IntersectionSize = "+numerator);

                    weight[i][j] = (float) numerator / denominator;
                }
            }
        }

        return weight;
    }

    protected  int[][] generateRetrieve(int[][] post) 
    {
        System.out.println(" Retrieve :");
        int num             =   config.getNumNodes();
        int[][] retrieve    =   new int[num][config.getMessageLimit()];

        for (int i = 0; i < num; i++) 
        {
            System.out.print("Random Retrieve of P" + (i + 1) + " : ");
            List<Integer> retrieveMessageList = new ArrayList<Integer>();
            
            for (int x = 1; x <= config.getNumMessages(); x++) 
                    retrieveMessageList.add(x);
            

            for (int j = 0; j < config.getMessageLimit(); j++) 
            {
                int index = (int) (Math.random() * retrieveMessageList.size());
                Integer tmp = retrieveMessageList.get(index);
                if (!Arrays.asList(post[i]).contains(tmp)) 
                {
                        retrieve[i][j] = tmp;
                        System.out.print(retrieve[i][j] + " ");
                } 
                
                else j--;
                
                retrieveMessageList.remove(tmp);
            }
            
            System.out.println();
        }
        
        return retrieve;
    }

    protected  int[][] generateStalkingRetrieve(int[][] post, int[] copyTime, int[][] retrieveRandom) 
    {
        int[][] retrieve    =   new int[config.getMatrix().length][config.getMessageLimit()];
        int stalkedIndex        =   config.getStalkingIndex();
        int stalkingIndex       =   config.getStalkerIndex();
        
        for(int i =0; i < config.getMatrix().length; i++)
        {
            for (int j=0;j < config.getMessageLimit();j++)
                retrieve[i][j] = retrieveRandom[i][j];
            
        }
        
        int[][] dist = new int[config.getMatrix().length][config.getMatrix().length];
        for (int i = 0; i < config.getMatrix().length; i++) 
                dist[i] = Dijsktra(config.getMatrix(), i);
        
        System.out.println();
        System.out.println(" Stalking Retrieve :");

        List<List<Integer>> retrieveList = new ArrayList<>();
        for (int i = 0; i <= config.getDistNum(); i++) 
        {
                List<Integer> retrieveListItem = new ArrayList<>();
                retrieveList.add(retrieveListItem);
        }
        
        int lengthSum = 0;
        List<Integer> item;
        
        for (int i = 0; i < config.getMatrix().length; i++) 
        {
            if (dist[i][stalkedIndex] <= config.getDistNum() )
            {
                item = retrieveList.get(dist[i][stalkedIndex]);
                for (Integer tmp : post[i]) 
                {
                    if (!item.contains(tmp))
                    {
                        item.add(tmp);
                        lengthSum++;
                    }
                }
            }
        }
        
        int t = 0;
        for (List<Integer> retrieveListItem : retrieveList)
        {
            while (copyTime[t] > 0)
            {
                retrieveListItem.addAll(retrieveListItem);
                copyTime[t]--;
                t++;
            }
        }

        for (int i = 0; i < config.getMessageLimit(); i++) 
        {
            int index = (int) (Math.random() * lengthSum);
            int[] retrieveLength = getRetrieveLength(retrieveList);
            int j = 0;
            int length = 0;

            for (int lengthTmp : retrieveLength)
            {
                length += lengthTmp;
                if (index < length) break;

                j++;
            }

            List<Integer> retrieveItem = retrieveList.get(j);
            Integer retrieveItemTmp = null;

            if (j == 0 ) 
                retrieveItemTmp = retrieveItem.get(index);

            else 
                retrieveItemTmp = retrieveItem.get(index - (length - retrieveList.get(j).size()));

            System.out.print("Stalker retrieve: ");
            retrieve[stalkingIndex][i] = retrieveItemTmp;
            System.out.print(retrieve[stalkingIndex][i] + " ");
            retrieveItem.remove(retrieveItemTmp);

            while (j >= 0)
            {
                List<Integer> itemTmp = retrieveList.get(j);
                retrieveList.add(itemTmp);
                j--;
            }
        }

        return retrieve;
    }

    protected  int[] getRetrieveLength(List<List<Integer>> retrieveList) 
    {
        int[] retrieveLength = new int[retrieveList.size()];
        int i = 0;
        for (List<Integer> setTmp : retrieveList) 
        {
            retrieveLength[i] = setTmp.size();
            i++;
        }
        
        return retrieveLength;
    }

    protected  int[][] generationRetrievel(int num, int[][] post)
    {
        boolean flag;
        flag = false;
        int[][] retrieve = new int[num][config.getMessageLimit()];

        for (int i = 0; i < num; i++)
        {
            System.out.print("P" + (i + 1) + " : ");
            int limit = Math.abs(RANDOM.nextInt()) % config.getMessageLimit() + 1;

            for (int j = 0; j < config.getMessageLimit(); j++)
            {
                flag = false;
                if (j < limit)
                {
                    int msgId = Math.abs(RANDOM.nextInt()) % config.getNumMessages() + 1;

                    for (int k = 0; k < config.getMessageLimit(); k++)
                    {
                        if (post[i][k] == msgId) 
                                flag = true;
                    }

                    for (int q = 0; q < j; q++) 
                    {
                        if (retrieve[i][q] == msgId) 
                        {
                                flag = true;
                        }
                    }

                    if (flag) j--;

                    else 
                    {
                        if (isExistInPost(msgId, post)) 
                        {
                                retrieve[i][j] = msgId;
                                System.out.print(retrieve[i][j] + " ");
                        } 

                        else j--;
                    }
                } 

                else retrieve[i][j] = 0;
            }

            System.out.println();
        }

        return retrieve;
    }

    protected  int[][] generationStalkingRetrievel(int[][] post, double[] probability) 
    {
        int[][] retrieve = generationRetrievel(config.getMatrix().length, post);
        int[][] retrieveTmp;
        int stalkedIndex    =   config.getStalkingIndex();
        int stalkingIndex   =   config.getStalkerIndex();
        
        int[] dist = Dijsktra(config.getMatrix(), stalkedIndex);
        Set<Integer> retrieveSet = new HashSet<Integer>();
        
        for (int r = 0; r < post[stalkedIndex].length; r++)
        {
            double pro = Math.random();
            if (pro < probability[0]) 
                    retrieveSet.add(post[stalkedIndex][r]);
        }
        
        for (int r = 0; r < dist.length; r++) 
        {
            int distNum = config.getDistNum(); // retrieve///
            if (dist[r] <= distNum && dist[r] > 0) 
            {
                for (int p = 0; p < post[r].length; p++) 
                {
                    if (post[r][p] == 0) break;

                    double pro = Math.random();
                    if (pro < probability[dist[r]]) 
                        retrieveSet.add(post[r][p]);
                }
            }
            
            for (int i = 0; i < post.length; i++)
            {
                for (int j = 0; j < post[i].length; j++) 
                {
                    double pro = Math.random();
                    if (pro < probability[config.getDistNum() + 1]) 
                    {
                        pro = Math.random();
                        if (pro < probability[config.getDistNum() + 1]) 
                                retrieveSet.add(post[i][j]);
                    }
                }
            }
        }
        
        retrieveSet.remove(0);
        
        if (retrieve[0].length < retrieveSet.size()) 
        {
            retrieveTmp = new int[config.getMatrix().length][retrieveSet.size()];
            for (int i = 0; i < config.getMatrix().length; i++) 
            {
                for (int j = 0; j < retrieveSet.size(); j++)
                {
                    if (i < config.getMessageLimit() && j < config.getMessageLimit()) 
                        retrieveTmp[i][j] = retrieve[i][j];
                    else 
                        retrieveTmp[i][j] = 0;
                }
            }
        } 
        
        else retrieveTmp = retrieve;
        
        int index = 0;
        for (Integer tmp : retrieveSet) 
        {
                retrieveTmp[stalkingIndex][index] = tmp;
                index++;
        }
        
        return retrieveTmp;
    }

    protected  int[][] generationStalkingRetrievel2(int[][] post, double[] probability) 
    {
        int[][] mMatrix     =   config.getMatrix();
        int stalkedIndex    =   config.getStalkingIndex();
        int stalkingIndex   =   config.getStalkerIndex();
        
        int[][] retrieve = generationRetrievel(mMatrix.length, post);
        int[][] retrieveTmp;
        int[][] dist = new int[mMatrix.length][mMatrix.length];
        
        for (int i = 0; i < mMatrix.length; i++) 
            dist[i] = Dijsktra(mMatrix, i);
        
        System.out.println();
        System.out.println(" Retrieve :");
        Set<Integer> retrieveSet = new HashSet<Integer>();
        
        for (int r = 0; r < post[stalkedIndex].length; r++) 
        {
            double pro = Math.random();
            if (pro < probability[0]) 
                    retrieveSet.add(post[stalkedIndex][r]);
        }

        for (int r = 0; r < mMatrix.length; r++) 
        {
            if (dist[r][stalkedIndex] <= config.getDistNum() && dist[r][stalkedIndex] > 0) {
                for (int p = 0; p < post[r].length; p++) 
                {
                    if (post[r][p] == 0) break;
                    
                    double pro = Math.random();
                    if (pro < probability[dist[r][stalkedIndex]]) 
                            retrieveSet.add(post[r][p]);
                }
            }
            
            for (int i = 0; i < post.length; i++)
            {
                for (int j = 0; j < post[i].length; j++)
                {
                    double pro = Math.random();
                    if (pro < probability[config.getDistNum() + 1])
                    {
                        pro = Math.random();
                        if (pro < probability[config.getDistNum() + 1]) 
                            retrieveSet.add(post[i][j]);
                    }
                }
            }
        }
        
        retrieveSet.remove(0);

        if (retrieve[0].length < retrieveSet.size()) 
        {
            retrieveTmp = new int[mMatrix.length][retrieveSet.size()];
            for (int i = 0; i < mMatrix.length; i++) {
                for (int j = 0; j < retrieveSet.size(); j++) 
                {
                    if (j < config.getMessageLimit()) 
                        retrieveTmp[i][j] = retrieve[i][j];
                    
                    else 
                        retrieveTmp[i][j] = 0;
                }
            }
        } 
        
        else retrieveTmp = retrieve;
        
        int index = 0;
        for (Integer tmp : retrieveSet) 
        {
            retrieveTmp[stalkingIndex][index] = tmp;
            index++;
        }
        
        for (int i = 0; i < retrieveTmp.length; i++)
        {
            System.out.print("P" + (i + 1) + " ");

            for (int j = 0; j < retrieveTmp[i].length; j++) 
                System.out.print(retrieveTmp[i][j] + " ");
            
            System.out.println();
        }
        
        return retrieveTmp;
    }

    protected  int[][] generatePost(int time)
    {
        int num     =   config.getNumNodes();
        System.out.println(" POST :" + time);
        int[][] post = new int[num][config.getMessageLimit()];


        for (int i = 0; i < num; i++) 
        {
            System.out.print("Random Post of P" + (i + 1) + " : ");
            List<Integer> postMessageList = new ArrayList<>();
            
            for (int x = 1; x <= config.getNumMessages(); x++) 
                postMessageList.add(x);
            
            for (int j = 0; j < config.getMessageLimit(); j++) 
            {
                int index = (int) (Math.random() * postMessageList.size());
                Integer tmp = postMessageList.get(index);
                post[i][j] = tmp;
                System.out.print(post[i][j] + " ");
                postMessageList.remove(tmp);
            }
            
            System.out.println();
        }

        System.out.println();
        return post;
    }

    protected  int[][] generationPost(String[] points, int time)
    {
        int num =   config.getNumNodes();
        boolean flag;
        System.out.println(" POST :" + time);
        flag = false;
        int[][] post = new int[num][config.getMessageLimit()];
        
        for (int i = 0; i < num; i++) 
        {
            System.out.print(points[i] + " : ");
            int limit = Math.abs(RANDOM.nextInt()) % config.getMessageLimit() + 1;

            for (int j = 0; j < config.getMessageLimit(); j++) 
            {
                flag = false;
                if (j < limit)
                {
                    int msgId = Math.abs(RANDOM.nextInt()) % config.getNumMessages() + 1;
                    
                    for (int k = 0; k < j; k++) 
                    {
                        if (post[i][k] == msgId) 
                                flag = true;
                    }
                    
                    if (flag) j--;
                    
                    else 
                    {
                        post[i][j] = msgId;
                        System.out.print(post[i][j] + " ");
                    }
                } 
                
                else post[i][j] = 0;
                
            }
            System.out.println();
        }
        return post;
    }

    protected  int[][] generationGraphPowerlaw() 
    {
        int num         =   config.getNumNodes();
        int edgesToAdd  =   config.getNumEdges();
        int[][] mMatrix;
        
        System.out.println("Generate Directed Graph using the Barabasi-Albert model where edgesToAdd= " + edgesToAdd);

        // Create N nodes
        // Keep track of the degree of each node
        int indegrees[] = new int[num];
        int outdegrees[] = new int[num];

        // For each node
        // Save node in array
        List<Integer> nodesList = new ArrayList<Integer>();
        for (int x = 0; x < num; x++) 
                nodesList.add(x);

        // set the number of edges to zero
        int numEdges = 0;

        // Set up the initial complete seed network
        mMatrix = new int[num][num];

        for (int i = 0; i < num - 1; i++)
        {
            mMatrix[i][i + 1] = 1;
            outdegrees[i]++;
            indegrees[i + 1]++;
        }

        mMatrix[num - 1][0] = 1;
        outdegrees[num - 1]++;
        indegrees[0]++;
        numEdges = num;

        int added = 0;
        double degreeIgnore = 0;

        int index = 0;
        int j = 0;
        ArrayList<Integer> nodeList = new ArrayList<Integer>();

        // Add each node one at a time
        for (int i = 0; i < num; i++) 
        {
            added = 0;
            degreeIgnore = indegrees[i];

            // Add the appropriate number of edges
            for (int m = 0; m < edgesToAdd; m++) 
            {
                nodeList = new ArrayList<Integer>();

                // adding all elements to nodeList
                for (int k = 0; k < num; k++) 
                        nodeList.add(k);

                nodeList.remove(i);

                // keep a running talley of the probability
                double prob = 0;

                // Choose a RANDOM number
                double randNum = RANDOM.nextDouble();

                // Try to add this node to every existing node
                while (!nodeList.isEmpty()) 
                {
                    index = (int) (Math.random() * nodeList.size());
                    j = nodeList.get(index);
                    nodeList.remove(index);

                    // Check for an existing connection between these two nodes
                    if (mMatrix[i][j] != 1)
                    {
                        // Increment the talley by the jth node's probability
                        prob = (double) Math.pow(((double) indegrees[j]) / ((double) (numEdges) - degreeIgnore), 0.25d);

                        // If this pushes us past the the probability
                        if (randNum <= prob) 
                        {
                            // Create and edge between node i and node j
                            mMatrix[i][j] = 1;
                            
                            degreeIgnore += indegrees[j];

                            // increment the number of edges
                            added++;
                            // increment the degrees of each node
                            indegrees[j]++;
                            outdegrees[i]++;
                            
                            // Stop iterating for this probability, once we have
                            // found a single edge
                            break;
                        }
                    }
                }
            }
                
            numEdges += added;
        }

        // return the resulting network
        return mMatrix;
    }

    // print out the indegrees of all nodes
    protected  void printDegree()
    {
        int num =   config.getNumNodes();
        int[] degrees = new int[num];
        for (int i = 0; i < num; i++) 
        {
            for (int j = 0; j < num; j++)
            {
                if (config.getMatrix()[i][j] == 1)
                        degrees[j]++;
            }
        }
        
        for (int i = 0; i < num; i++) 
                System.out.print(degrees[i] + " ");
        
        System.out.print("\n");
    }

    protected  int[][] initialGraph()
    {
        int n   =   config.getNumNodes();
        int m   =   config.getNumEdges();
        int[][] mMatrix = new int[n][n];

        for (int x = 0; x < m; x++) 
        {
            for (int y = 0; y < m; y++) 
            {
                if (x != y) 
                    mMatrix[x][y] = 1;
                else
                    mMatrix[x][y] = 0;
            }
        }
        
        return mMatrix;
    }

    
    protected  int[][] generationGraphp() 
    {
        double p    =   config.getEdgeProb();
        int num     =   config.getNumNodes();
        int[][] mMatrix;
        
        System.out.println("Generate Directed Graph using the G(n,p) model where p= " + p);

        mMatrix = new int[num][num];
        for (int x = 0; x < num; x++) 
        {
            for (int y = 0; y < num; y++) 
            {
                double r = Math.random();
                if (r < p && x != y)
                    mMatrix[x][y] = 1;
                else
                    mMatrix[x][y] = 0;

                System.out.print(mMatrix[x][y] + " ");
            }

            System.out.println();
        }

        return mMatrix;
    }

    protected  void printMatrix(String name) 
    {
        System.out.println();
        System.out.println(name + " : ");
        
        int[][] matrix  =   config.getMatrix();
        for (int i = 0; i < matrix.length; i++) 
        {
            for (int j = 0; j < matrix[i].length; j++)
                    System.out.print(matrix[i][j] + " ");
            
            System.out.println();
        }
    }

    protected  void printVector(String name, float[] vector) 
    {
        System.out.println();
        System.out.println(name + " : ");
        
        for (int i = 0; i < vector.length; i++) 
                System.out.print(vector[i] + " ");
    }

    protected  float[][] transposedMatrix(float[][] matrix) 
    {
        float[][] transposedMatrix = new float[matrix[0].length][matrix.length];
        for (int i = 0; i < matrix[0].length; i++) 
        {
            for (int j = 0; j < matrix.length; j++) 
                transposedMatrix[i][j] = matrix[j][i];
        }
        
        return transposedMatrix;
    }

    protected  float[] matrixXvector(float[][] matrix, float[] vector) 
    {
        float[] c_point = new float[vector.length];
        for (int i = 0; i < vector.length; i++) 
        {
            for (int j = 0; j < vector.length; j++) 
                c_point[i] += matrix[i][j] * vector[j];
        }
        
        return c_point;
    }

    protected  void writeG(String fileName, float[][][] g)
    {
        File file = new File(Stalking.class.getClassLoader().getResource(".").getPath() + "../" + fileName);
        System.out.println("Save g.txt to:" + file.getPath());

        try(PrintStream p = new PrintStream(new FileOutputStream(file, false)))
        {
            if (!file.exists()) 
                file.createNewFile();
            
            for (int time = 0; time < g.length; time++)
            {
                p.println("line:");
                for (int i = 0; i < g[time].length; i++)
                {
                    StringBuilder sb = new StringBuilder();
                    for (int j = 0; j < g[time][i].length; j++) 
                    {
                        sb.append(g[time][i][j]);
                        if (j != g[time][i].length - 1) 
                            sb.append(" ");
                    }
                    
                    p.println(sb.toString());
                }
            }
        } 
        
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    protected  int[][] readTxtFile(String filePath)
    {
        List<String> tmpList = new ArrayList<>();
        String encoding = "UTF-8";
        File file = new File(filePath);
        
        if(file.isFile() && file.exists())
        {
            try(BufferedReader bufferedReader   =   new BufferedReader(new InputStreamReader(new FileInputStream(file), encoding)))
            {
                String lineTxt;
                while ((lineTxt = bufferedReader.readLine()) != null) 
                        tmpList.add(lineTxt);
            } 

            catch (Exception e) 
            {
                System.out.println("////////");
                e.printStackTrace();
            }
        }
        
        else
            System.out.println("////////");
        
        int[][] mMatrix = new int[tmpList.size()][tmpList.size()];
        for (int i = 0; i < tmpList.size(); i++) 
        {
            for (int j = 0; j < tmpList.size(); j++) 
                    mMatrix[i][j] = Integer.parseInt(tmpList.get(i).split(" ")[j]);
        }
        
        return mMatrix;
    }

    protected  boolean isExistInPost(Integer retrieve, int[][] post)
    {
        return true;
    }

    protected  int[] Dijsktra(int[][] mMatrix, int start) 
    {
        int INF = Integer.MAX_VALUE;
        int[][] weight = new int[mMatrix.length][mMatrix.length];
        
        for (int i = 0; i < mMatrix.length; i++)
        {
            for (int j = 0; j < mMatrix.length; j++) 
            {
                    if (mMatrix[i][j] == 0 && i != j) 
                        weight[i][j] = 2000;
                    
                    else 
                        weight[i][j] = mMatrix[i][j];
            }
        }
        
        int n = weight.length; 
        int[] shortPath = new int[n]; 
        String[] path = new String[n]; 
        
        for (int i = 0; i < n; i++) 
                path[i] = "P" + (start + 1) + "-->P" + (i + 1);
        
        int[] visited = new int[n]; 
        
        shortPath[start] = 0;
        visited[start] = 1;
        for (int count = 1; count <= n - 1; count++) 
        { 
            int k = -1; 
            int dmin = INF;
            for (int i = 0; i < n; i++)
            {
                if (visited[i] == 0 && weight[start][i] < dmin) 
                {
                    dmin = weight[start][i];
                    k = i;
                }
            }
            
            shortPath[k] = dmin;
            visited[k] = 1;
            for (int i = 0; i < n; i++) 
            {
                if (visited[i] == 0
                                && weight[start][k] + weight[k][i] < weight[start][i]) {
                        weight[start][i] = weight[start][k] + weight[k][i];
                        path[i] = path[k] + "-->P" + (i + 1);
                }
            }
        }
        
        return shortPath;
    }
}
