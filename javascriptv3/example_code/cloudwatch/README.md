# TypeScript environment for Amazon Cloudwatch examples
This is a workspace where you can find working AWS SDK for JavaScript version 3 (v3) Amazon Cloudwatch examples. 

The [preview version of the AWS SDK for JavaScript v3](https://github.com/aws/aws-sdk-js-v3) is available. 

Once it's released, see the [AWS SDK for JavaScript Developer Guide](https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/cloudwatch-examples.html) for the topic containing these examples.

Amazon CloudWatch enables you to collect, access, and correlate this data on a single platform from across all your AWS resources, applications, and services.


**NOTE:** The AWS SDK for JavaScript v3 is written in TypeScript so, for consistency, these examples are also in TypeScript. TypeScript extends of JavaScript so these examples can also be run as JavaScript. For more information, see [TypeScript homepage](https://www.typescriptlang.org/).
# Getting started

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

