import * as cdk from 'aws-cdk-lib';
import * as kinesis from 'aws-cdk-lib/aws-kinesisfirehose';
import * as logs from 'aws-cdk-lib/aws-logs';

const app = new cdk.App();
const stack = new cdk.Stack(app, 'KinesisFirehoseExampleStack');

const logGroup = new logs.LogGroup(stack, 'LogGroup', {
  logGroupName: process.env.LOG_GROUP_NAME,
  removalPolicy: cdk.RemovalPolicy.DESTROY,
});

const deliveryStream = new kinesis.CfnDeliveryStream(stack, 'DeliveryStream', {
  deliveryStreamName: process.env.DELIVERY_STREAM_NAME,
  deliveryStreamType: 'DirectPut',
});

new cdk.CfnOutput(stack, 'DeliveryStreamName', {
  value: deliveryStream.deliveryStreamName,
  description: 'The name of the Kinesis Firehose Delivery Stream',
});

new cdk.CfnOutput(stack, 'LogGroupName', {
  value: logGroup.logGroupName,
  description: 'The name of the CloudWatch Log Group',
});