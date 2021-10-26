# Amazon Cloudwatch JavaScript SDK v2 code examples

## Purpose
The code examples in this directory demonstrate how to work with Amazon CloudWatch
using the AWS SDK for JavaScript v2.

Amazon CloudWatch provides a reliable, scalable, and flexible monitoring solution that you can start using within minutes. 
You no longer need to set up, manage, and scale your own monitoring systems and infrastructure.

## Code examples
### API examples
- [Delete CloudWatch alarms](./cw_deletealarms.js)
- [Describe CloudWatch alarms](./cw_describealarms.js)
- [Disable CloudWatch alarm actions](./cw_disablealarmactions.js)
- [Enable CloudWatch alarm actions](./cw_enablealarmactions.js)
- [List CloudWatch metrics](./cw_listmetrics.js)
- [Put CloudWatch metric alarms](./cw_putmetricalarm.js)
- [Put CloudWatch metric data](./cw_putmetricdata.js)

## Important

- As an AWS best practice, grant this code least privilege, or only the 
  permissions required to perform a task. For more information, see 
  [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege) 
  in the *AWS Identity and Access Management User Guide*.
- This code has not been tested in all AWS Regions. Some AWS services are 
  available only in specific AWS Regions. For more information, see the 
  [AWS Regional Services List](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services/)
  on the AWS website.
- Running this code might result in charges to your AWS account.

## Running the code

### Prerequisites
- An AWS account. To create an account, see [How do I create and activate a new AWS account](https://aws.amazon.com/premiumsupport/knowledge-center/create-and-activate-aws-account/) on the AWS Premium Support website.
- AWS credentials. For details, see  [Setting credentials in Node.js](https://docs.aws.amazon.com/sdk-for-javascript/v2/developer-guide/setting-credentials-node.html) in the 
  *AWS SDK for Javascript (v2) Developer Guide*.
- The AWS SDK for JavaScript (v2). For AWS SDK for JavaScript download and installation instructions, see 
  [Installing the AWS SDK for JavaScript](https://docs.aws.amazon.com/sdk-for-javascript/v2/developer-guide/installing-jssdk.html) in the 
  *AWS SDK for JavaScript (v2) Developer Guide*.

Most of these code example files can be run with very little to no modification. For example, to use Node.js 
to run the `cw_deletealarms.js` file, replace the hard-coded values in the file with your own values, save the file, and then run the file. For example:

```
node cw_deletealarms.js
```


## Resources
 
- [AWS SDK for JavaScript v2 Developer Guide](https://docs.aws.amazon.com/sdk-for-javascript/v2/developer-guide/cloudwatch-examples.html)
- [AWS SDK for JavaScript v2 API Reference Guide -  Amazon Cloudwatch](https://docs.aws.amazon.com/AWSJavaScriptSDK/latest/AWS/CloudWatch.html)
