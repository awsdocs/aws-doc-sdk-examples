# Amazon Cloudwatch JavaScript SDK v3 code examples
Amazon Cloudwatch enables you to collect, access, and correlate this data on a single platform from across all your AWS resources, applications, and services.

## Code examples
This is a workspace where you can find the following AWS SDK for JavaScript version 3 (v3) Amazon Cloudwatch examples:

- [Delete CloudWatch alarms](src/cw_deletealarms.js)
- [Describe CloudWatch alarms](src/cw_describealarms.js)
- [Enable CloudWatch alarm actions](src/cw_enablealarmactions.js)
- [List CloudWatch metrics](src/cw_listmetrics.js)
- [Put CloudWatch metric alarms](src/cw_putmetricalarm.js)
- [Put CloudWatch metric data](src/cw_putmetricdata.js)
- [Put CloudWatch events](src/cwe_putevents.js)
- [Put CloudWatch event rule](src/cwe_putrule.js)
- [Put CloudWatch event targets](src/cwe_puttargets.js)
- [Delete CloudWatch log subscription filters](src/cwl_describesubscriptionfilters.js)
- [Describe CloudWatch log subscription filters](src/cwl_describesubscriptionfilters.js)
- [Put CloudWatch log subscription filters](src/cwl_putsubscriptionfilter.js)

**Note**: All code examples are written in ECMAscript 6 (ES6). For guidelines on converting to CommonJS, see 
[JavaScript ES6/CommonJS syntax](https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/sdk-examples-javascript-syntax.html).


## Getting started

1. Clone the [AWS Code Samples repo](https://github.com/awsdocs/aws-doc-sdk-examples) to your local environment. 
See [the Github documentation](https://docs.github.com/en/github/creating-cloning-and-archiving-repositories/cloning-a-repository) for 
instructions.

2. Install the dependencies listed in the package.json.

**Note**: These dependencies include the client modules for the AWS services that this example requires, 
which are the *@aws-sdk/client-cloudwatch*, *@aws-sdk/client-cloudwatch-events*, and *@aws-sdk/client-cloudwatch-logs*.
```
npm install node -g 
cd javascriptv3/example_code/cloudwatch 
npm install
```
3. In your text editor, update user variables specified in the ```Inputs``` section of the sample file.

4. Run sample code:
```
cd src
node [example name].js 
```
## Resources
- [AWS SDK for JavaScript v3 repo](https://github.com/aws/aws-sdk-js-v3) . 
- [AWS SDK for JavaScript v3 Developer Guide](https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/cloudwatch-examples.html)
- [AWS SDK for JavaScript v3 API Reference Guide - Amazon Cloudwatch client module](https://docs.aws.amazon.com/AWSJavaScriptSDK/v3/latest/clients/client-cloudwatch/index.html) 
- [AWS SDK for JavaScript v3 API Reference Guide - Amazon Cloudwatch Events client module](https://docs.aws.amazon.com/AWSJavaScriptSDK/v3/latest/clients/client-cloudwatch-events/index.html)
- [AWS SDK for JavaScript v3 API Reference Guide -  Amazon Cloudwatch Logs client module](https://docs.aws.amazon.com/AWSJavaScriptSDK/v3/latest/clients/client-cloudwatch-logs/index.html)

