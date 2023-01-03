# Amazon Elemental MediaConvert (EMC) JavaScript SDK v3 code examples
Amazon EMC is a file-based video transcoding service with broadcast-grade features.
 ## Code examples
This is a workspace where you can find the following AWS SDK for JavaScript version 3 (v3) Amazon EMC examples: 

- [Create a job template](src/emc_create_jobtemplate.js)
- [Create a job](src/emc_createjob.js)
- [Create a job using a template](src/emc_template_createjob.js)
- [List jobs](src/emc_listjobs.js)
- [Cancel a job](src/emc_canceljob.js)
- [List templates](src/emc_listtemplates.js)
- [Delete a template](src/emc_deletetemplate.js)
- [Get an endpoint](src/emc_getendpoint.js)

**Note**: All code examples are written in ECMAscript 6 (ES6). For guidelines on converting to CommonJS, see 
[JavaScript ES6/CommonJS syntax](https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/sdk-example-javascript-syntax.html).

## Getting started

1. Clone the [AWS SDK Code Samples repo](https://github.com/awsdocs/aws-doc-sdk-examples) to your local environment. See [the Github documentation](https://docs.github.com/en/github/creating-cloning-and-archiving-repositories/cloning-a-repository) for instructions.

2. Install the dependencies listed in the package.json.

**Note**: These include the client module for the AWS services required in these example, 
which is *@aws-sdk/client-mediaconvert*.
```
npm install node -g
cd javascriptv3/example_code/mediaconvert
npm install
```
3. In your text editor, update user variables specified in the ```Inputs``` section of the sample file.

4. Run sample code:
```
cd src
node [example name].js // For example, node emc_createjob.js
```
 ## Resources
- [AWS SDK for JavaScript v3](https://github.com/aws/aws-sdk-js-v3) 
- [AWS SDK for JavaScript v3 Developer Guide](https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/mediaconvert-examples.html) 
- [AWS SDK for JavaScript v3 API Reference Guide](https://docs.aws.amazon.com/AWSJavaScriptSDK/v3/latest/clients/client-mediaconvert/index.html)
