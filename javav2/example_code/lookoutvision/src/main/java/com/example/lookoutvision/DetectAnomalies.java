/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

// snippet-start:[lookoutvision.java2.detect_anomalies.complete]

package com.example.lookoutvision;

import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.lookoutvision.LookoutVisionClient;
import software.amazon.awssdk.services.lookoutvision.model.Anomaly;
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
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.NumberFormat;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONObject;

// Finds anomalies on a supplied image.
public class DetectAnomalies {

    public static final Logger logger = Logger.getLogger(DetectAnomalies.class.getName());

    public static DetectAnomalyResult detectAnomalies(LookoutVisionClient lfvClient, String projectName,
            String modelVersion,
            String photo) throws IOException, LookoutVisionException {
        /**
         * Creates an Amazon Lookout for Vision dataset from a manifest file.
         * Returns after Lookout for Vision creates the dataset.
         * 
         * @param lfvClient    An Amazon Lookout for Vision client.
         * @param projectName  The name of the project in which you want to create a
         *                     dataset.
         * @param modelVersion The version of the model that you want to use.
         *
         * @param photo        The photo that you want to analyze.
         * 
         * @return DetectAnomalyResult The analysis result from DetectAnomalies.
         */

        logger.log(Level.INFO, "Processing local file: {0}", photo);

        // Get image bytes.

        InputStream sourceStream = new FileInputStream(new File(photo));
        SdkBytes imageSDKBytes = SdkBytes.fromInputStream(sourceStream);
        byte[] imageBytes = imageSDKBytes.asByteArray();

        // Get the image type. Can be image/jpeg or image/png.
        String contentType = getImageType(imageBytes);

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
            prediction = "Prediction: Anomalous";
        }

        // Convert confidence to percentage.
        NumberFormat defaultFormat = NumberFormat.getPercentInstance();
        defaultFormat.setMinimumFractionDigits(1);
        String confidence = String.format("Confidence: %s", defaultFormat.format(result.confidence()));

        // Log classification result.
        String photoPath = "File: " + photo;
        String[] imageLines = { photoPath, prediction, confidence };
        logger.log(Level.INFO, "Image: {0}\nAnomalous: {1}\nConfidence {2}", imageLines);

        return result;

    }

    // Gets the image mime type. Supported formats are image/jpeg and image/png.
    private static String getImageType(byte[] image) throws IOException {

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

    public static boolean rejectOnClassification(String image, DetectAnomalyResult prediction, float minConfidence) {
        /**
         * Rejects an image based on its anomaly classification and prediction
         * confidence
         * 
         * @param image         The file name of the analyzed image.
         * @param prediction    The prediction for an image analyzed with
         *                      DetectAnomalies.
         * @param minConfidence The minimum acceptable confidence for the prediction
         *                      (0-1).
         * 
         * @return boolean True if the image is anomalous, otherwise False.
         */

        Boolean reject = false;

        logger.log(Level.INFO, "Checking classification for {0}", image);

        String[] logParameters = { prediction.confidence().toString(), String.valueOf(minConfidence) };

        if (Boolean.TRUE.equals(prediction.isAnomalous()) && prediction.confidence() >= minConfidence) {
            logger.log(Level.INFO, "Rejected: Anomaly confidence {0} is greater than confidence limit {1}",
                    logParameters);
            reject = true;
        }
        if (Boolean.FALSE.equals(reject))
            logger.log(Level.INFO, ": No anomalies found.");

        return reject;

    }

    public static Boolean rejectOnCoverage(String image, DetectAnomalyResult prediction, float minConfidence,
            String anomalyLabel, float maxCoverage) {
        /**
         * Rejects an image based on a maximum allowable coverage area for an anomaly
         * type.
         * 
         * @param image         The file name of the analyzed image.
         * @param prediction    The prediction for an image analyzed with
         *                      DetectAnomalies.
         * @param minConfidence The minimum acceptable confidence for the prediction
         *                      (0-1).
         * @param anomalyLabel  The anomaly type (label) to check.
         * @param maxCoverage   The maximum allowable coverage area of the anomaly type.
         *                      (0-1).
         * 
         * @return boolean True if the coverage area of the anomaly type exceeds the
         *         maximum allowed, otherwise False.
         */

        Boolean reject = false;

        logger.log(Level.INFO, "Checking coverage for {0}", image);

        if (Boolean.TRUE.equals(prediction.isAnomalous()) && prediction.confidence() >= minConfidence) {
            for (Anomaly anomaly : prediction.anomalies()) {

                if (Objects.equals(anomaly.name(), anomalyLabel)
                        && anomaly.pixelAnomaly().totalPercentageArea() >= maxCoverage) {

                    String[] logParameters = { prediction.confidence().toString(),
                            String.valueOf(minConfidence),
                            String.valueOf(anomaly.pixelAnomaly().totalPercentageArea()),
                            String.valueOf(maxCoverage) };
                    logger.log(Level.INFO,
                            "Rejected: Anomaly confidence {0} is greater than confidence limit {1} and " +
                                    "{2} anomaly type coverage is higher than coverage limit {3}\n",
                            logParameters);
                    reject = true;

                }
            }
        }

        if (Boolean.FALSE.equals(reject))
            logger.log(Level.INFO, ": No anomalies found.");

        return reject;
    }

    public static Boolean rejectOnAnomalyTypeCount(String image, DetectAnomalyResult prediction,
            float minConfidence, Integer maxAnomalyLabels) {

        /**
         * Rejects an image based on a maximum allowable number of anomaly types.
         *
         * @param image           The file name of the analyzed image.
         * @param prediction      The prediction for an image analyzed with
         *                        DetectAnomalies.
         * @param minConfidence   The minimum acceptable confidence for the predictio
         *                        (0-1).
         * @param maxAnomalyLabels The maximum allowable number of anomaly labels (types).
         * 
         * @return boolean True if the image contains more than the maximum allowed
         *         anomaly types, otherwise False.
         */

        Boolean reject = false;

        logger.log(Level.INFO, "Checking coverage for {0}", image);

        Set<String> defectTypes = new HashSet<>();

        if (Boolean.TRUE.equals(prediction.isAnomalous()) && prediction.confidence() >= minConfidence) {
            for (Anomaly anomaly : prediction.anomalies()) {
                defectTypes.add(anomaly.name());
            }
            // Reduce defect types by one to account for 'background' anomaly type.
            if ((defectTypes.size() - 1) > maxAnomalyLabels) {
                String[] logParameters = { prediction.confidence().toString(),
                        String.valueOf(minConfidence),
                        String.valueOf(defectTypes.size()),
                        String.valueOf(maxAnomalyLabels) };
                logger.log(Level.INFO, "Rejected: Anomaly confidence {0} is >= minimum confidence {1} and " +
                        "the number of anomaly types {2} > the allowable number of anomaly types {3}\n", logParameters);
                reject = true;
            }

        }

        if (Boolean.FALSE.equals(reject))
            logger.log(Level.INFO, ": No anomalies found.");

        return reject;
    }

    public static void analyzeImage(LookoutVisionClient lfvClient, String image, String projectName,
            String modelVersion,
            Float confidenceLimit, Float coverageLimit, int anomalyLabelsLimit, String anomalyLabel)
            throws IOException, LookoutVisionException {

        List<String> anomalies = new ArrayList<>();

        Boolean reject = false;

        System.out.println(String.format("Analyzing image: %s", image));

        DetectAnomalyResult prediction = DetectAnomalies.detectAnomalies(lfvClient, projectName, modelVersion, image);

        reject = DetectAnomalies.rejectOnClassification(image, prediction, confidenceLimit);

        if (Boolean.TRUE.equals(reject)) {
            anomalies.add("Classification: An anomaly was found.");
        }

        reject = DetectAnomalies.rejectOnCoverage(image, prediction, confidenceLimit, anomalyLabel, coverageLimit);

        if (Boolean.TRUE.equals(reject)) {
            anomalies.add("Coverage: Anomaly coverage too high.");
        }

        reject = DetectAnomalies.rejectOnAnomalyTypeCount(image, prediction, confidenceLimit, anomalyLabelsLimit);

        if (Boolean.TRUE.equals(reject)) {
            anomalies.add("Anomaly type count: Too many anomaly types found.");
        }

        if (anomalies.isEmpty()) {
            System.out.println(String.format("No anomalies found in %s.", image));
        } else {
            System.out.println(String.format("Anomalies found in %s", image));
            anomalies.forEach(System.out::println);

        }

    }

    public static void main(String[] args) {

        String image = null;
        String configFile = null;
        String projectName = null;
        String modelVersion = null;
        Float confidenceLimit = null;
        Float coverageLimit = null;
        Integer anomalyLabelsLimit = null;
        String anomalyType = null;

        final String USAGE = "\n" +
                "Usage:\n" +
                "    DetectAnomalies <image> <config> \n\n" +
                "Where:\n" +
                "    image - The image file to analyze.\n\n" +
                "    config - The configuration JSON file to use. See resources/analysis-config.json\n\n";
               

        try {

            if (args.length != 2) {
                System.out.println(USAGE);
                System.exit(1);
            }

            image = args[0];
            configFile = args[1];

            // Get the configuration information.
            JSONObject config = new JSONObject(new String(Files.readAllBytes(Paths.get(configFile))));
            projectName = config.getString("project");
            modelVersion = config.getString("model_version");
            confidenceLimit = config.getFloat("confidence_limit");
            coverageLimit = config.getFloat("coverage_limit");
            anomalyLabelsLimit = config.getInt("anomaly_labels_limit");
            anomalyType = config.getString("anomaly_label");


            // Get the Lookout for Vision client.
            LookoutVisionClient lfvClient = LookoutVisionClient.builder().build();

            DetectAnomalies.analyzeImage(lfvClient, image, projectName, modelVersion, confidenceLimit, coverageLimit,
                    anomalyLabelsLimit, anomalyType);

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
