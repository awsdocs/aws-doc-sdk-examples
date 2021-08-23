/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
package com.amazonaws.personalize.client.datasets;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import software.amazon.awssdk.utils.IoUtils;

import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.waiters.WaiterResponse;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketResponse;
import software.amazon.awssdk.services.s3.model.NoSuchBucketException;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.waiters.S3Waiter;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

public class MovieLensDatasetProvider implements DatasetProvider {

    private static final String MOVIE_LENS_URL = "http://files.grouplens.org/datasets/movielens/ml-100k.zip";

    private static final int BUFFER_SIZE = 4096;

    public String getSchema(DatasetType type) throws IOException {
        return IoUtils.toUtf8String(new FileInputStream("movie-lens-ds/schemas/interactions-schema.json"));
    }

    public Map<String, String> getItemIdToNameMapping() throws IOException {
        downloadMovieLensDataset();
        CSVReader reader = new CSVReader(new BufferedReader(new InputStreamReader(new FileInputStream("./movie-lens-ds/ml-100k/u.item"))), '|');
        Map<String, String> map = new HashMap<String, String>();
        String[] row = null;
        while ((row = reader.readNext()) != null) {
            map.put(row[0], row[1]);
        }
        return map;
    }

    public void exportDatasetToS3(DatasetType type, S3Client s3Client, String bucketName, boolean skipIfAlreadyExists) throws IOException {

        // Check if bucket exists. If not exist create bucket, skip getting by bytes.
        if (skipIfAlreadyExists && !checkBucketExists(s3Client, bucketName)) {
            createBucket(s3Client, bucketName);
        } else if (skipIfAlreadyExists && getObjectBytes(s3Client, bucketName, getS3Path(type))) {
            return;
        }
        downloadMovieLensDataset();
        uploadMovieLensDatasetToS3(s3Client, bucketName, type, getS3Path(type));

    }

    public static void downloadMovieLensDataset() throws IOException {
        HttpURLConnection conn = (HttpURLConnection) new URL(MOVIE_LENS_URL).openConnection();
        unzip(conn.getInputStream(), "./movie-lens-ds/");
    }

    public String getS3Path(DatasetType type) {
        return "movie-lens/" + type.toString() + "/" + type + ".csv";
    }

    public static void uploadMovieLensDatasetToS3(S3Client s3Client, String bucket, DatasetType type, String path) throws IOException {
        CSVReader reader = new CSVReader(new BufferedReader(new InputStreamReader(new FileInputStream("./movie-lens-ds/ml-100k/u.data"))), '\t');
        StringWriter sw = new StringWriter();
        CSVWriter pw = new CSVWriter(sw);
        pw.writeNext(new String[]{"USER_ID", "ITEM_ID", "TIMESTAMP"});
        String[] row = null;
        while ((row = reader.readNext()) != null) {
            int rating = Integer.parseInt(row[2]);
            if (rating > 3) {
                pw.writeNext(new String[]{row[0], row[1], row[3]});
            }
        }
        reader.close();
        pw.close();
        String data = sw.toString();

        try {
            PutObjectRequest objectRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(path)
                    .build();

            s3Client.putObject(objectRequest, RequestBody.fromBytes(data.getBytes()));
        } catch (S3Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }

    }


    private static ByteBuffer getRandomByteBuffer(int size) throws IOException {
        byte[] b = new byte[size];
        new Random().nextBytes(b);
        return ByteBuffer.wrap(b);
    }

    public static boolean checkBucketExists(S3Client s3Client, String bucketName) {
        HeadBucketRequest headBucketRequest = HeadBucketRequest.builder()
                .bucket(bucketName)
                .build();

        try {
            s3Client.headBucket(headBucketRequest);
            return true;

        } catch (S3Exception ex) {
            if (ex.statusCode() == 403 || ex.statusCode() == 400) {
                System.out.println(ex.getMessage());
            }
            else if (ex.statusCode() == 404){
                System.out.println("This bucket doesn't exist, creating bucket...");
                return false;
            }
        }
        return false;
    }

    // Create a bucket by using a S3Waiter object
    public static void createBucket(S3Client s3Client, String bucketName) {

        try {
            S3Waiter s3Waiter = s3Client.waiter();
            CreateBucketRequest bucketRequest = CreateBucketRequest.builder()
                    .bucket(bucketName)
                    .build();

            s3Client.createBucket(bucketRequest);
            HeadBucketRequest bucketRequestWait = HeadBucketRequest.builder()
                    .bucket(bucketName)
                    .build();


            // Wait until the bucket is created and print out the response
            WaiterResponse<HeadBucketResponse> waiterResponse = s3Waiter.waitUntilBucketExists(bucketRequestWait);
            waiterResponse.matched().response().ifPresent(System.out::println);
            System.out.println(bucketName + " is ready");

        } catch (S3Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }

    //Checks to see if the dataset is already uploaded to s3.
    public static boolean getObjectBytes(S3Client s3Client, String bucketName, String keyName) {

        try {
            GetObjectRequest objectRequest = GetObjectRequest
                    .builder()
                    .key(keyName)
                    .bucket(bucketName)
                    .build();

            ResponseBytes<GetObjectResponse> objectBytes = s3Client.getObjectAsBytes(objectRequest);
            byte[] data = objectBytes.asByteArray();
            return data.length > 0;
        } catch (NoSuchKeyException | NoSuchBucketException ex) {
            return false;
        } catch (S3Exception s3Exception) {
            System.err.println(s3Exception.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return false;
    }


    private static void unzip(InputStream is, String destDirectory) throws IOException {
        File destDir = new File(destDirectory);
        if (!destDir.exists()) {
            destDir.mkdir();
        }
        ZipInputStream zipIn = new ZipInputStream(is);
        ZipEntry entry = zipIn.getNextEntry();
        // iterates over entries in the zip file
        while (entry != null) {
            String filePath = destDirectory + File.separator + entry.getName();
            if (!entry.isDirectory()) {
                // if the entry is a file, extracts it
                extractFile(zipIn, filePath);
            } else {
                // if the entry is a directory, make the directory
                File dir = new File(filePath);
                dir.mkdirs();
            }
            zipIn.closeEntry();
            entry = zipIn.getNextEntry();
        }
        zipIn.close();
    }

    private static void extractFile(ZipInputStream zipIn, String filePath) throws IOException {
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
        byte[] bytesIn = new byte[BUFFER_SIZE];
        int read = 0;
        while ((read = zipIn.read(bytesIn)) != -1) {
            bos.write(bytesIn, 0, read);
        }
        bos.close();
    }

    public static void main(String[] args) throws IOException {
        DatasetProvider dp = new MovieLensDatasetProvider();
        System.out.println(dp.getItemIdToNameMapping());
    }

}
