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
        String topicArn = this.snsService.createNotificationTopic(notify);

        Set<String> images = tags.stream().parallel().flatMap(this::imagesByTag).collect(Collectors.toSet());
        String manifest = makeManifest(PhotoApplicationResources.WORKING_BUCKET, images);
        String manifestArn = this.s3Service.putManifest(manifest);
        String jobId = this.s3Service.startRestore(manifestArn, tags);

        Job job = new Job(jobId, topicArn);
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
