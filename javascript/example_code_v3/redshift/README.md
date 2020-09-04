# Typescript environment for Amazon Redshift samples
Environment for AWS SDK for JavaScript (V3) samples for Amazon Redshift. For more information, see the [AWS documentation for these examples](https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/redshift-examples.html).

This example is available here https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/redshift-examples.html.

This is a workspace where you can find working AWS SDK for JavaScript (V3) Amazon Redshift samples. 

# Getting Started

1. Clone the repo to your local environment. See [here](https://docs.github.com/en/github/creating-cloning-and-archiving-repositories/cloning-a-repository) for instructions.

1. Install the dependencies listed in the package.json.

**Note**: These include the client module for the AWS services required in these example, 
which is *@aws-sdk/client-redshift-node*.
```
npm install ts-node -g // if you prefer to use JavaScript, enter 'npm install node -g' instead
cd javascript/example_code_v3/redshift
yarn
```

3. In your text editor, update user variables specified in the 'Inputs' section of the sample file.

4. Run sample code:
```
cd src
ts-node [sample name].ts // e.g., ts-node redshift-create-cluster.ts
```

