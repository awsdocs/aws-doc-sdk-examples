// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.weathertop.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HistoricalSDK {

    private static final Region REGION = Region.US_EAST_1;
    private static final String BUCKET_NAME = "weathertop2";
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final DateTimeFormatter FILENAME_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * Returns historical test summary for the latest 3 runs of the given language.
     *
     * @param language SDK language prefix (e.g., "java")
     * @return A JSON string with the historical summary in the required format
     */
    public String getHistoricalSummary(String language) {
        S3Client s3 = S3Client.builder().region(REGION).build();
        String prefix = language + "-";
        Pattern pattern = Pattern.compile(language + "-(\\d{4}-\\d{2}-\\d{2})T\\d{2}-\\d{2}\\.json");

        // List all matching files
        List<S3Object> matchedObjects = listMatchingObjects(s3, BUCKET_NAME, prefix, pattern);

        // Sort descending by date in filename
        matchedObjects.sort(Comparator.comparing(
                o -> extractDateFromKey(o.key(), pattern), Comparator.reverseOrder()));

        List<Map<String, Object>> historyList = new ArrayList<>();

        for (int i = 0; i < Math.min(3, matchedObjects.size()); i++) {
            String key = matchedObjects.get(i).key();
            String json = readS3Object(s3, BUCKET_NAME, key);

            try {
                JsonNode root = MAPPER.readTree(json);
                JsonNode summary = root.path("results").path("summary");

                int tests = summary.path("tests").asInt();
                int passed = summary.path("passed").asInt();
                double passRate = (tests == 0) ? 0.0 : (double) passed / tests * 100;

                Map<String, Object> entry = new LinkedHashMap<>();
                entry.put("date", extractDateFromKey(key, pattern).format(FILENAME_DATE_FORMATTER));
                entry.put("tests", tests);
                entry.put("passed", passed);
                entry.put("passRate", Math.round(passRate * 100.0) / 100.0); // round to 2 decimals

                historyList.add(entry);
            } catch (Exception e) {
                throw new RuntimeException("Failed to parse JSON from: " + key, e);
            }
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("language", language);
        result.put("history", historyList);

        try {
            return MAPPER.writeValueAsString(result);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize final result", e);
        }
    }

    private List<S3Object> listMatchingObjects(S3Client s3, String bucket, String prefix, Pattern pattern) {
        ListObjectsV2Request request = ListObjectsV2Request.builder()
                .bucket(bucket)
                .prefix(prefix)
                .build();

        ListObjectsV2Response response = s3.listObjectsV2(request);
        List<S3Object> matched = new ArrayList<>();

        for (S3Object obj : response.contents()) {
            if (pattern.matcher(obj.key()).matches()) {
                matched.add(obj);
            }
        }
        return matched;
    }

    private String readS3Object(S3Client s3, String bucket, String key) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        ResponseBytes<GetObjectResponse> bytes = s3.getObjectAsBytes(getObjectRequest);
        return bytes.asString(StandardCharsets.UTF_8);
    }

    private LocalDate extractDateFromKey(String key, Pattern pattern) {
        Matcher matcher = pattern.matcher(key);
        if (matcher.find()) {
            return LocalDate.parse(matcher.group(1));
        } else {
            throw new RuntimeException("Invalid key format: " + key);
        }
    }
}
