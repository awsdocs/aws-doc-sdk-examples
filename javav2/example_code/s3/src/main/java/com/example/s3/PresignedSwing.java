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

import javax.swing.JFrame;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
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
            URL url = new URL("<Enter URL>") ;
            InputStream in;
            in = url.openStream();
            FileOutputStream fos = new FileOutputStream("C:\\AWS\\allpeople.png");
            System.out.println("reading from resource and writing to file...");
            int length;
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
    public static void main(String[] args) {
        new Swing();
    }
}