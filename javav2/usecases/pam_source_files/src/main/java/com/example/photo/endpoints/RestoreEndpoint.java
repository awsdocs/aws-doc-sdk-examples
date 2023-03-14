package com.example.photo.endpoints;

import com.example.photo.Job;
import com.example.photo.PhotoApplicationResources;
import com.example.photo.services.DynamoDBService;
import com.example.photo.services.S3Service;
import com.example.photo.services.SnsService;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RestoreEndpoint {
    final DynamoDBService dbService;
    final S3Service s3Service;
    final SnsService snsService;

    public RestoreEndpoint(DynamoDBService dynamoDBService, S3Service s3Service, SnsService snsService) {
        this.dbService = dynamoDBService;
        this.s3Service = s3Service;
        this.snsService = snsService;
    }

    public Job restore(String notify, List<String> tags) {
        Set<String> images = tags.stream().parallel().flatMap(this::imagesByTag).collect(Collectors.toSet());
        String manifest = makeManifest(PhotoApplicationResources.STORAGE_BUCKET, images);
        String manifestArn = this.s3Service.putManifest(manifest);

        // These three values are required for createJob() to work: etag. accountID, roleARN!
        String eTag = this.s3Service.getETag(manifestArn, PhotoApplicationResources.WORKING_BUCKET);

        // This needs to be pulled in from CDK!
        String accountID = "924034042979";  // REPLACE WITH ACCOUNT ID

        // CDK should create this too.
        String roleARN = "arn:aws:iam::924034042979:role/pamRole";  // Replace with valid IAM ROLE
        String jobId = this.s3Service.startRestore(manifestArn,tags, eTag, accountID, roleARN);
        String topicArn = this.snsService.createNotificationTopic(notify, jobId);
        Job job = new Job();
        job.setJobId(jobId);
        job.setTopicArn(topicArn);

        this.dbService.putSubscription(job);
        return job;
    }

    private Stream<String> imagesByTag(String tag) {
        return this.dbService.getImagesTag(tag).stream();
        // return Stream.of(tag + "1.jpg", tag+"2.jpg"); // For testing
    }

    private String makeManifest(String bucket, Collection<String> objects) {
        return objects.stream().map(object -> bucket + "," + object).collect(Collectors.joining("\n"));
    }
}
