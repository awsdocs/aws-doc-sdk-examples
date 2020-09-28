# TypeScript environment for Amazon S3 examples
Environment for AWS SDK for JavaScript (V3) Amazon S3 examples. For more information, see the [AWS documentation for these examples](https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/s3-examples.html).

Amazon Simple Storage Service (Amazon S3) is an object storage service that offers industry-leading scalability, data availability, security, and performance.

This is a workspace where you can find working AWS SDK for JavaScript (V3) examples for Amazon S3. 

**Note:** The AWS SDK for JavaScript (V3) is written in TypeScript so, for consistency, these examples are also in TypeScript. TypeScript is
a superset of JavaScript, so you can also run these examples as JavaScript.


# Getting started

1. Clone the [AWS SDK Code Examples repository](https://github.com/awsdocs/aws-doc-sdk-examples) to your local environment. For instructions, see [Cloning a repository](https://docs.github.com/en/github/creating-cloning-and-archiving-repositories/cloning-a-repository).

2. Install the dependencies listed in the package.json file.

    **Note**: These dependencies include the client modules for the AWS services that this example requires, 
    such as ```@aws-sdk/client-s3```, ```@aws-sdk/client-cognito-identity```, and 
    ```@aws-sdk/credential-provider-cognito-identity```.
```
npm install ts-node -g // If using JavaScript, enter 'npm install node -g' instead
cd javascript/example_code_v3/s3
npm install
```
3. If you're using JavaScript, do the following:
- Change the example's file name extension from ```.ts``` to ```.js```.
- Remove the ```module.exports ={*}``` statement from the example file.

4. In your text editor, update user variables specified in the ```Inputs``` section of the example file.

5. Run the example code.
```
cd src
ts-node [sample name].ts // e.g., ts-node s3.ts
```
