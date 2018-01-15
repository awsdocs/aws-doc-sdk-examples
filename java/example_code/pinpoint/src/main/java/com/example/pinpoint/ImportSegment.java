package com.example.pinpoint;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.pinpoint.AmazonPinpoint;
import com.amazonaws.services.pinpoint.AmazonPinpointClientBuilder;
import com.amazonaws.services.pinpoint.model.CreateImportJobRequest;
import com.amazonaws.services.pinpoint.model.CreateImportJobResult;
import com.amazonaws.services.pinpoint.model.Format;
import com.amazonaws.services.pinpoint.model.ImportJobRequest;
import com.amazonaws.services.pinpoint.model.ImportJobResponse;


public class ImportSegment {
	public static void main(String[] args) {
		final String USAGE = "\n" +
                "CreateSegment - create a segment \n\n" +
                "Usage: CreateApp <appId>\n\n" +
                "Where:\n" +
                "  appId - the ID the application to create a segment for.\n\n";

        if (args.length < 1) {
            System.out.println(USAGE);
            System.exit(1);
        }
        String appId = args[0];
        String bucket = args[1];
        String key = args[2];
        String roleArn = args[3];
        
		AmazonPinpoint pinpoint = AmazonPinpointClientBuilder.standard().withRegion(Regions.US_EAST_1).build();
		    			
		ImportJobResponse result = createImportSegment(pinpoint, appId, bucket, key, roleArn);
    	System.out.println("Import job for " + bucket + " submitted.");
    	System.out.println("See application " + result.getApplicationId() + " for import job status.");
	}
	
	public static ImportJobResponse createImportSegment(AmazonPinpoint client, 
			String appId,
            String bucket, 
            String key,
            String roleArn) {
		
		// Create the job.
		ImportJobRequest importRequest = new ImportJobRequest()
				.withDefineSegment(true)
				.withRegisterEndpoints(true)
				.withRoleArn(roleArn)
				.withFormat(Format.JSON)
				.withS3Url("s3://" + bucket + "/" + key);

		CreateImportJobRequest jobRequest = new CreateImportJobRequest()
				.withImportJobRequest(importRequest)
				.withApplicationId(appId);

		CreateImportJobResult jobResponse = client.createImportJob(jobRequest);

		return jobResponse.getImportJobResponse();
		}
	}
