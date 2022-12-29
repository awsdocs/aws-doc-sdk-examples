// import * as cdk from 'aws-cdk-lib';
import { Construct } from 'constructs';
// import * as sqs from 'aws-cdk-lib/aws-sqs';

export interface EcrProps {
  // Define construct properties here
}

export class Ecr extends Construct {

  constructor(scope: Construct, id: string, props: EcrProps = {}) {
    super(scope, id);

    // Define construct contents here

    // example resource
    // const queue = new sqs.Queue(this, 'EcrQueue', {
    //   visibilityTimeout: cdk.Duration.seconds(300)
    // });
  }
}
