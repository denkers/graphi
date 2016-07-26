package com.graphi.tasks;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MappedProperty 
{
    private String name;
    private Map<String, String> params;

    public MappedProperty()
    {
        name    =   "";
        params  =   new LinkedHashMap<>();
    }
    
    public MappedProperty(String propertyStr)
    {
        this();
        initParams(propertyStr);
    }

    private void initParams(String propertyStr)
    {
        Matcher matcher         =   Pattern.compile("(@\\w+\\s?=\\s?\\w+)").matcher(propertyStr);
        Matcher nameMatcher     =   Pattern.compile("(\\w+)").matcher(propertyStr);
        name                    =   nameMatcher.find()? nameMatcher.group() : "";
        
        while(matcher.find())
        {
            System.out.println("found group");
            String paramGroup       =   matcher.group();
            Matcher pNameMatcher    =   Pattern.compile("@(\\w+)").matcher(paramGroup);
            if(pNameMatcher.find())
            {
                
                String paramName        =   pNameMatcher.group(1);
                Matcher pValueMatcher   =   Pattern.compile("=\\s?(\\w+)").matcher(paramGroup);
                if(pValueMatcher.find())
                {
                    String paramValue      =   pValueMatcher.group(1);
                    params.put(paramName, paramValue);
                }
            }
            
        }
    }
    
    public Map<String, String> getParams()
    {
        return params;
    }
    
    public String getName()
    {
        return name;
    }
        
    @Override
    public String toString()
    {
        String propertyStr  =   "";
        String paramStr     =   "";
        propertyStr += name + "(";
        
        for(Entry<String, String> param : params.entrySet())
            paramStr += "@" + param.getKey() + "=" + param.getValue() + ", ";
        
        if(paramStr.length() > 1) paramStr = paramStr.substring(0, paramStr.length() - 2);
        propertyStr += paramStr + ")";
        
        return propertyStr;
    }
}