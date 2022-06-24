# SwiftUtilities

The _SwiftUtilities_ package provides various tools and utility functions
that are useful in sample code and in tests for those examples.

The included utility functions are:

## String

The `String` class is extended with the following functions:

### `uniqueName(withPrefix:withExtension:isValid) -> String`

Generates a guaranteed-unique name which can be used for anything from unique
strings in an array to names for files, S3 buckets, data objects, or any
other named object.

#### Parameters

* `withPrefix`: A string used at the beginning of the specified name. By
  default, no prefix is applied.

* `withExtension`: A string specifying an extension to add to the end of the
  name, for file names. Do not include the period (".") in the string; it is
  added for you. By default, no extension is added.

* `isValid`: A Boolean indicating whether or not the generated name should be a
  valid one. For the purposes of this function, it is assumed that a valid name
  cannot start with a comma (","), and should not include either numbers or the
  percent ("%") symbol. Between these, the resulting string should usually be
  invalid for most use cases. By default, this is `false`, which causes a valid
  name to be returned.

### Return value

A `String` containing the generated unique name. The name is in fact a UUID,
though possibly altered based on the provided options.

### `withLoremText(paragraphs:) -> String`

Returns a Lorem Ipsum style string containing the specified number of randomly-generated
paragraphs. If `paragraphs` is not specified, then 5 paragraphs of Lorem Ipsum
text are returned. Each paragraph is between four and eight sentences long, and
each sentence contains between 3 and 9 words.

This text is suitable for use in test files and strings so that tests and
examples don't need to contain strings that may need translation or may take up
space unnecessarily.

#### Parameters

* `paragraphs`: The number of paragraphs to generate. If not specified, the
  resulting string contains five paragraphs.

#### Return value

A `String` containing up to `paragraphs` paragraphs of Lorem Ipsum style
generated text.

## Testing

This package includes automated tests for each function. Before you run the
tests, make sure you configure your environment to allow access to AWS and
[download and install](https://www.swift.org/download/) the Swift tools if you don't already have them.

To run the tests, make sure you have Swift installed, then issue the command
`swift test` to run the tests.

```
$ cd SwiftUtilities
$ swift test
```

The tests will compile and run, outputting the results to the console. You can
also run the tests in Xcode if you prefer.

---

**_Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved._**  
_SPDX-License-Identifier: Apache-2.0_
