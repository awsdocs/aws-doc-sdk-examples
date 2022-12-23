package com.example.cloudfront;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.core.internal.waiters.ResponseOrException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectResponse;
import software.amazon.awssdk.services.s3.model.HeadBucketResponse;
import software.amazon.awssdk.services.s3.model.ObjectOwnership;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.waiters.S3Waiter;
import software.amazon.awssdk.services.s3control.S3ControlClient;
import software.amazon.awssdk.services.sts.StsClient;

import java.io.File;

public class S3TestUtils {
    private static final Logger logger = LoggerFactory.getLogger(S3TestUtils.class);
    public static void createBucket(S3Client s3Client, String bucketName) throws Throwable {
        s3Client.createBucket(b -> b.bucket(bucketName));

        try (S3Waiter waiter = S3Waiter.create()) {
            ResponseOrException<HeadBucketResponse> responseOrException = waiter.waitUntilBucketExists(b -> b.bucket(bucketName).build()).matched();
            HeadBucketResponse response = responseOrException.response().orElseThrow(() -> new RuntimeException("Bucket Not Created"));
            logger.info("[{}] bucket created", bucketName);
        }
    }

    public static void lockdownBucket(S3Client s3Client, String bucketName) {
        // account-level, control plane stuff
        S3ControlClient s3ControlClient = S3ControlClient.create();
        s3ControlClient.putPublicAccessBlock(builder -> builder
                .accountId(getAccountId())
                .publicAccessBlockConfiguration(builder1 -> builder1
                        .blockPublicAcls(true)
                        .blockPublicPolicy(true)
                        .ignorePublicAcls(true)
                        .restrictPublicBuckets(true))
                .build());
        logger.info("public access blocked for bucket [{}]", bucketName);

        s3Client.putBucketOwnershipControls(builder -> builder.bucket(bucketName)
                .ownershipControls(ocb -> ocb
                        .rules(ocrb -> ocrb
                                .objectOwnership(ObjectOwnership.BUCKET_OWNER_ENFORCED)
                        ).build()
                ).build()
        );
        logger.info("object owner enforced for [{}]", bucketName);
    }

    public static void uploadFileToBucket(S3Client s3Client, final String bucketName, final String fileNameToUpload) {
        String fileName = CloudFrontSigningTest.class.getClassLoader().getResource(fileNameToUpload).getFile();

        PutObjectResponse response = s3Client.putObject(builder -> builder
                .bucket(bucketName).key(fileNameToUpload), RequestBody
                .fromFile(new File(fileName)));
        if (response.sdkHttpResponse().isSuccessful()) {
            logger.info("[{}] uploaded to [{}] bucket", fileName, bucketName);
        }
    }

    public static String getAccountId() {
        StsClient stsClient = StsClient.builder().credentialsProvider(ProfileCredentialsProvider.create()).build();
        return stsClient.getCallerIdentity().account();
    }

    public static void deleteObjectFromBucket(S3Client s3Client, String bucketName, String fileNameToUpload) {
        DeleteObjectResponse deleteObjectResponse = s3Client.deleteObject(builder -> builder.bucket(bucketName).key(fileNameToUpload));
        logger.info(fileNameToUpload + " deleted from " + bucketName);
    }

    public static void deleteBucket(S3Client s3Client, String bucketName) {
        s3Client.deleteBucket(b -> b.bucket(bucketName));
        logger.info(bucketName + " bucket deleted");
    }
}
