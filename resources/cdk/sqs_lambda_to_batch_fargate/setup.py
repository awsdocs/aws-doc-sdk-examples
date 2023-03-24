import setuptools


with open("README.md") as fp:
    long_description = fp.read()


setuptools.setup(
    name="sqs_lambda_to_batch_fargate",
    version="0.0.1",

    description="A CDK Python app for demonstration purposes",
    long_description="Use messages published to SQS queue to trigger Lambda function which invokes AWS Batch",
    long_description_content_type="text/markdown",

    author="AWS SDK Code Examples",

    package_dir={"": "sqs_lambda_to_batch_fargate"},
    packages=setuptools.find_packages(where="sqs_lambda_to_batch_fargate"),

    python_requires=">=3.7",

    classifiers=[
        "Intended Audience :: Developers",
        "License :: OSI Approved :: Apache Software License",
        "Programming Language :: JavaScript",
        "Programming Language :: Python :: 3 :: Only",
        "Programming Language :: Python :: 3.6",
        "Programming Language :: Python :: 3.7",
        "Programming Language :: Python :: 3.8",
        "Topic :: Software Development :: Code Generators",
        "Topic :: Utilities",
        "Typing :: Typed",
    ],
)
