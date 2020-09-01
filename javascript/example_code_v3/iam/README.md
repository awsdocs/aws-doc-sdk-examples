# typescript_environment_iam_examples
Environment for AWS SDK for JavaScript (V3) AWS Identity and Access Management (IAM) samples.
IAM enables you to manage access to AWS services and resources securely.
This is a workspace where you can find working AWS SDK for JavaScript (V3) IAM samples. 

# Getting Started

1. Install the dependencies listed in the package.json.

**Note**: These include the client modules for the AWS services required in these example, 
which are the Amazon IAM and Amazon Security Token Service (STS).
```
npm install ts-node -g
cd iam
yarn
```

2. In your text editor, update user variables specified in the 'Inputs' section of the sample file.

3. Run sample code:
```
cd src
ts-node [sample name].ts // e.g., ts-node iam_accesskeylastused.ts
```
