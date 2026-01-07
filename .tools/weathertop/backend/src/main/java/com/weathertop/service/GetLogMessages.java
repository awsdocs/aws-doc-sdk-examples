// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.weathertop.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.S3Object;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GetLogMessages {

    private static final Region REGION = Region.US_EAST_1;
    private static final String BUCKET_NAME = "weathertop2";
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH-mm");


    /**
     * Retrieves the historical test summary for a specific SDK language from S3.
     * <p>
     * This method performs the following steps:
     * <ol>
     *   <li>Builds an {@link S3Client} for the configured {@code REGION}.</li>
     *   <li>Finds the latest JSON test result file in the bucket {@code BUCKET_NAME} for the given language using {@link #getLatestFileKey}.</li>
     *   <li>Downloads the JSON file as bytes and converts it to a UTF-8 string.</li>
     *   <li>Parses the JSON and extracts the {@code tests} array under {@code results}.</li>
     *   <li>Returns the extracted tests array as a pretty-printed JSON string. If the {@code tests} node is not an array, returns {@code "[]"}</li>
     * </ol>
     * <p>
     * Example output:
     *
     * @param language The SDK language prefix (e.g., "java", "python") used to locate the latest test results.
     * @return A pretty-printed JSON string representing the {@code tests} array for the specified language.
     * @throws RuntimeException if there is a failure reading the JSON, extracting the tests node, or any S3-related error occurs.
     */
    public String getHistoricalSummary(String language) {
        S3Client s3 = S3Client.builder()
                .region(REGION)
                .build();

        String latestFileKey = getLatestFileKey(s3, BUCKET_NAME, language);
        System.out.println("Latest file: " + latestFileKey);

        GetObjectRequest getRequest = GetObjectRequest.builder()
                .bucket(BUCKET_NAME)
                .key(latestFileKey)
                .build();

        ResponseBytes<GetObjectResponse> objectBytes = s3.getObjectAsBytes(getRequest);
        String jsonString = objectBytes.asString(StandardCharsets.UTF_8);

        try {
            JsonNode root = MAPPER.readTree(jsonString);
            JsonNode testsNode = root.path("results").path("tests");

            if (!testsNode.isArray()) {
                return "[]";
            }

            return MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(testsNode);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse JSON or extract tests node", e);
        }
    }


    /**
     * Retrieves the key of the latest timestamped JSON file for a given SDK language in an S3 bucket.
     * Example filename: {@code java-2025-07-10T12-15.json}
     * If multiple matching files exist, the one with the most recent timestamp is returned.
     *
     * @param s3Client       The {@link S3Client} used to interact with Amazon S3.
     * @param bucketName     The name of the S3 bucket to search for JSON files.
     * @param languagePrefix The prefix for the SDK language (e.g., "java", "python") used to filter files.
     * @return The key of the latest JSON file matching the specified language prefix.
     * @throws RuntimeException if no matching files are found in the bucket.
     */
    public static String getLatestFileKey(S3Client s3Client, String bucketName, String languagePrefix) {
        Pattern filePattern = Pattern.compile(languagePrefix + "-(\\d{4}-\\d{2}-\\d{2}T\\d{2}-\\d{2})\\.json");

        ListObjectsV2Request request = ListObjectsV2Request.builder()
                .bucket(bucketName)
                .prefix(languagePrefix + "-")
                .build();

        ListObjectsV2Response response = s3Client.listObjectsV2(request);
        List<S3Object> objects = response.contents();

        return objects.stream()
                .map(S3Object::key)
                .filter(key -> filePattern.matcher(key).matches())
                .sorted(Comparator.comparing((String key) -> extractTimestamp(key, filePattern)).reversed())
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No matching files found for prefix: " + languagePrefix));
    }

    private static LocalDateTime extractTimestamp(String key, Pattern pattern) {
        Matcher matcher = pattern.matcher(key);
        if (matcher.find()) {
            return LocalDateTime.parse(matcher.group(1), FORMATTER);
        } else {
            throw new RuntimeException("Invalid file format: " + key);
        }
    }
}
