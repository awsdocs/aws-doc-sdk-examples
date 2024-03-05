import * as cdk from '@aws-cdk/core';
import * as s3 from '@aws-cdk/aws-s3';

interface S3StackProps extends cdk.StackProps {
  customBucketName: string;
}

export class S3Stack extends cdk.Stack {
  constructor(scope: cdk.Construct, id: string, props: S3StackProps) {
    super(scope, id, props);

    new s3.Bucket(this, 'custombucket1212312', {
      bucketName: props.customBucketName,
    });
  }
}

