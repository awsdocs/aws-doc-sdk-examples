# TypeScript environment for Amazon Elemental MediaConvert (EMC) examples
This is a workspace where you can find the following AWS SDK for JavaScript version 3 (v3) Amazon EMC examples. 
 
The [AWS SDK for JavaScript v3](https://github.com/aws/aws-sdk-js-v3) is available. 

The [AWS SDK for JavaScript v3 Developer Guide](https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/mediaconvert-examples.html) contains these examples.

The [AWS SDK for JavaScript v3 API Reference Guide](https://docs.aws.amazon.com/AWSJavaScriptSDK/v3/latest/clients/client-mediaconvert/index.html) contains the API operations for the AWS SDK for JavaScript v3 Amazon EMC client module.

Amazon EMC is a file-based video transcoding service with broadcast-grade features.




# Getting started

1. Clone the [AWS SDK Code Samples repo](https://github.com/awsdocs/aws-doc-sdk-examples) to your local environment. See [the Github documentation](https://docs.github.com/en/github/creating-cloning-and-archiving-repositories/cloning-a-repository) for instructions.

2. Install the dependencies listed in the package.json.

**Note**: These include the client module for the AWS services required in these example, 
which is *@aws-sdk/client-mediaconvert*.
```
npm install ts-node -g # If using JavaScript, enter 'npm install node -g' instead
cd javascriptv3/example_code/mediaconvert
npm install
```

3. If you're using JavaScript, change the sample file extension from ```.ts``` to ```.js```.


4. In your text editor, update user variables specified in the ```Inputs``` section of the sample file.

5. Run sample code:
```
cd src
ts-node [example name].ts // e.g., ts-node iam_accesskeylastused.ts
```
