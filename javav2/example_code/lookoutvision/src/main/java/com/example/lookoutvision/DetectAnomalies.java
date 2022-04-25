/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

// snippet-start:[lookoutvision.java2.detect_anomalies.complete]

package com.example.lookoutvision;

import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.lookoutvision.LookoutVisionClient;
import software.amazon.awssdk.services.lookoutvision.model.DetectAnomaliesRequest;
import software.amazon.awssdk.services.lookoutvision.model.DetectAnomaliesResponse;
import software.amazon.awssdk.services.lookoutvision.model.DetectAnomalyResult;
import software.amazon.awssdk.services.lookoutvision.model.LookoutVisionException;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;

import java.text.NumberFormat;
import java.awt.*;
import java.awt.font.LineMetrics;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.*;

import java.util.logging.Level;
import java.util.logging.Logger;

// Finds anomalies on a supplied image.
public class DetectAnomalies extends JPanel {

    private static final long serialVersionUID = 1L;
    private transient BufferedImage image;
    private transient Dimension dimension;
    public static final Logger logger = Logger.getLogger(DetectAnomalies.class.getName());

    // Constructor. Finds anomalies in a local image file.
    public DetectAnomalies(LookoutVisionClient lfvClient, String projectName, String modelVersion,
            String photo) throws IOException, LookoutVisionException {

        logger.log(Level.INFO, "Processing local file: {0}", photo);

        // Get image bytes and buffered image.
        InputStream sourceStream = new FileInputStream(new File(photo));
        SdkBytes imageSDKBytes = SdkBytes.fromInputStream(sourceStream);
        byte[] imageBytes = imageSDKBytes.asByteArray();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(imageSDKBytes.asByteArray());
        image = ImageIO.read(inputStream);

        // Get the image type. Can be image/jpeg or image/png.
        String contentType = getImageType(imageBytes);

        // Set the size of the window that shows the image.
        setWindowDimensions();

        // Detect anomalies in the supplied image.
        DetectAnomaliesRequest request = DetectAnomaliesRequest.builder().projectName(projectName)
                .modelVersion(modelVersion).contentType(contentType).build();

        DetectAnomaliesResponse response = lfvClient.detectAnomalies(request,
                RequestBody.fromBytes(imageBytes));

        /*
         * Tip: You can also use the following to analyze a local file.
         * Path path = Paths.get(photo);
         * DetectAnomaliesResponse response = lfvClient.detectAnomalies(request, path);
         */
        DetectAnomalyResult result = response.detectAnomalyResult();

        String prediction = "Prediction: Normal";

        if (Boolean.TRUE.equals(result.isAnomalous())) {
            prediction = "Prediction: Abnormal";
        }

        // Convert prediction to percentage.
        NumberFormat defaultFormat = NumberFormat.getPercentInstance();
        defaultFormat.setMinimumFractionDigits(1);
        String confidence = String.format("Confidence: %s", defaultFormat.format(result.confidence()));

        // Draw file name, prediction, and confidence on image.
        String photoPath = "File: " + photo;
        String[] imageLines = { photoPath, prediction, confidence };
        drawImageInfo(imageLines);

        logger.log(Level.INFO, "Image: {0}\nAnomalous: {1}\nConfidence {2}", imageLines);

    }

    // Sets window dimensions to 1/2 screen size, unless image is smaller.
    public void setWindowDimensions() {
        dimension = java.awt.Toolkit.getDefaultToolkit().getScreenSize();

        dimension.width = (int) dimension.getWidth() / 2;
        if (image.getWidth() < dimension.width) {
            dimension.width = image.getWidth();
        }
        dimension.height = (int) dimension.getHeight() / 2;

        if (image.getHeight() < dimension.height) {
            dimension.height = image.getHeight();
        }

        setPreferredSize(dimension);

    }

    // Draws the file name, prediction, and confidence on the image.
    public void drawImageInfo(String[] imageLines) {

        int indent = 10;

        // Set up drawing.
        Graphics2D g2d = image.createGraphics();
        g2d.setColor(Color.GREEN);
        g2d.setFont(new Font("Tahoma", Font.PLAIN, 80));
        Font font = g2d.getFont();
        FontMetrics metrics = g2d.getFontMetrics(font);

        int y1 = 0;

        for (int i = 0; i < imageLines.length; i++) {

            // Get text height, width, and descent.
            int textWidth = metrics.stringWidth(imageLines[i]);
            LineMetrics lm = metrics.getLineMetrics(imageLines[i], g2d);
            int textHeight = (int) lm.getHeight();
            int descent = (int) lm.getDescent();

            int y2 = (y1 + textHeight) - descent;

            // Draw black rectangle.
            g2d.setColor(Color.BLACK);
            g2d.fillRect(indent, y1, textWidth, textHeight);

            // Draw text.
            g2d.setColor(Color.GREEN);
            g2d.drawString(imageLines[i], indent, y2);

            y1 += textHeight;

        }
        g2d.dispose();

    }

    // Draws the image containing the bounding boxes and labels.
    @Override
    public void paintComponent(Graphics g) {

        Graphics2D g2d = (Graphics2D) g; // Create a Java2D version of g.

        // Draw the image.
        g2d.drawImage(image, 0, 0, dimension.width, dimension.height, this);

    }

    // Gets the image mime type. Supported formats are image/jpeg and image/png.
    private String getImageType(byte[] image) throws IOException {

        InputStream is = new BufferedInputStream(new ByteArrayInputStream(image));
        String mimeType = URLConnection.guessContentTypeFromStream(is);

        logger.log(Level.INFO, "Image type: {0}", mimeType);

        if (mimeType.equals("image/jpeg") || mimeType.equals("image/png")) {
            return mimeType;
        }
        // Not a supported file type.
        logger.log(Level.SEVERE, "Unsupported image type: {0}", mimeType);
        throw new IOException(String.format("Wrong image type. %s format isn't supported.", mimeType));
    }

    public static void main(String[] args) throws Exception {

        String photo = null;
        String projectName = null;
        String modelVersion = null;

        final String USAGE = "\n" +
                "Usage:\n" +
                "    DetectAnomalies <project> <version> <image> \n\n" +
                "Where:\n" +
                "    project - The Lookout for Vision project.\n\n" +
                "    version - The version of the model within the project.\n\n" +
                "    image - The path and filename of a local image. \n\n";

        try {

            if (args.length != 3) {
                System.out.println(USAGE);
                System.exit(1);
            }

            projectName = args[0];
            modelVersion = args[1];
            photo = args[2];
            DetectAnomalies panel = null;

            // Get the Lookout for Vision client.
            LookoutVisionClient lfvClient = LookoutVisionClient.builder().build();

            // Create frame and panel.
            JFrame frame = new JFrame("Anomaly Detection");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            panel = new DetectAnomalies(lfvClient, projectName, modelVersion, photo);

            frame.setContentPane(panel);
            frame.pack();
            frame.setVisible(true);
            // Keep window live for 10 seconds.
            Thread.sleep(10000);
            System.exit(0);

        } catch (LookoutVisionException lfvError) {
            logger.log(Level.SEVERE, "Lookout for Vision client error: {0}: {1}",
                    new Object[] { lfvError.awsErrorDetails().errorCode(),
                    lfvError.awsErrorDetails().errorMessage() });
            System.out.println(String.format("lookout for vision client error: %s", lfvError.getMessage()));
            System.exit(1);

        } catch (FileNotFoundException fileError) {
            logger.log(Level.SEVERE, "Could not find file: {0}", fileError.getMessage());
            System.out.println(String.format("Could not find file: %s", fileError.getMessage()));
            System.exit(1);

        } catch (IOException ioError) {
            logger.log(Level.SEVERE, "IO error {0}", ioError.getMessage());
            System.out.println(String.format("IO error: %s", ioError.getMessage()));
            System.exit(1);
        }

    }
}
// snippet-end:[lookoutvision.java2.detect_anomalies.complete]
