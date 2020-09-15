# Typescript environment for the SubmitData App tutorial
Environment for AWS SDK for JavaScript (V3) submitData App tutorial. For more information, see the 
[AWS documentation for this example](https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/cross-service-example-submitting-data.html).


This is a workspace where you can find working AWS SDK for JavaScript (V3) submitData App tutorial. 

# Getting Started

1. Clone the [AWSDocs Code Samples repo](https://github.com/awsdocs/aws-doc-sdk-examples) to your local environment. 
See [here](https://docs.github.com/en/github/creating-cloning-and-archiving-repositories/cloning-a-repository) for 
instructions.

1. Install the dependencies listed in the ~/javascript/example_v3/cross-services/submit-data-app/package.json.

**Note**: These include the AWS SDK for JavaScript (V3) client modules for the AWS services required in this example, 
which are the *@aws-sdk/client-cognito-identity*, *@aws-sdk/client-cognito-identity-browser*, *@aws-sdk/client-dynamodb*,
*@aws-sdk/credential-provider-cognito-identity*, *@aws-sdk/client-sns*, *@aws-sdk/client-s3*, and *@aws-sdk/client-iam*.
They also include third-party Node.js modules, fs (file-server), path, and webpack.
```
npm install ts-node -g // if you prefer to use JavaScript, enter 'npm install node -g' instead
cd javascript/example_code_v3/cross-services/submit-data-app 
yarn
```

