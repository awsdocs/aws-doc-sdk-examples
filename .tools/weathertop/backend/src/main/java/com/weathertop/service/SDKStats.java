// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.weathertop.service;

import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.*;

public class SDKStats {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH-mm");

    /**
     * Retrieves the latest test result summaries for a set of SDK languages from an S3 bucket.
     * <p>
     * For each language in the provided array:
     * <ul>
     *   <li>Finds the most recent JSON test result file in the S3 bucket based on a timestamped filename pattern (e.g., {@code java-2025-07-10T12-15.json}).</li>
     *   <li>Parses the file to extract total tests and passed tests.</li>
     *   <li>Calculates the pass rate as {@code (passed / tests) * 100} and rounds to two decimal places.</li>
     *   <li>Builds a JSON response summarizing the test statistics for each language.</li>
     * </ul>
     *
     * The final returned JSON has the structure:
     * <pre>
     * {
     *   "summary": [
     *     {
     *       "language": "java",
     *       "tests": 429,
     *       "passRate": 98.37
     *     },
     *     {
     *       "language": "kotlin",
     *       "tests": 287,
     *       "passRate": 97.56
     *     }
     *     ...
     *   ]
     * }
     * </pre>
     *
     * @param languages An array of SDK language prefixes (e.g., {@code ["java", "kotlin", "php"]}) used to locate result files in S3.
     * @return A JSON string containing test result summaries for each language, including total tests and calculated pass rate.
     * @throws JsonProcessingException If parsing or generating JSON fails.
     */
    public String getCoverageSummary(String[] languages) throws JsonProcessingException {
        Region region = Region.US_EAST_1;
        S3Client s3Client = S3Client.builder().region(region).build();
        String bucketName = "weathertop2";
        ObjectMapper mapper = new ObjectMapper();

        List<Map<String, Object>> summaryList = new ArrayList<>();

        for (String lang : languages) {
            String prefix = lang + "-";
            Pattern timestampPattern = Pattern.compile(lang + "-(\\d{4}-\\d{2}-\\d{2}T\\d{2}-\\d{2})\\.json");

            String latestKey = getLatestJsonKey(s3Client, bucketName, prefix, timestampPattern);
            if (latestKey == null) {
                System.out.println(" No file found for language: " + lang);
                continue;
            }

            // Download and parse.
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(latestKey)
                    .build();

            ResponseBytes<GetObjectResponse> objectBytes = s3Client.getObjectAsBytes(getObjectRequest);
            String jsonContent = objectBytes.asString(StandardCharsets.UTF_8);

            JsonNode root = mapper.readTree(jsonContent);
            JsonNode summary = root.path("results").path("summary");

            int tests = summary.path("tests").asInt();
            int passed = summary.path("passed").asInt();
            int failed = summary.path("failed").asInt();

            if (tests == 0) continue; // Avoid divide by zero

            double passRate = ((double) passed / tests) * 100;

            Map<String, Object> langStats = new HashMap<>();
            langStats.put("language", lang);
            langStats.put("tests", tests);
            langStats.put("passed", passed);   // ✅ use actual passed count
            langStats.put("failed", failed);   // ✅ add failed count
            langStats.put("passRate", Math.round(passRate * 100.0) / 100.0); // round to 2 decimals
            summaryList.add(langStats);
        }

        Map<String, Object> finalResult = new HashMap<>();
        finalResult.put("summary", summaryList);
        return mapper.writeValueAsString(finalResult);
    }

    /**
     * Retrieves the key of the most recent timestamped JSON file in the specified S3 bucket.
     * <p>
     * This method lists objects in the given S3 bucket that match the specified prefix and timestamp pattern
     * (e.g., {@code java-2025-07-10T12-15.json}). It filters the list using the provided regex {@link Pattern},
     * extracts the timestamp from each matching key, and returns the one with the latest (most recent) timestamp.
     *
     * @param s3Client The {@link S3Client} used to interact with Amazon S3.
     * @param bucket   The name of the S3 bucket containing the result files.
     * @param prefix   The prefix used to filter the keys (e.g., {@code "java-"}).
     * @param pattern  The regular expression {@link Pattern} to match and extract the timestamp from object keys.
     * @return The key of the most recent matching JSON file, or {@code null} if no valid file is found.
     */
    private String getLatestJsonKey(S3Client s3Client, String bucket, String prefix, Pattern pattern) {
        ListObjectsV2Request request = ListObjectsV2Request.builder()
                .bucket(bucket)
                .prefix(prefix)
                .build();

        ListObjectsV2Response response = s3Client.listObjectsV2(request);
        return response.contents().stream()
                .map(S3Object::key)
                .filter(key -> pattern.matcher(key).matches())
                .max(Comparator.comparing(key -> extractTimestamp(key, pattern)))
                .orElse(null);
    }

    /**
     * Extracts a {@link LocalDateTime} timestamp from an S3 object key using the provided regex pattern.
     * <p>
     * The key must match the format defined by the pattern, typically something like:
     * {@code language-yyyy-MM-ddTHH-mm.json} (e.g., {@code java-2025-07-10T12-15.json}).
     * The method captures the timestamp portion, parses it using the predefined formatter, and returns it
     * as a {@code LocalDateTime} object.
     *
     * @param key     The S3 object key string (e.g., {@code java-2025-07-10T12-15.json}).
     * @param pattern A {@link Pattern} containing a capturing group that extracts the timestamp portion of the key.
     * @return The extracted {@link LocalDateTime} from the key.
     * @throws RuntimeException if the key does not match the expected pattern.
     */
    private LocalDateTime extractTimestamp(String key, Pattern pattern) {
        Matcher matcher = pattern.matcher(key);
        if (matcher.matches()) {
            return LocalDateTime.parse(matcher.group(1), FORMATTER);
        }
        throw new RuntimeException("Invalid timestamp format in key: " + key);
    }

    /**
     * Retrieves a list of services that have no tests for each specified SDK language.
     * <p>
     * This method scans the S3 bucket for the latest JSON test result file for each language
     * (based on timestamped filenames like {@code java-2025-07-10T12-15.json}). It reads
     * the {@code no_tests} section of each JSON and returns a JSON string that maps each
     * SDK language to the list of services with no tests.
     * </p>
     *
     * <p>Example output:</p>
     * <pre>
     * {
     *   "dotnet": ["bedrock-runtime", "cognito"],
     *   "java": ["s3", "lambda"],
     *   "javascript": []
     * }
     * </pre>
     *
     * <p>If no JSON file is found for a given language, the method will return an empty array
     * for that language.</p>
     *
     * @param languages An array of SDK language prefixes (e.g., {@code ["java", "dotnet", "javascript"]})
     *                  used to locate the result files in S3.
     * @return A JSON string mapping each SDK language to an array of service names that have no tests.
     * @throws JsonProcessingException If there is an error parsing or generating JSON.
     */
    public String getNoTestsBySDK(String[] languages) throws JsonProcessingException {
        Region region = Region.US_EAST_1;
        S3Client s3Client = S3Client.builder().region(region).build();
        String bucketName = "weathertop2";
        ObjectMapper mapper = new ObjectMapper();

        Map<String, List<String>> sdkNoTestsMap = new HashMap<>();

        for (String lang : languages) {
            String prefix = lang + "-";
            Pattern timestampPattern = Pattern.compile(lang + "-(\\d{4}-\\d{2}-\\d{2}T\\d{2}-\\d{2})\\.json");

            String latestKey = getLatestJsonKey(s3Client, bucketName, prefix, timestampPattern);
            if (latestKey == null) {
                System.out.println("No file found for language: " + lang);
                sdkNoTestsMap.put(lang, Collections.emptyList());
                continue;
            }

            // Download and parse
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(latestKey)
                    .build();

            ResponseBytes<GetObjectResponse> objectBytes = s3Client.getObjectAsBytes(getObjectRequest);
            String jsonContent = objectBytes.asString(StandardCharsets.UTF_8);

            JsonNode root = mapper.readTree(jsonContent);
            JsonNode noTestsNode = root.path("results").path("no_tests");

            List<String> noTestsList = new ArrayList<>();
            if (noTestsNode.isArray()) {
                for (JsonNode node : noTestsNode) {
                    noTestsList.add(node.asText());
                }
            }

            sdkNoTestsMap.put(lang, noTestsList);
        }

        return mapper.writeValueAsString(sdkNoTestsMap);
    }
}
