from aws_cdk import (
    aws_s3 as s3,
    cdk,
)


class S3Stack(cdk.Stack):
    def __init__(self, app: cdk.App, id: str) -> None:
        super().__init__(app, id)

        bucket = aws_s3.Bucket(
            self,
            "MyBucket",
            versioned=True,
            encryption=aws_s3.BucketEncryption.KmsManaged)


app = cdk.App()
S3Stack(app, "MyStack")
app.run()
