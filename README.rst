.. Copyright 2010-2020 Amazon.com, Inc. or its affiliates. All Rights Reserved.

   This work is licensed under a Creative Commons Attribution-NonCommercial-ShareAlike 4.0
   International License (the "License"). You may not use this file except in compliance with the
   License. A copy of the License is located at http://creativecommons.org/licenses/by-nc-sa/4.0/.

   This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
   either express or implied. See the License for the specific language governing permissions and
   limitations under the License.
Welcome
=======
This is the repository for code samples used in the public 
`AWS documentation <https://docs.aws.amazon.com>`_. The examples use the AWS Software Developer 
Kits (SDKs) for the supported programming languages. For more information, see `Tools to Build on 
AWS <https://aws.amazon.com/getting-started/tools-sdks/>`_.

Finding code examples
=====================

The code examples are organized by programming language. For instance, all of the examples for the
`AWS SDK for Java Developer Guide <https://www.github.com/awsdocs/aws-java-developer-guide>`_ are
kept in the `java <java>`_ directory.

You can also try using the preview of our `use case index of examples <code-index.md>`_ (and let us
know what you think about it).

Building and running code examples
==================================

Inside each of the language-specific directories, you'll find a **README** file that explains how to
build and run the examples contained within it.

The example code inside the language-specific directories is organized by
the AWS service abbreviation ("s3" for `Amazon S3 <https://aws.amazon.com/s3>`_ examples, and so
on).

Proposing new code examples
===========================

To propose a new code example for the AWS documentation team to consider working on, `create a new 
request <https://github.com/awsdocs/aws-doc-sdk-examples/issues/new?assignees=&labels=code+sample+request&template=request-new-code-example.md&title=%5BNEW+EXAMPLE+REQUEST%5D+%3C%3CProvide+a+title+for+this+proposal%3E%3E>`_.

The AWS documentation team is looking to produce code examples that cover broader scenarios and use 
cases, versus simple code snippets that cover only individual API calls.

From time to time, the AWS documentation team will select some of these proposals to begin working on.
To view their decisions, see the `code examples roadmap <https://github.com/awsdocs/aws-doc-sdk-examples/projects/2>`_. 
If you feel strongly about seeing the timeline of a particular proposal accelerated or the scope of a 
particular proposal expanded or focused, be sure to +1 the related issue, add comments to it, or both,
and the team will take this under consideration. 

The AWS documentation team typically moves accepted proposals in the roadmp from the **Wish List** 
stage to the **Backlog** stage, then to the **In Progress** stage, and finally to the **Recently 
Completed** stage. The **Wish List** stage represents proposals that the team *might* begin working
on at some point in the future but with no expected timeline, while the **Backlog** stage represents 
proposals that the team will *likely* begin working on soon. To view the team's progress, see the 
`code examples roadmap <https://github.com/awsdocs/aws-doc-sdk-examples/projects/2>`_.

Submitting code examples for use in AWS documentation
=====================================================

If you plan to contribute examples for use in the documentation (the purpose of this repository),
please read this section carefully so that we can work together more effectively. 
For process instructions and additional guidance, see the `Guidelines for contributing <CONTRIBUTING.md>`_. 

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

All content in this repository, unless otherwise stated, is 
Copyright © 2010-2020, Amazon Web Services, Inc. or its affiliates. All rights reserved.

Except where otherwise noted, all examples in this collection are licensed under the `Apache
license, version 2.0 <https://www.apache.org/licenses/LICENSE-2.0>`_ (the "License"). The full
license text is provided in the ``LICENSE`` file accompanying this repository.
