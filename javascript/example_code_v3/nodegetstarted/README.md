# Typescript environment for 'Node Get Started' samples
Environment for AWS SDK for JavaScript (V3) samples for getting started with node. For more information, see the [AWS documentation for this example](https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/getting-started-nodejs.html).

This is a workspace where you can find working AWS SDK for JavaScript (V3) 'Node Get Started' samples. 

# Getting Started

1. Clone the [AWS SDK Code Samples repo](https://github.com/awsdocs/aws-doc-sdk-examples) repo to your local environment. See [here](https://docs.github.com/en/github/creating-cloning-and-archiving-repositories/cloning-a-repository) for instructions.

1. Install the dependencies listed in the package.json.

**Note**: These include the client module for the AWS services required in these example, 
which is *@aws-sdk/client-s3*.
```
npm install ts-node -g // if you prefer to use JavaScript, enter 'npm install node -g' instead
cd javascript/example_code_v3/nodegetstarted
yarn
```

3. In your text editor, update user variables specified in the 'Inputs' section of the sample file.

4. Run sample code:
```
cd src
ts-node [sample name].ts // e.g., ts-node sample.ts
```

