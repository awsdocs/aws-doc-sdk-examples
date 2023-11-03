# Scripts

The scripts contained in this module are primarily for internal use by the AWS
Code Examples team.

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
python -m .github/pre_validate/pre_validate.py
``` 

The script can also be used to verify individual folders. If you want to verify
just the files in a specific folder and its subfolders, use the `--root` option.
For example, to scan just the `file_transfer` folder in the Python S3 example section,
run the following command. 

```
python -m .github/pre_validate/pre_validate.py --root python/example_code/s3/file_transfer 
``` 

To suppress most output, add the `--quiet` option.

### Running the tests

To run the unit tests associated with this script, in a command window at the 
`.github/pre_validate` folder of the repository, run `python -m pytest test_pre_validate.py`.

---
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
