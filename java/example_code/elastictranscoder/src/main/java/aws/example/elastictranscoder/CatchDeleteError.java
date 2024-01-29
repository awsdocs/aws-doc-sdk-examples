// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
// snippet-start:[elastictranscoder.java.catch_delete_error.import]
try {
   DeleteJobRequest request = new DeleteJobRequest(jobId);
   DeleteJobResult result = ET.deleteJob(request);
   System.out.println("Result: " + result);
   // Get error information from the service while trying to run the operation	
   }  catch (AmazonServiceException ase) {
      System.err.println("Failed to delete job " + jobId);
      // Get specific error information
      System.out.println("Error Message:    " + ase.getMessage());
      System.out.println("HTTP Status Code: " + ase.getStatusCode());
      System.out.println("AWS Error Code:   " + ase.getErrorCode());
      System.out.println("Error Type:       " + ase.getErrorType());
      System.out.println("Request ID:       " + ase.getRequestId());
   // Get information in case the operation is not successful for other reasons	
   }  catch (AmazonClientException ace) {
      System.out.println("Caught an AmazonClientException, which means"+
      " the client encountered " +
      "an internal error while trying to " +
      "communicate with Elastic Transcoder, " +
      "such as not being able to access the network.");
      System.out.println("Error Message: " + ace.getMessage());
   }
// snippet-end:[elastictranscoder.java.catch_delete_error.import]
