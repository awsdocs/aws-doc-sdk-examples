# Typescript environment for Amazon Cloudwatch examples
Environment for AWS SDK for JavaScript (V3) AWS Cloudwatch samples. For more information, see the 
[AWS documentation for these examples](https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/cloudwatch-examples.html).

Amazon CloudWatch enables you to collect, access, and correlate this data on a single platform from across all your AWS resources, applications, and services.

This is a workspace where you can find working AWS SDK for JavaScript (V3) Cloudwatch samples. 

**NOTE:** The AWS SDK for JavaScript (V3) is written in TypeScript so, for consistency, these examples are also in TypeScript. TypeScript is
a super-set of JavaScript so these examples can also be run as JavaScript.
# Getting Started

1. Clone the [AWSDocs Code Samples repo](https://github.com/awsdocs/aws-doc-sdk-examples) to your local environment. 
See [here](https://docs.github.com/en/github/creating-cloning-and-archiving-repositories/cloning-a-repository) for 
instructions.

2. Install the dependencies listed in the package.json.

**Note**: These include the client modules for the AWS services required in these example, 
which are the *@aws-sdk/client-cloudwatch*, *@aws-sdk/client-cloudwatch-events*, and *@aws-sdk/client-cloudwatch-logs*.
```
npm install ts-node -g // If you prefer to use JavaScript, enter 'npm install node -g' instead
cd javascript/example_code_v3/cloudwatch 
yarn
```
3. If you prefer to use JavaScript:
- change the sample file extension from ```.ts``` to ```.js```
- remove the ```module.exports ={*}``` statement from the sample file

4. In your text editor, update user variables specified in the 'Inputs' section of the sample file.

5. Run sample code:
```
cd src
ts-node [sample name].ts // If you prefer to use JavaScript, enter 'node [sample name].js' instead
```

