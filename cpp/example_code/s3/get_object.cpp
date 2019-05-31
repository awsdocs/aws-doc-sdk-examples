 
//snippet-sourcedescription:[get_object.cpp demonstrates how to retrieve an object from an Amazon S3 bucket.]
//snippet-service:[s3]
//snippet-keyword:[Amazon S3]
//snippet-keyword:[C++]
//snippet-keyword:[Code Sample]
//snippet-sourcetype:[full-example]
//snippet-sourceauthor:[AWS]


/*
   Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.

   This file is licensed under the Apache License, Version 2.0 (the "License").
   You may not use this file except in compliance with the License. A copy of
   the License is located at

    http://aws.amazon.com/apache2.0/

   This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
   CONDITIONS OF ANY KIND, either express or implied. See the License for the
   specific language governing permissions and limitations under the License.
*/
//snippet-start:[s3.cpp.get_object.inc]
#include <aws/core/Aws.h>
#include <aws/s3/S3Client.h>
#include <aws/s3/model/GetObjectRequest.h>
//snippet-end:[s3.cpp.get_object.inc]

/**
 * Get an object from an Amazon S3 bucket.
 */
int main(int argc, char** argv)
{
    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        if (argc < 3)
        {
            std::cout << std::endl <<
                "To run this example, supply the name of a bucket and an object key!" <<
                std::endl << "Ex: get_object <bucket-name> <object_key>" << std::endl
                << std::endl;
            exit(1);
        }
        // snippet-start:[s3.cpp.get_object.code]
        // Assign these values before running the program
        const Aws::String bucket_name = argv[1];
        const Aws::String object_name = argv[2];  // For demo, set to a text file

        // Set up the request
        Aws::S3::S3Client s3_client;
        Aws::S3::Model::GetObjectRequest object_request;
        object_request.SetBucket(bucket_name);
        object_request.SetKey(object_name);

        // Get the object
        auto get_object_outcome = s3_client.GetObject(object_request);
        if (get_object_outcome.IsSuccess())
        {
            // Get an Aws::IOStream reference to the retrieved file
            auto &retrieved_file = get_object_outcome.GetResultWithOwnership().GetBody();

            // Output the first line of the retrieved text file
            std::cout << "Beginning of file contents:\n";
            char file_data[255] = { 0 };
            retrieved_file.getline(file_data, 254);
            std::cout << file_data << std::endl;
        }
        else
        {
            auto error = get_object_outcome.GetError();
            std::cout << "ERROR: " << error.GetExceptionName() << ": " 
                << error.GetMessage() << std::endl;
        }
        // snippet-end:[s3.cpp.get_object.code]
    }
    Aws::ShutdownAPI(options);
}

