import * as cdk from 'aws-cdk-lib';
import * as kinesisfirehose from 'aws-cdk-lib/aws-kinesisfirehose';
import * as s3 from 'aws-cdk-lib/aws-s3';

class KinesisFirehoseExample extends cdk.Stack {
  constructor(scope: cdk.App, id: string, props?: cdk.StackProps) {
    super(scope, id, props);

    // Create an S3 bucket for data delivery
    const bucket = new s3.Bucket(this, 'DataBucket');

    // Create a Kinesis Firehose Delivery Stream
    const deliveryStream = new kinesisfirehose.CfnDeliveryStream(this, 'DeliveryStream', {
      deliveryStreamType: 'DirectPut',
      extendedS3DestinationConfiguration: {
        bucketArn: bucket.bucketArn,
        roleArn: 'YOUR_IAM_ROLE_ARN',
      },
    });
  }
}

const app = new cdk.App();
new KinesisFirehoseExample(app, 'KinesisFirehoseExample');