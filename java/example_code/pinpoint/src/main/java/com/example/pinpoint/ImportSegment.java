//snippet-sourcedescription:[ImportSegment.java demonstrates how to import a segment into Pinpoint.]
//snippet-keyword:[Java]
//snippet-keyword:[Code Sample]
//snippet-service:[mobiletargeting]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[]
//snippet-sourceauthor:[soo-aws]
/*
 * Copyright 2010-2018 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
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
                "ImportSegment - import a segment \n\n" +
                "Usage: ImportSegment <appId> <bucket> <key> <roleArn> \n\n" +
                "Where:\n" +
                "  appId - the ID the application to create a segment for.\n\n" +
                "  bucket - name of the s3 bucket that contains the segment definitons.\n\n" +
                "  key - key of the s3 object " +
                "  roleArn - ARN of the role that allows pinpoint to access S3";

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
