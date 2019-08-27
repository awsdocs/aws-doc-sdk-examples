// snippet-sourcedescription:[ ]
// snippet-service:[dynamodb]
// snippet-keyword:[Java]
// snippet-keyword:[Amazon DynamoDB]
// snippet-keyword:[Code Sample]
// snippet-keyword:[ ]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[ ]
// snippet-sourceauthor:[AWS]
// snippet-start:[dynamodb.java.trydax.DaxAsyncClientDemo] 

/**
 * Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * This file is licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License. A copy of
 * the License is located at
 *
 * http://aws.amazon.com/apache2.0/
 *
 * This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
*/
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import com.amazon.dax.client.dynamodbv2.ClientConfig;
import com.amazon.dax.client.dynamodbv2.ClusterDaxAsyncClient;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.handlers.AsyncHandler;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsync;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.GetItemRequest;
import com.amazonaws.services.dynamodbv2.model.GetItemResult;

public class DaxAsyncClientDemo {
    public static void main(String[] args) throws Exception {

	ClientConfig daxConfig = new ClientConfig().withCredentialsProvider(new ProfileCredentialsProvider())
		.withEndpoints("mydaxcluster.2cmrwl.clustercfg.dax.use1.cache.amazonaws.com:8111");

	AmazonDynamoDBAsync client = new ClusterDaxAsyncClient(daxConfig);

        HashMap<String, AttributeValue> key = new HashMap<String, AttributeValue>();
        key.put("Artist", new AttributeValue().withS("No One You Know"));
        key.put("SongTitle", new AttributeValue().withS("Scared of My Shadow"));

	GetItemRequest request = new GetItemRequest()
	    .withTableName("Music").withKey(key);

	// Java Futures
	Future<GetItemResult> call = client.getItemAsync(request);
	while (!call.isDone()) {
	    // Do other processing while you're waiting for the response
	    System.out.println("Doing something else for a few seconds...");
	    Thread.sleep(3000);
	}
	// The results should be ready by now
	
	try {
	    call.get();
	    
	} catch (ExecutionException ee) {
	    // Futures always wrap errors as an ExecutionException.
	    // The *real* exception is stored as the cause of the
	    // ExecutionException
	    Throwable exception = ee.getCause();
	    System.out.println("Error getting item: " + exception.getMessage());
	}

	// Async callbacks
	call = client.getItemAsync(request, new AsyncHandler<GetItemRequest, GetItemResult>() {

	    @Override
	    public void onSuccess(GetItemRequest request, GetItemResult getItemResult) {
		System.out.println("Result: " + getItemResult);
	    }
	    
	    @Override
	    public void onError(Exception e) {
		System.out.println("Unable to read item");
		System.err.println(e.getMessage());
		// Callers can also test if exception is an instance of
		// AmazonServiceException or AmazonClientException and cast
		// it to get additional information
	    }

	});
	call.get();

    }
}

// snippet-end:[dynamodb.java.trydax.DaxAsyncClientDemo] 