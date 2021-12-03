# JavaScript environment for Amazon Transcribe examples
Amazon Transcribe makes it easy for developers to add speech to text capabilities to their applications.

## Code examples
This is a workspace where you can find the following AWS SDK for JavaScript v3 Amazon Transcribe examples:
- [Create Transcribe job](src/transcribe_create_job.js) (StartTranscriptionJobCommand)
- [Create Transcribe medical job](src/transcribe_create_medical_job.js) (StartMedicalTranscriptionJobCommand)
- [Delete Transcribe job](src/transcribe_delete_job.js) (DeleteTranscriptionJobCommand)
- [Delete Transcribe medical job](src/transcribe_delete_medical_job.js) (DeleteMedicalTranscriptionJobCommand)
- [List Transcribe jobs](src/transcribe_list_jobs.js) (ListTranscriptionJobsCommand)
- [List Transcribe medical jobs](src/transcribe_list_medical_jobs.js) (ListMedicalTranscriptionJobsCommand)

**Note**: All code examples are written in ECMAscript 6 (ES6). For guidelines on converting to CommonJS, see
[JavaScript ES6/CommonJS syntax](https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/sdk-examples-javascript-syntax.html).

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
- AWS credentials. For details, see  [Setting credentials in Node.js](https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/setting-credentials-node.html) in the
  *AWS SDK for Javascript (v3) Developer Guide*.

1. Clone the [AWS SDK Code Samples repo](https://github.com/awsdocs/aws-doc-sdk-examples) to your local environment. See [the Github documentation](https://docs.github.com/en/github/creating-cloning-and-archiving-repositories/cloning-a-repository) for instructions.

2. Install the dependencies listed in the package.json.

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

## Unit tests
For more information see, the [README](../README.rst).
## Resources
- [AWS SDK for JavaScript v3](https://github.com/aws/aws-sdk-js-v3) 
- [AWS SDK for JavaScript v3 Developer Guide](https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/sqs-examples.html) 
- [AWS SDK for JavaScript v3 API Reference Guide](https://docs.aws.amazon.com/AWSJavaScriptSDK/v3/latest/clients/client-transcribe/index.html) 
- [Amazon Transcribe documentation](https://docs.aws.amazon.com/transcribe/)