#include <awsdoc/s3/s3_examples.h>
#include <aws/core/Aws.h>
#include <aws/s3/S3Client.h>

bool AwsDoc::S3::ListBuckets()
{
    Aws::S3::S3Client s3_client;
    auto outcome = s3_client.ListBuckets();

    if (outcome.IsSuccess())
    {
        std::cout << "Your Amazon S3 buckets:" << std::endl;

        Aws::Vector<Aws::S3::Model::Bucket> bucket_list =
            outcome.GetResult().GetBuckets();

        for (auto const &bucket : bucket_list)
        {
            std::cout << "  * " << bucket.GetName() << std::endl;
        }
        return true;
    }
    else
    {
        std::cout << "ListBuckets error: "
            << outcome.GetError().GetExceptionName() << " - "
            << outcome.GetError().GetMessage() << std::endl;
        return true;
    }
}

int main(int argc, char** argv)
{
    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        AwsDoc::S3::ListBuckets();
    }
    Aws::ShutdownAPI(options);
    return 0;
}
