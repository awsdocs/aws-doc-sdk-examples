// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.bedrockruntime.libs;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;

public class ImageTools {

    public static void displayImage(String base64ImageData) {
        byte[] imageData = Base64.getDecoder().decode(base64ImageData);
        displayImage(imageData);
    }

    public static void displayImage(byte[] imageData) {
        try {
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageData));
            JFrame frame = new JFrame("Image");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            JLabel label = new JLabel(new ImageIcon(image));
            frame.getContentPane().add(label);

            frame.pack();
            frame.setVisible(true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
