.. Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.

   This work is licensed under a Creative Commons Attribution-NonCommercial-ShareAlike 4.0
   International License (the "License"). You may not use this file except in compliance with the
   License. A copy of the License is located at http://creativecommons.org/licenses/by-nc-sa/4.0/.

   This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
   either express or implied. See the License for the specific language governing permissions and
   limitations under the License.

This repository contains AWS SDK code examples used in the public `AWS documentation repositories
<https://www.github.com/awsdocs>`_.

About the examples
==================

The code examples are organized by programming language. For instance, all of the examples for the
`AWS SDK for Java Developer Guide <https://www.github.com/awsdocs/aws-java-developer-guide>`_ are
kept in the `java <java>`_ directory.

Building and running examples
-----------------------------

Inside each of the language-specific directories, you'll find a **README** file that explains how to
build and run the examples contained within it.

The example code inside the language-specific directories is organized by
the AWS service abbreviation ("s3" for `Amazon S3 <https://aws.amazon.com/s3>`_ examples, and so
on).

Submitting code examples for use in AWS documentation
=====================================================

If you plan to contribute examples for use in the documentation (the purpose of this repository),
please read this section carefully so that we can work together more effectively.

* **Make sure that the code can be built and run**. There's nothing more frustrating in developer
  documentation than code examples that don't work. Build the code and test it before submitting it!

* **Do not include personal account data, keys or IDs in your examples**. Code should obtain access
  keys from the standard SDK credentials and config files, use environment variables or external
  data files, or query the user for this information.

* **Format code lines to 80 characters**. Long lines can be enclosed in a scrollable box for HTML,
  but in a PDF build, long lines will often spill off the side of the page, making the code
  unreadable. If your code includes long text strings, consider breaking these into smaller chunks
  and concatenating them together.

* **Use short(er) variable names**. To aid in readability and to help keep line length down, use
  *short yet descriptive* names for variables. Do *not* simply mimic class names when creating
  variables that represent an object of that class. It nearly always results in excessively long
  variable names, making it difficult to keep code lines within 80 characters.

* **Use spaces, not tabs, for indentation**. Tabs are variable-length in most editors, but will
  usually render as 8 characters wide in printed documentation. *Always use spaces* to ensure
  consistent formatting in printed code.

  You can ignore this rule for makefiles, which may *require* the use of tabs, but these are
  typically only used for building examples, and are not included in documentation.

* **Minimize the use of comments**. Code is ignored for translation, so comments in code are not
  translated for the printed documentation's target language. Comments should not be needed in most
  code used for documentation, since the goal is clarity and ease of understanding. By making code
  self-explanatory, you'll make better code for documentation and reduce the need to add comments.

* **Place comments on separate lines from code**. If you *must* add a comment for explanation or any
  other purpose, make sure that it's placed on a separate line from code (*not* inline). This
  allows readers of the source file to read the comment, yet it can be stripped out when including
  snippets from the file within documentation.

* **All code must be submitted under the Apache 2.0 license**, as noted in the following **Copyright
  and License** section.

Copyright and License
=====================

All content in this repository, unless otherwise stated, is Copyright © 2010-2019, Amazon Web
Services, Inc. or its affiliates. All rights reserved.

Except where otherwise noted, all examples in this collection are licensed under the `Apache
license, version 2.0 <https://www.apache.org/licenses/LICENSE-2.0>`_ (the "License"). The full
license text is provided in the ``LICENSE`` file accompanying this repository.
