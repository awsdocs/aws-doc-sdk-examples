.. Copyright 2010-2016 Amazon.com, Inc. or its affiliates. All Rights Reserved.

   This work is licensed under a Creative Commons Attribution-NonCommercial-ShareAlike 4.0
   International License (the "License"). You may not use this file except in compliance with the
   License. A copy of the License is located at http://creativecommons.org/licenses/by-nc-sa/4.0/.

   This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
   either express or implied. See the License for the specific language governing permissions and
   limitations under the License.

####################
aws-doc-sdk-examples
####################

This repository contains AWS SDK examples used in the public `AWS documentation repositories
<https://www.github.com/awsdocs>`_.

.. note:: To build any of these documentation sets, download one of these repositories and run the
   ``build_docs.py`` script that's provided in the repository. The script will automatically merge
   this shared content into the build directory before building with Sphinx.

About the examples
==================

The SDK examples are organized by programming language. For instance, all of the examples for the
`AWS SDK for Java Developer Guide <https://www.github.com/awsdocs/aws-java-developer-guide>`_ are
kept in the `java <java>`_ directory.

Building and running examples
-----------------------------

Within each of the language-scoped directories, you'll find a **README** file that explains how to
build and run the examples contained within it.

The example code itself is present in the ``example_code`` subdirectory, and is organized further by
the AWS service abbreviation ("s3" for `Amazon S3 <https://aws.amazon.com/s3>`_ examples, and so on).


How examples are used in the documentation
==========================================

Examples are automatically included within each developer guide by ``build_docs.py`` (present in
each documentation project), which clones this repository and then copies files from the appropriate
``example_code`` directory into the ``doc_build`` directory prior to building.

Within a guide's source-files, examples are referenced using Sphinx's `literalinclude
<http://www.sphinx-doc.org/en/stable/markup/code.html#includes>`_ directive. For example::

   .. literalinclude:: example_code/s3/src/main/java/aws/example/s3/CreateBucket.java
      :lines: 43-50
      :dedent: 8


Copyright and License
=====================

All content in this repository, unless otherwise stated, is Copyright © 2010-2016, Amazon Web
Services, Inc. or its affiliates. All rights reserved.

Except where otherwise noted, all examples in this collection are licensed under the `Apache
license, version 2.0 <http://www.apache.org/licenses/LICENSE-2.0>`_ (the "License"). The full
license text also provided in the LICENSE file accompanying this repository.

