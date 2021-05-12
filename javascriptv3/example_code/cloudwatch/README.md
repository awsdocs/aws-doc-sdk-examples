# TypeScript environment for Amazon Cloudwatch examples
Amazon Cloudwatch enables you to collect, access, and correlate this data on a single platform from across all your AWS resources, applications, and services.

This is a workspace where you can find the following AWS SDK for JavaScript version 3 (v3) Amazon Cloudwatch examples:

- [Delete CloudWatch alarms](src/cw_deletealarms.ts)
- [Describe CloudWatch alarms](src/cw_describealarms.ts)
- [Enable CloudWatch alarm actions](src/cw_enablealarmactions.ts)
- [List CloudWatch metrics](src/cw_listmetrics.ts)
- [Put CloudWatch metric alarms](src/cw_putmetricalarm.ts)
- [Put CloudWatch metric data](src/cw_putmetricdata.ts)
- [Put CloudWatch events](src/cwe_putevents.ts)
- [Put CloudWatch event rule](src/cwe_putrule.ts)
- [Put CloudWatch event targets](src/cwe_puttargets.ts)
- [Delete CloudWatch log subscription filters](src/cwl_describesubscriptionfilters.ts)
- [Describe CloudWatch log subscription filters](src/cwl_describesubscriptionfilters.ts)
- [Put CloudWatch log subscription filters](src/cwl_putsubscriptionfilter.ts)



## Getting started

1. Clone the [AWS Code Samples repo](https://github.com/awsdocs/aws-doc-sdk-examples) to your local environment. 
See [the Github documentation](https://docs.github.com/en/github/creating-cloning-and-archiving-repositories/cloning-a-repository) for 
instructions.

2. Install the dependencies listed in the package.json.

**Note**: These dependencies include the client modules for the AWS services that this example requires, 
which are the *@aws-sdk/client-cloudwatch*, *@aws-sdk/client-cloudwatch-events*, and *@aws-sdk/client-cloudwatch-logs*.
```
npm install ts-node -g // If you prefer to use JavaScript, enter 'npm install node -g' instead
cd javascriptv3/example_code/cloudwatch 
npm install
```
3. If you're using JavaScript, change the sample file extension from ```.ts``` to ```.js```.


4. In your text editor, update user variables specified in the ```Inputs``` section of the sample file.

5. Run sample code:
```
cd src
ts-node [example name].ts // If you prefer to use JavaScript, enter 'node [sample name].js' instead
```
## Resources
- [AWS SDK for JavaScript v3 repo](https://github.com/aws/aws-sdk-js-v3) . 
- [AWS SDK for JavaScript v3 Developer Guide](https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/cloudwatch-examples.html)
- [AWS SDK for JavaScript v3 API Reference Guide - Amazon Cloudwatch client module](https://docs.aws.amazon.com/AWSJavaScriptSDK/v3/latest/clients/client-cloudwatch/index.html) 
- [AWS SDK for JavaScript v3 API Reference Guide - Amazon Cloudwatch Events client module](https://docs.aws.amazon.com/AWSJavaScriptSDK/v3/latest/clients/client-cloudwatch-events/index.html)
- [AWS SDK for JavaScript v3 API Reference Guide -  Amazon Cloudwatch Logs client module](https://docs.aws.amazon.com/AWSJavaScriptSDK/v3/latest/clients/client-cloudwatch-logs/index.html)

