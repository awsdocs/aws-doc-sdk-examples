# Guidelines for contributing

Welcome to AWS Docs! We thrive on your contribution. :heart:

## Before you get started
Please take 3 minutes to understand:
1. [Submit an issue](#submit-an-issue)
1. [What to expect from AWS](#what-to-expect-from-aws)
1. [Write code and submit pull requests](#write-code-and-submit-pull-requests)

---

## Submit an issue

We look forward to receiving your issues for:

* New content you'd like to contribute (such as new code examples or tutorials) - for more information, see [Types of examples](https://github.com/awsdocs/aws-doc-sdk-examples/edit/main/CONTRIBUTING.md#types-of-examples) below.
* Inaccuracies in the content
* Information gaps in the content that need more detail to be complete
* Typos or grammatical errors
* Suggested rewrites that improve clarity and reduce confusion

## What to expect from AWS

When you submit a pull request, our team is notified and will respond as quickly as we can. We'll do our best to work with you to ensure that your pull request adheres to our style and standards. If we merge your pull request, we might make additional edits later for style or clarity.

The AWS documentation source files on GitHub aren't published directly to the official documentation website. If we merge your pull request, we'll publish your changes to the documentation website as soon as we can, but they won't appear immediately or automatically.


**Note:** We all write differently, and you might not like how we've written or organized something currently. We want that feedback. But please be sure that your request for a rewrite is supported by the previous criteria. If it isn't, we might decline to merge it.

## Write code and submit pull requests

If you'd like to contribute, but don't have a project in mind, look at the [open issues](https://github.com/awsdocs/aws-doc-sdk-examples/issues) in this repository for some ideas. Any issues with the [help wanted](https://github.com/awsdocs/aws-doc-sdk-examples/labels/help%20wanted) or [enhancement](https://github.com/awsdocs/aws-doc-sdk-examples/labels/enhancement) labels are a great place to start.

In addition to written content, we really appreciate new examples for our documentation, such as examples for different platforms or environments, and examples in additional programming languages.

Code examples are organized by service for each SDK. Within each SDK, the folder and example structure varies. To add
an example, find the SDK and service folder and add the example following the established convention for that SDK.

For example:

* Amazon S3 examples for AWS SDK for Java V2 are stored in separate files for each action in the `javav2/example_code/s3/src/main/java/com/example/s3` folder.
* Amazon S3 examples for AWS SDK for Python (Boto3) are stored in thematically grouped files, such as `bucket_wrapper.py` in `python/example_code/s3/s3_basics` for bucket actions.

If your example uses multiple APIs from a single service,
use the action that you consider most important.
For example, if you are listing resources, then adding a resource,
and listing the resources once again, use the action that adds the resource.

If your example uses multiple services and you aren't sure where to add it to the repo,
create an issue and describe what your code example does. 
One of the AWS SDK code example team members will follow up with you in that issue.

To contribute, send us a pull request. For small changes, such as fixing a typo or adding a link, you can use the [GitHub Edit Button](https://blog.github.com/2011-04-26-forking-with-the-edit-button/). For larger changes:

1. [Fork the repository](https://help.github.com/articles/fork-a-repo/).
2. In your fork, make your change in a branch that's based on this repo's **main** branch.
3. Commit the change to your fork, using a clear and descriptive commit message.
4. [Create a pull request](https://help.github.com/articles/creating-a-pull-request-from-a-fork/), answering any questions in the pull request form.

### Before you send us a pull request, please be sure that:

1. You're working from the latest source on the **main** branch.
2. You check [existing open](https://github.com/awsdocs/aws-doc-sdk-examples/pulls), and [recently closed](https://github.com/awsdocs/aws-doc-sdk-examples/pulls?q=is%3Apr+is%3Aclosed), pull requests to be sure that someone else hasn't already addressed the problem.
3. You [create an issue](https://github.com/awsdocs/aws-doc-sdk-examples/issues/new) before working on a contribution that will take a significant amount of your time.

For contributions that will take a significant amount of time, [open a new issue](https://github.com/awsdocs/aws-doc-sdk-examples/issues/new) to pitch your idea before you get started. Explain the problem and describe the content you want to see added to the documentation. Let us know if you'll write it yourself or if you'd like us to help. We'll discuss your proposal with you and let you know whether we're likely to accept it. We don't want you to spend a lot of time on a contribution that might be outside the scope of the documentation or that's already in the works.

### Types of examples
There are three types of examples of AWS SDK usage in this repo:
1. **Single action** - show how to call individual service functions, such as [creating an Amazon S3 bucket](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javascriptv3/example_code/s3/src/s3_createbucket.js).
2. **Scenario** - show how to accomplish specific tasks by calling multiple functions within a single service, such as [Getting started with Amazon S3 buckets and objects](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javascriptv3/example_code/s3/scenarios/s3_basics/src/s3_basics.js). 
3. **Cross-service**  - show how to build sample applications across multiple AWS services, such as the [AWS Photo Analyzer](https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/javav2/usecases/creating_photo_analyzer_app), which demonstrates how to build web application that analyzes nature images located in an Amazon Simple Storage Service (Amazon S3) bucket by using the AWS SDK for Java V2.

When you submit a new code example to us, we strongly encourage you to include the following:

* **Provide a README.md file at the root level of your submission to help users save time and effort when they work with your example.** 
  At a minimum, your README.md file should describe what your example demonstrates, call out any prerequisites needed to run it, and then tell users how to run it. 
  [Here's a are the README template](https://github.com/awsdocs/aws-doc-sdk-examples/wiki/README-templates) 
  to use.
* **Add code comments**
  For more information and examples, see [Code comment guidelines](https://github.com/awsdocs/aws-doc-sdk-examples/wiki/Code-comment-guidelines)
* **Write your code in a modular style to help users more easily copy and reuse it in their own solutions.** 
  By "modular," we mean that your code should accept inputs from the caller and return outputs to the caller. Provide comments in the code that describe these inputs and outputs. Also, don't hard-code input values in modularized code. Instead, provide these values through your unit tests, as described in the next point. 
  [Here's a good example](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/gov2/sts/AssumeRole/AssumeRolev2.go) 
  of code written in a modular style.
* **Add some type of [unit tests](https://en.wikipedia.org/wiki/Unit_testing ) to help users more easily run your example.** These unit tests can use hard-coded input values (or input values provided by the user) to call your example code. For more information and examples, see the [Code quality guidelines - testing and linting](https://github.com/awsdocs/aws-doc-sdk-examples/wiki/Code-quality-guidelines---testing-and-linting)
* **Add standard error or exception handling to your code to enable easier troubleshooting and recovery.** [
  Here's a good example](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/lambda/src/main/java/com/example/lambda/LambdaInvoke.java) 
  of standard error/exception handling.  
* **Don't include personal account data, keys, or IDs in your examples**. Code should obtain access keys from the standard AWS SDK credentials and configuration files, use environment variables or external data files, or query the user for this information.
* **Format code lines to 80 characters wherever possible**. Long lines can often spill off the side of the screen in the PDF versions of the documentation, making the code unreadable. If your code includes long text strings, consider breaking these into smaller chunks and concatenating them.
* **Use spaces, not tabs, for indentation**. Tabs are variable length in most editors, but will usually render as 8 characters wide in printed documentation. To ensure consistent formatting in printed code, we recommend using *4 spaces*, unless the target language has a different convention. You can ignore this rule for makefiles, which might *require* the use of tabs. But these are typically used only for building examples, and aren't  included in documentation.
* **All code must be submitted under the [Apache 2.0 license](https://www.apache.org/licenses/LICENSE-2.0).**
                                                      
If your code example submission is missing any of these things, we might ask you to include them before we merge. 

Although many older code examples in this repo don't contain all of these things, we're working to ensure that all newer ones do.

### Default branch name change

We have changed the default branch for this repo from **master** to **main**.

If the parent branch of your fork or branch is **master**,
the following instructions tell you how to change the parent branch to **main**.

To show the parent branch,
where **BRANCH** is the name of your branch:

1. Navigate to the root of your branch or fork.
2. Make sure your branch is the current branch (**git checkout BRANCH**).
3. Run **git branch --contains**.

### Changing a branch parent branch from master to main
To change the parent branch for your branch to **main**,
navigate to the root of your branch and enter the following commands,
where *BRANCH* is the name of your branch:

```		
   git branch -m master main
   git fetch origin
   git branch -u origin/main main
   git remote set-head origin -a
   git remote update --prune
```

### Changing a fork's default branch from master to main
GitHub will notify you when a parent branch has changed.
To change your fork's default branch to **main**:

1. Navigate to main web page of your fork.
2. You should see a "The default branch on the parent repository has been renamed" message.
3. Select the **branch settings** link.
4. Change **master** to **main**.

## Questions or issues?
If you have any questions, or if you experience an issue when retargeting your branch or fork,
create a new GitHub issue and include as much detail as possible.

## Code of conduct

This project has adopted the [Amazon Open Source Code of Conduct](https://aws.github.io/code-of-conduct). For more information, see the [Code of Conduct FAQ](https://aws.github.io/code-of-conduct-faq) or contact [opensource-codeofconduct@amazon.com](mailto:opensource-codeofconduct@amazon.com) with any additional questions or comments.

## Security issue notifications

If you discover a potential security issue, please notify AWS Security via our [vulnerability reporting page](http://aws.amazon.com/security/vulnerability-reporting/). Please do **not** create a public issue on GitHub.

## Licensing

See the [LICENSE](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/LICENSE) file for this project's licensing. We will ask you to confirm the licensing of your contribution. We may ask you to sign a [Contributor License Agreement (CLA)](http://en.wikipedia.org/wiki/Contributor_License_Agreement) for larger changes.
