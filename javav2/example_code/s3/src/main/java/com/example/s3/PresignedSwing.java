//snippet-sourcedescription:[PresignedSwing.java demonstrates how to a presigned object using a Java Swing app.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon S3]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[12/15/2021]
//snippet-sourceauthor:[scmacdon-aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.s3;

import javax.swing.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

class Swing implements ActionListener {
    JFrame frame=new JFrame();
    JButton button=new JButton("Get Presigned Amazon S3 Object");

    Swing(){
        prepareGUI();
        buttonProperties();
    }

    public void prepareGUI(){
        frame.setTitle("My Window");
        frame.getContentPane().setLayout(null);
        frame.setVisible(true);
        frame.setBounds(200,200,400,400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    public void buttonProperties(){
        button.setBounds(130,200,200,100);
        frame.add(button);
        button.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        //Get a presigned PDF from an Amazon S3 bucket.
        try {
            URL url  =  new URL("https://bucketmay995.s3.amazonaws.com/people.png?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20220303T125432Z&X-Amz-SignedHeaders=host&X-Amz-Expires=3600&X-Amz-Credential=AKIA33JWY3BXUOZ37473%2F20220303%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Signature=58c20bbe2b8fc3bffb7a73caf5c214cf32a6b9f7a10cac678fe381af1c77715f") ;
            InputStream in = null;
            in = url.openStream();
            FileOutputStream fos = new FileOutputStream(new File("C:\\AWS\\allpeople.png"));
            System.out.println("reading from resource and writing to file...");
            int length = -1;
            byte[] buffer = new byte[1024];// buffer for portion of data from connection
            while ((length = in.read(buffer)) > -1) {
                fos.write(buffer, 0, length);
            }
            fos.close();
            in.close();
            System.out.println("File downloaded");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}

public class PresignedSwing {
    public static void main(String[] args)
    {
        new Swing();
    }
}