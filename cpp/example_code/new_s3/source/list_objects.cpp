#include <awsdoc/s3/s3_examples.h>
#include <aws/core/Aws.h>
#include <aws/core/utils/memory/stl/AWSString.h>
#include <aws/s3/S3Client.h>
#include <aws/s3/model/ListObjectsRequest.h>

bool AwsDoc::S3::ListObjects(const char* bucketName)
{
    std::cout << "Objects in S3 bucket: " << bucketName << std::endl;

    Aws::S3::S3Client s3Client;

    Aws::S3::Model::ListObjectsRequest listObjectsRequest;
    listObjectsRequest.WithBucket(bucketName);

    auto listObjectsOutcome = s3Client.ListObjects(listObjectsRequest);

    if (listObjectsOutcome.IsSuccess())
    {
        Aws::Vector<Aws::S3::Model::Object> objectList =
            listObjectsOutcome.GetResult().GetContents();

        for (auto const &s3Object : objectList)
        {
            std::cout << "* " << s3Object.GetKey() << std::endl;
        }
        return true;
    }
    else
    {
        std::cout << "ListObjects error: " <<
            listObjectsOutcome.GetError().GetExceptionName() << " " <<
            listObjectsOutcome.GetError().GetMessage() << std::endl;
        return false;
    }
}

int main(int argc, char** argv)
{
    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        if (argc < 2)
        {
            std::cout << std::endl <<
                "To run this example, supply the name of a bucket to list!" <<
                std::endl << "Ex: run_list_objects <bucket-name>" << std::endl
                << std::endl;
            return 1;
        }
        const char* bucketName = argv[1];
        AwsDoc::S3::ListObjects(bucketName);
    }
    Aws::ShutdownAPI(options);
    return 0;
}
