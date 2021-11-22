# Scripts

The scripts contained in this module are primarily for internal use by the AWS
Code Examples team.

## Cleanup report

### Purpose

Reads cleanup metadata and writes a report of files cleaned up vs. files still
needing cleanup. A cleaned file contains code that has been brought up to coding
standard, has been tested, and has at least minimal comments. To include a file
in the cleaned report, list it in a metadata.yaml file somewhere in the repo.

### Prerequisites

To run this script, you must have the following installed globally or in a virtual
environment:
 
* Python 3.6 or later
* PyYaml 5.3 or later
* PyTest 5.3.5 or later (to run unit tests)

### Running the script

The typical usage of this script is to determine the cleanup coverage in this
GitHub repository. To generate a CSV-formatted report of cleanup coverage, in a command
window at the root folder of the repository, run the following.

```
python -m scripts.cleanup_report
``` 

Or output the report to a file.

```
python -m scripts.cleanup_report --report=c:\reports\cleanliness.csv
```

Or run the script against a subfolder and include dirty files, which can be useful 
for testing new metadata files.

```
python -m scripts.cleanup_report --root python/example_code/sqs --dirty
```

### Running the tests

To run the unit tests associated with this script, in a command window at the 
`scripts\tests` folder of the repository, run `python -m pytest test_cleanup_report.py`.


## Checkin tests

### Purpose

The checkin tests are run whenever a pull request is submitted or changed 
(using Travis CI, configured in .travis.yml).

The checkin tests walk the full repository and scan code files to look for 
the following issues.

* Disallow a list of specified words.
* Disallow any 20- or 40- character strings that fit a specified regex profile
  that indicates they might be secret access keys. Allow strings that fit the
  regex profile if they are in the allow list.
* Disallow file names that contain 20- or 40- character strings that fit the same
  regex profile, unless the filename is in the allow list.
* Verify that snippet-start and snippet-end tags are in matched pairs. You are
  not required to include these tags, but if you do they must be in pairs.
  
A count of errors found is returned. When Travis CI receives a non-zero return code,
it treats the checks as failed and displays a message in the pull request.

### Prerequisites

To run this script, you must have the following installed globally or in a virtual
environment:
 
* Python 3.6 or later
* PyTest 5.3.5 or later (to run unit tests)

### Running the script

The typical usage of this script is to check for certain disallowed strings and
verify matched snippet tags when submitting a pull request. You can run the script
manually by running the following in the root folder of your GitHub clone. 

```
python -m scripts.checkin_tests
``` 

The script can also be used to verify individual folders. If you want to verify
just the files in a specific folder and its subfolders, use the `--root` option.
For example, to scan just the `file_transfer` folder in the Python S3 example section,
run the following command. 

```
python -m scripts.checkin_tests --root python/example_code/s3/file_transfer 
``` 

To suppress most output, add the `--quiet` option.

### Running the tests

To run the unit tests associated with this script, in a command window at the 
`scripts\tests` folder of the repository, run `python -m pytest test_checkin_tests.py`.

## README summarizer

Gathers titles and purpose statements from README.md files and generates a top-level
README.md file at the specified root. This README lists all of the examples and 
their purposes. Any existing README.md file at the root is overwritten.

This script is currently intended for use only with the Python subsection of the
repo, but could be expanded in the future.

### Prerequisites

To run this script, you must have the following installed globally or in a virtual
environment:
 
* Python 3.6 or later

### Running the script

Run the following in the root folder of your GitHub clone. 

```
python -m scripts.summarizer --root python
``` 

## testRust.sh

1. Updates the SDK crate versions in the **Cargo.toml** files within the local clone of the [aws-doc-sdk-examples](https://github.com/awsdocs/aws-doc-sdk-examples) repository.
1. Runs **cargo clippy**
1. Runs **cargo fmt**

### Prerequisites

To run this script, you must have the following installed:

* bash

You must also set the following environment variables:

- ``RustRoot`` is the fully-qualified path to the **rust_dev_preview** directory on your computer.
- ``FromVersion`` is the version number of the previous SDK crates.
- ``ToVersion`` is the version number of the SDK crates for the current release.

If you are testing the existing release, set both **FromVersion** and **ToVersion** to the same value
(**0.0.25-alpha** as of when this script was first checked in).

### Running the script

The script does not take any command-line arguments; 
you can run it from any directory on your computer.

## vetCDK.sh

### Purpose

Validates all of the CDK apps in the in the 
*resources/cdk* directory.

### Prerequisites

To run this script, you must have the following installed:

* bash

### Running the script

Run the script from the root of your cloned
*aws-doc-sdk-examples* repo:

```
./scripts/vetCDK.sh
```

---
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
