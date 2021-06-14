# Amazon Cloudwatch Events JavaScript SDK v3 code examples
Amazon CloudWatch Events delivers a near real-time stream of system events that describe changes in Amazon Web Services (AWS) resources. 
## Code examples
This is a workspace where you can find the following AWS SDK for JavaScript version 3 (v3) Amazon Cloudwatch Events examples:

- [Put CloudWatch events](src/putEvents.js)
- [Put CloudWatch event rule](src/putRule.js)
- [Put CloudWatch event targets](src/putTargets.js)

**Note**: All code examples are written in ECMAscript 6 (ES6). For guidelines on converting to CommonJS, see 
[JavaScript ES6/CommonJS syntax](https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/sdk-examples-javascript-syntax.html).


## Getting started

1. Clone the [AWS Code Samples repo](https://github.com/awsdocs/aws-doc-sdk-examples) to your local environment. 
See [the Github documentation](https://docs.github.com/en/github/creating-cloning-and-archiving-repositories/cloning-a-repository) for 
instructions.

2. Install the dependencies listed in the package.json.

**Note**: These dependencies include the client modules for the AWS services that this example requires, 
which include *@aws-sdk/client-cloudwatch-events*.
```
npm install node -g 
cd javascriptv3/example_code/cloudwatch-events 
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
- [AWS SDK for JavaScript v3 API Reference Guide - Amazon Cloudwatch Events client module](https://docs.aws.amazon.com/AWSJavaScriptSDK/v3/latest/clients/client-cloudwatch-events/index.html)

