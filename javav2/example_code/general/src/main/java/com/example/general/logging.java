//snippet-sourcedescription:[logging.java demonstrates how to ...]
//snippet-keyword:[SDK for Java 2.0]
//snippet-keyword:[Code Sample]
//snippet-service:[s3]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[]
//snippet-sourceauthor:[soo-aws]
package com.example.general;

// snippet-start:[s3.java.logging.complete]
// snippet-start:[s3.java.logging.import]
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ListBucketsRequest;
import software.amazon.awssdk.services.s3.model.ListBucketsResponse;
// snippet-end:[s3.java.logging.import]

// snippet-start:[s3.java.logging.main]
public class logging {

	private static final Logger logger = LogManager.getLogger(logging.class);

	public static void main (String[] args) {
		System.out.println("testing logging setup for " + logging.class);

		S3Client s3 = S3Client.builder().build();
		ListBucketsRequest listBucketsRequest = ListBucketsRequest.builder().build();
	    ListBucketsResponse listBucketsResponse = s3.listBuckets(listBucketsRequest);
	    listBucketsResponse.buckets().stream().forEach(x -> System.out.println(x.name()));

		logger.info("logging level info");
		logger.debug("logging debug stuff");
		logger.warn("logging warning");
		logger.error("logging error");
		logger.fatal("logging fatal");
	}
}
// snippet-end:[s3.java.logging.main]
// snippet-end:[s3.java.logging.complete]