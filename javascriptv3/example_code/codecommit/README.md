#  AWS CodeCommit JavaScript SDK v3 code examples
AWS CodeCommit is a fully-managed source control service that makes it easy for companies to host secure and highly scalable private Git repositories.
## Code examples
This is a workspace where you can find the following AWS SDK for JavaScript version 3 (v3) AWS CodeBuild examples: 

- [Create a branch](src/createBranch.js)
- [Commit changes to repo](src/createCommit.js)
- [Create a pull request](src/createPullRequest.js)
- [Create a repository](src/createRepository.js)
- [Delete a branch](src/deleteBranch.js)
- [Delete a repository](src/deleteRepository.js)
- [Describe pull request events](src/describePullRequestEvents.js)
- [Get merge options](src/getMergeOptions.js)
- [Get informtion about a pull request](src/getPullRequest.js)
- [Get informatino about a repository](src/getRepository.js)
- [List your repositories](src/listRepositories.js)
- [Merge branches](src/mergeBranches.js)


**Note**: All code examples are written in ECMAscript 6 (ES6). For guidelines on converting to CommonJS, see 
[JavaScript ES6/CommonJS syntax](https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/sdk-examples-javascript-syntax.html).

## Getting started

1. Clone the [AWS SDK Code Samples repo](https://github.com/awsdocs/aws-doc-sdk-examples) to your local environment. See [the Github documentation](https://docs.github.com/en/github/creating-cloning-and-archiving-repositories/cloning-a-repository) for instructions.

2. Install the dependencies listed in the package.json.

```
npm install node -g
cd javascriptv3/example_code/codecommit
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
- [AWS SDK for JavaScript v3 repo](https://github.com/aws/aws-sdk-js-v3)
- [AWS SDK for JavaScript v3 API Reference Guide](https://docs.aws.amazon.com/AWSJavaScriptSDK/v3/latest/clients/client-codecommit/index.html) 
- [AWS CodeCommit user guide](https://docs.aws.amazon.com/codecommit/latest/userguide/welcome.html)
