package com.example.lookoutvision;

class DatasetUpdateFailedException  extends Exception  
{  
    public DatasetUpdateFailedException (String errorMessage)  
    {  
        // calling the constructor of parent Exception  
        super(errorMessage);  
    }  
} 
