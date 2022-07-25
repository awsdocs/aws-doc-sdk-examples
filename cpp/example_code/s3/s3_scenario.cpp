//snippet-sourcedescription:[S3Scenario.cpp demonstrates how to perform various Amazon Simple Storage Service (Amazon S3) operations.]
//snippet-keyword:[AWS SDK for C++]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon S3]
//snippet-sourcetype:[full-example]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

// snippet-start:[s3.cpp.s3_scenario.inc]
#include <iostream>
#include <aws/core/Aws.h>
#include <aws/s3/S3Client.h>
#include <aws/s3/model/CreateBucketRequest.h>
#include <aws/s3/model/DeleteBucketRequest.h>
#include <aws/s3/model/BucketLocationConstraint.h>
#include <aws/core/utils/UUID.h>
#include <aws/core/utils/StringUtils.h>
#include "include/awsdoc/s3/s3_examples.h"

// snippet-end:[s3.cpp.s3_scenario.inc]

// snippet-start:[s3.cpp.s3_scenario.main]
/**
 * Before running this C++ code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/AmazonS3/latest/userguide/GetStartedWithS3.html
 */



int main(int argc, char* argv[])  {

    if (argc != 6) {
         std::cout << "Usage:\n" <<
              "    <bucketName> <key> <objectPath> <savePath> <toBucket>\n\n" <<
              "Where:\n" <<
              "    bucketName - The Amazon S3 bucket to create.\n\n" <<
              "    key - The key to use.\n\n" <<
              "    objectPath - The path where the file is located (for example, C:/AWS/book2.pdf). " <<
              "    savePath - The path where the file is saved after it's downloaded (for example, C:/AWS/book2.pdf). " <<
              "    toBucket - An Amazon S3 bucket to where an object is copied to (for example, C:/AWS/book2.pdf). ";
         return 1;
    }

    Aws::String bucketName = argv[1];
    Aws::String key = argv[2];
    Aws::String objectPath = argv[3];
    Aws::String savePath = argv[4];
    Aws::String toBucket = argv[5] ;

    Aws::SDKOptions options;
    InitAPI(options);

    Aws::Client::ClientConfiguration clientConfig;
    if (clientConfig.region.empty()) {
        std::cout << "An AWS region must be specified to run this scenario.\n" << std::endl;
        return 0;
    }
    AwsDoc::S3::S3Scenario(bucketName, key, objectPath, savePath, toBucket, clientConfig);

    ShutdownAPI(options);

    return 0;
#if 0
        ProfileCredentialsProvider credentialsProvider = ProfileCredentialsProvider.create();
        Region region = Region.US_EAST_1;
        S3Client s3 = S3Client.builder()
                .region(region)
                .credentialsProvider(credentialsProvider)
                .build();

        // Create an Amazon S3 bucket.
        createBucket(s3, bucketName);

        // Update a local file to the Amazon S3 bucket.
        uploadLocalFile(s3, bucketName, key, objectPath);

        // Download the object to another local file.
        getObjectBytes (s3, bucketName, key, savePath);

        // Perform a multipart upload.
        String multipartKey = "multiPartKey";
        multipartUpload(s3, toBucket, multipartKey);

        // List all objects located in the Amazon S3 bucket.
        // Show 2 ways
        listAllObjects(s3, bucketName);
        anotherListExample(s3, bucketName) ;

        // Copy the object to another Amazon S3 bucket
        copyBucketObject (s3, bucketName, key, toBucket);

        // Delete the object from the Amazon S3 bucket.
        deleteObjectFromBucket(s3, bucketName, key);

        // Delete the Amazon S3 bucket
        deleteBucket(s3, bucketName);
        System.out.println("All Amazon S3 operations were successfully performed");
        s3.close();
#endif
}

AWSDOC_S3_API bool AwsDoc::S3::S3Scenario(const Aws::String& bucketName,
                              const Aws::String& key, const Aws::String& objectPath,
                              const Aws::String& savePath, const Aws::String& toBucket,
                              const Aws::Client::ClientConfiguration &clientConfig) {

    Aws::S3::S3Client client(clientConfig);

    // Create an Amazon S3 bucket.
    {
        Aws::S3::Model::CreateBucketRequest request;
        request.SetBucket(bucketName);

        Aws::S3::Model::CreateBucketOutcome outcome = client.CreateBucket(request);

        if (!outcome.IsSuccess()) {
            Aws::S3::S3Error err = outcome.GetError();
            std::cerr << "Error: CreateBucket: " <<
                      err.GetExceptionName() << ": " << err.GetMessage() << std::endl;
            return false;
        } else {
            std::cout << "Created bucket " << bucketName <<
                      " in the specified AWS Region." << std::endl;
        }
    }

    // Delete the Amazon S3 bucket
    {
        Aws::S3::Model::DeleteBucketRequest request;
        request.SetBucket(bucketName);

        Aws::S3::Model::DeleteBucketOutcome outcome =
                client.DeleteBucket(request);

        if (!outcome.IsSuccess())
        {
            Aws::S3::S3Error err = outcome.GetError();
            std::cerr << "Error: DeleteBucket: " <<
                      err.GetExceptionName() << ": " << err.GetMessage() << std::endl;
            return false;
        }
        else
        {
            std::cout << "The bucket was deleted" << std::endl;
        }
    }

    return true;
}

#if 0
    // Create a bucket by using a S3Waiter object
    public static void createBucket( S3Client s3Client, String bucketName) {

        try {
            S3Waiter s3Waiter = s3Client.waiter();
            CreateBucketRequest bucketRequest = CreateBucketRequest.builder()
                    .bucket(bucketName)
                    .build();

            s3Client.createBucket(bucketRequest);
            HeadBucketRequest bucketRequestWait = HeadBucketRequest.builder()
                    .bucket(bucketName)
                    .build();

            // Wait until the bucket is created and print out the response.
            WaiterResponse<HeadBucketResponse> waiterResponse = s3Waiter.waitUntilBucketExists(bucketRequestWait);
            waiterResponse.matched().response().ifPresent(System.out::println);
            System.out.println(bucketName +" is ready");

        } catch (S3Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }


        return 0;
    }
#endif
#if 0
    public static void deleteBucket(S3Client client, String bucket) {
        DeleteBucketRequest deleteBucketRequest = DeleteBucketRequest.builder()
                .bucket(bucket)
                .build();
        client.deleteBucket(deleteBucketRequest);

        System.out.println(bucket +" was deleted.");
    }
#endif
    /**
     * Upload an object in parts
     */
#if 0
    private static void multipartUpload(S3Client s3, String bucketName, String key) {

        int mB = 1024 * 1024;
        // First create a multipart upload and get the upload id
        CreateMultipartUploadRequest createMultipartUploadRequest = CreateMultipartUploadRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        CreateMultipartUploadResponse response = s3.createMultipartUpload(createMultipartUploadRequest);
        String uploadId = response.uploadId();
        System.out.println(uploadId);

        // Upload all the different parts of the object
        UploadPartRequest uploadPartRequest1 = UploadPartRequest.builder()
                .bucket(bucketName)
                .key(key)
                .uploadId(uploadId)
                .partNumber(1).build();

        String etag1 = s3.uploadPart(uploadPartRequest1, RequestBody.fromByteBuffer(getRandomByteBuffer(5 * mB))).eTag();

        CompletedPart part1 = CompletedPart.builder().partNumber(1).eTag(etag1).build();

        UploadPartRequest uploadPartRequest2 = UploadPartRequest.builder().bucket(bucketName).key(key)
                .uploadId(uploadId)
                .partNumber(2).build();
        String etag2 = s3.uploadPart(uploadPartRequest2, RequestBody.fromByteBuffer(getRandomByteBuffer(3 * mB))).eTag();
        CompletedPart part2 = CompletedPart.builder().partNumber(2).eTag(etag2).build();


        // Finally call completeMultipartUpload operation to tell S3 to merge all uploaded
        // parts and finish the multipart operation.
        CompletedMultipartUpload completedMultipartUpload = CompletedMultipartUpload.builder()
                .parts(part1, part2)
                .build();

        CompleteMultipartUploadRequest completeMultipartUploadRequest =
                CompleteMultipartUploadRequest.builder()
                        .bucket(bucketName)
                        .key(key)
                        .uploadId(uploadId)
                        .multipartUpload(completedMultipartUpload)
                        .build();

        s3.completeMultipartUpload(completeMultipartUploadRequest);

    }
#endif

#if 0
    private static ByteBuffer getRandomByteBuffer(int size) {
        byte[] b = new byte[size];
        new Random().nextBytes(b);
        return ByteBuffer.wrap(b);
    }
#endif

    // Return a byte array
#if 0
    private static byte[] getObjectFile(String filePath) {

        FileInputStream fileInputStream = null;
        byte[] bytesArray = null;

        try {
            File file = new File(filePath);
            bytesArray = new byte[(int) file.length()];
            fileInputStream = new FileInputStream(file);
            fileInputStream.read(bytesArray);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return bytesArray;
    }
#endif
#if 0
    public static void getObjectBytes (S3Client s3, String bucketName, String keyName, String path ) {

        try {

            GetObjectRequest objectRequest = GetObjectRequest
                    .builder()
                    .key(keyName)
                    .bucket(bucketName)
                    .build();

            ResponseBytes<GetObjectResponse> objectBytes = s3.getObjectAsBytes(objectRequest);
            byte[] data = objectBytes.asByteArray();

            // Write the data to a local file
            File myFile = new File(path );
            OutputStream os = new FileOutputStream(myFile);
            os.write(data);
            System.out.println("Successfully obtained bytes from an S3 object");
            os.close();

        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (S3Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
#endif
#if 0
    public static void uploadLocalFile(S3Client s3, String bucketName, String key, String objectPath) {

        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        s3.putObject(objectRequest,
                RequestBody.fromBytes(getObjectFile(objectPath)));
    }
#endif
#if 0
    public static void listAllObjects(S3Client s3, String bucketName) {

        ListObjectsV2Request listObjectsReqManual = ListObjectsV2Request.builder()
                .bucket(bucketName)
                .maxKeys(1)
                .build();

        boolean done = false;
        while (!done) {
            ListObjectsV2Response listObjResponse = s3.listObjectsV2(listObjectsReqManual);
            for (S3Object content : listObjResponse.contents()) {
                System.out.println(content.key());
            }

            if (listObjResponse.nextContinuationToken() == null) {
                done = true;
            }

            listObjectsReqManual = listObjectsReqManual.toBuilder()
                    .continuationToken(listObjResponse.nextContinuationToken())
                    .build();
        }
    }
#endif
#if 0
    public static void anotherListExample(S3Client s3, String bucketName) {

       ListObjectsV2Request listReq = ListObjectsV2Request.builder()
                .bucket(bucketName)
                .maxKeys(1)
                .build();

        ListObjectsV2Iterable listRes = s3.listObjectsV2Paginator(listReq);

        // Process response pages
        listRes.stream()
                .flatMap(r -> r.contents().stream())
                .forEach(content -> System.out.println(" Key: " + content.key() + " size = " + content.size()));

        // Helper method to work with paginated collection of items directly
        listRes.contents().stream()
                .forEach(content -> System.out.println(" Key: " + content.key() + " size = " + content.size()));

        for (S3Object content : listRes.contents()) {
            System.out.println(" Key: " + content.key() + " size = " + content.size());
        }
    }
#endif
#if 0
    public static void deleteObjectFromBucket(S3Client s3, String bucketName, String key) {

        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        s3.deleteObject(deleteObjectRequest);
        System.out.println(key +" was deleted");
    }
#endif
#if 0
    public static String copyBucketObject (S3Client s3, String fromBucket, String objectKey, String toBucket) {

        String encodedUrl = null;
        try {
            encodedUrl = URLEncoder.encode(fromBucket + "/" + objectKey, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            System.out.println("URL could not be encoded: " + e.getMessage());
        }
        CopyObjectRequest copyReq = CopyObjectRequest.builder()
                .copySource(encodedUrl)
                .destinationBucket(toBucket)
                .destinationKey(objectKey)
                .build();

        try {
            CopyObjectResponse copyRes = s3.copyObject(copyReq);
            System.out.println("The "+ objectKey +" was copied to "+toBucket);
            return copyRes.copyObjectResult().toString();
        } catch (S3Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return "";
    }
}
#endif

// snippet-end:[s3.java2.s3_scenario.main]

