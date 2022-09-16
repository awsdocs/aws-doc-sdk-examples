# Amazon Redshift JavaScript SDK v3 code examples
Amazon Redshift is a fully managed, petabyte-scale data warehouse service in the cloud. You can start with just a few hundred gigabytes of data and scale to a petabyte or more. This enables you to use your data to acquire new insights for your business and customers.

## Code examples
This is a workspace where you can find the following AWS SDK for JavaScript version 3 (v3) Amazon Redshift examples. 

- [Create Redshift cluster](src/redshift-create-cluster.js)
- [Delete a Redshift cluster](src/redshift-delete-cluster.js)
- [Describe Redshift clusters](src/redshift-describe-clusters.js)
- [Modify a Redshift cluster](src/redshift-modify-cluster.js)

**Note**: All code examples are written in ECMAscript 6 (ES6). For guidelines on converting to CommonJS, see 
[JavaScript ES6/CommonJS syntax](https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/sdk-example-javascript-syntax.html).


# Getting started

1. Clone the [AWS SDK Code Samples repo](https://github.com/awsdocs/aws-doc-sdk-examples) to your local environment. See [the Github documentation](https://docs.github.com/en/github/creating-cloning-and-archiving-repositories/cloning-a-repository) for instructions.

2. Install the dependencies listed in the package.json.

**Note**: These include the client module for the AWS services required in these example, 
which is *@aws-sdk/client-redshift-node*.

```
npm install node -g
cd javascriptv3/example_code/redshift
npm install
```

3. In your text editor, update user variables specified in the ```Inputs``` section of the sample file.

4. Run sample code:
```
cd src
node [example name].js // For example, node redshift-create-cluster.js
```

## Resources
- [AWS SDK for JavaScript v3](https://github.com/aws/aws-sdk-js-v3)  
- [AWS SDK for JavaScript v3 Developer Guide](https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/redshift-examples.html) 
- [AWS SDK for JavaScript v3 API Reference Guide](https://docs.aws.amazon.com/AWSJavaScriptSDK/v3/latest/clients/client-redshift/index.html)

 
