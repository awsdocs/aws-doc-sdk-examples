#include <awsdoc/s3/s3_examples.h>
#include <aws/core/Aws.h>

int main(int argc, char** argv)
{
    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        const char bucketName[] = "";
        if (!AwsDoc::S3::ListObjects(bucketName))
        {
            return 1;
        }
    }
    Aws::ShutdownAPI(options);
    return 0;
}
