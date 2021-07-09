# TypeScript environment for Amazon Transcribe examples
Amazon Transcribe makes it easy for developers to add speech to text capabilities to their applications.

## Code examples
This is a workspace where you can find the following AWS SDK for JavaScript v3 Transcribe examples:
- [Create Transcribe job](src/transcribe_create_job.js)
- [Create Transcribe medical job](src/transcribe_create_medical_job.js)
- [Delete Transcribe job](src/transcribe_delete_job.js)
- [Delete Transcribe medical job](src/transcribe_delete_medical_job.js)
- [List Transcribe jobs](src/transcribe_list_jobs.js)
- [List Transcribe medical jobs](src/transcribe_list_medical_jobs.js)


## Getting started

1. Clone the [AWS SDK Code Samples repo](https://github.com/awsdocs/aws-doc-sdk-examples) repo to your local environment. See [the Github documentation](https://docs.github.com/en/github/creating-cloning-and-archiving-repositories/cloning-a-repository) for instructions.

2. Install ts-node or node the dependencies listed in the package.json.

**Note**: These include the client module for the AWS services required in these example, 
which is "@aws-sdk/client-transcribe".
```
npm install node -g 
cd javascriptv3/example_code/transcribe
npm install
```
3. In your text editor, update user variables specified in the ```Inputs``` section of the sample file.

4. Run sample code:
```
cd src
node [example name].js
```
## Resources
- [AWS SDK for JavaScript v3](https://github.com/aws/aws-sdk-js-v3) 
- [AWS SDK for JavaScript v3 Developer Guide](https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/sqs-examples.html) 
- [AWS SDK for JavaScript v3 API Reference Guide](https://docs.aws.amazon.com/AWSJavaScriptSDK/v3/latest/clients/client-transcribe/index.html) 
