# Extract Snippets for GitHub Actions

Jerry Kindall, Amazon Web Services  
Last updated 24-Jun-2021

## What it is

This is a suite of GitHub Actions workflows with supporting scripts that
extracts code snippets from source code files, which can then be used in
documentation via include directives.  When the code changes, the snippets are
automatically extracted again, and the documentation will pick them up on the
next build.

There are two separate workflows: 

* Extract Snippets (`extract-snippets.yaml`): Extracts snippets from all source
  files in the repo.  Runs on a commit to the main or master branch; can also be
  run manually.

* Extract Snippets Dry Run (`extract-snippets-dryrun.yml`): Extracts snippets
  from all source files in a pull request but does not check in any snippets;
  meant to validate PRs.  Displays only the issue report (problems found in the
  extraction process).  Can also be run manually.

To prevent the introduction of consistency problems in the snippets (e.g.
duplicate snippet filenames with different content), all files in the repo are
always processed.  This is not noticeably slower than e.g. processing only the
files in a given commit; the overhead of the action setup and Git commands
overshadows the run time of the actual snippet extraction.

## Compared to other snippet extraction tools

The AWS Docs organization has a tool which it uses to extract snippets from
source files.  Compared to that tool, this tool has the following additional
features:

* Runs on GitHub so snippets can be automatically updated on every commit.
* Includes snippet-append and snippet-echo tags (see below).
* Can dedent (remove indentation from) extracted snippets.
* Checks for and logs a variety of problems, including conflicting snippet
  tags (same tag in multiple files).
* Besides a processing log, produces a report of files with problems and an
  index mapping snippet tags back to the file(s) that contain them.

It does not have the following features of the AWS Docs tool:

* Extract metadata from snippets for use in catalogs.  The metadata tags are
  recognized, but do not do anything, in this snippet extractor.
  
## Snippet tags

Snippet tags are special single-line comments in source files.  They must not
follow any code on their line and must begin with the language's single-line
comment marker (`//` in many languages, `#` in some others).  If a language does
not have a single-line comment marker, the block comment delimiter may be used,
but should be closed on the same line following the snippet tag.  The snippet
tag is followed by the snippet directive, a colon, and an argument in square
brackets.  Whitespace is permitted (but optional) between the comment marker and
the snippet directive. For example:

<tt>// snippet&#45;start:[cdk.typescript.widget_service]</tt>

Here, the directive begins the extraction of a code snippet to the filename
specified, with a `.txt` extension.

The main tags used in our repos are `snippet-start` and `snippet-end`.  Each
`snippet-start` requires a matching `snippet-end` (specifying the same snippet
name) in the same source file.  Multiple snippets may be extracted from one
source file, and may overlap.  The snippet tags do not appear in the extracted
snippets.

The following tags are unique to this extractor (they are not supported by the
snippet extractor used by the AWS Docs team).

* `snippet-append`: Extracts additional source code to a snippet file that has
  already been created by a previous `snippet-start` directive, stopping at
  `snippet-end` as with `snippet-start`.

* `snippet-echo`: Writes the argument literally to the snippet(s) currently
  being extracted.  Useful for adding closing braces etc. when extracting a
  partial code block.  Whitespace is stripped from the right of the argument but
  not the left, so you can match indentation.

Also unique to this extractor, `snippet-start` supports an optional number
following the closing bracket.  

<tt>// snippet&#45;start:[my-snippet] 8</tt>

If this number is present, that many spaces are removed from the beginning of
each line of the snippet, allowing snippets to be dedented (have indentation
removed), so their left margin is decreased.  Each snippet, even overlapping
snippets, has its own dedent level.  If you use `snippet-append`, it uses the
same dedent specified on `snippet-start`.  Dedent does not affect
`snippet-echo`, so provide the desired indentation yourself.

This extractor also recognizes the following tags (i.e. they are not errors),
but does not do anything with them.  They are supported for compatibility with
source files tagged for the original AWS Docs extractor, which can register
metadata about each snippet.

* `snippet-keyword`
* `snippet-service`
* `snippet-sourceauthor`
* `snippet-sourcedate`
* `snippet-sourcedescription`
* `snippet-sourcesyntax`
* `snippet-sourcetype`

## extract-snippets.sh

This `bash` script calls the Python script (described next) to extract the
snippets, then checks the results in to the `snippets` branch of the repo. If
the script is passed any argument (value is irrelevant), it exits after
extracting the snippets without adding them to the repo ("dry run" mode).

## extract-snippets.py

This script reads from standard input the paths of the files containing the
snippets to be extracted.  It ignores non-source files, hidden files, and files
in hidden directories (it is not necessary to filter out such files beforehand).
The script's required argument is the directory that the snippets should be
extracted into.  This directory must not contain any files named the same as a
snippet being extracted.

For example, the following command runs the script on source files in the
current directory, extracting snippets also into the current directory.

```
ls | python3 extract-snippets.py .
```

Both Windows and Linux-style paths are supported so you can test the script on
Windows during development.

The supported source file formats are stored in `snippet-extensions.yml` or
another file specified as the second command-line argument. This file is a YAML
map of filename extensions to comment markers.  If a language supports more than
one line comment marker, you can provide them separated by whitespace in a
single string:

```
.php: "# //"
```

If a language does not support a line comment marker (e.g. C), you can specify
its starting block comment marker.  However, the extraction process does not
include the lines with the snippet tags in the snippets, so you should include
the closing block comment marker on the same line to avoid the closing marker
being included in the snippet.  For example:

<tt>/* snippet&#45;start:[terry.riley.in-c] */</tt>

Not:

<tt>/* snippet&#45;start:[terry.riley.in-c]</tt><br/>
<tt>*/</tt>

Some languages support both line and block comments.  In this case, we suggest
you always use the line comment marker for snippet tags.

You may pass a different YAML (or JSON) file as the script's second argument --
for example, the provided `snippet-extensions-more.yml`, which contains a more
extensive map of source formats.  Note that if you specify only a filename, the
file of that name *in the same directory as the script* (not in the working
directory!) is used.  To specify a file in the current directory, use `./`, e.g.
`./my-snippet-extensions.yml`.

The keys in `snippet-extensions.yml` are matched case-sensitively at the end of
file paths, and can be used to match more than extensions.  If you wanted to
extract snippets from makefiles, for example, you could add to the mapping:

```
/makefile: "#"
```

The slash makes it match the complete filename: i.e., there is a directory
separator, then "makefile", at the end of the path.  Always use `/` for this
purpose even if you are using Windows paths with backslashes; paths are
normalized to use slashes before this comparison.

If a given path could match more than one language, the first one listed in the
extension file wins.

To match all files, use `""` as the key (after all, there's an empty string at
the end of every path -- in fact, infinitely many of them).  You probably
shouldn't do this, but you *can.*  It might be useful as a catch-all in a repo
where you want to process all files and most languages in the repo use the same
comment marker.  It should go last in the extensions file.

To exclude a file or files from being processed, specify the end of its path and
an empty string as the comment marker.  Such items should appear earlier in the
file than others that might match, since the first match wins.

```
"/lambda/widgets.js": ""
```

The output of `extract-snippets.py` is a list of the source files processed.
Indented under each source file is a list of the snippets extracted from it, if
any, notated with EXTRACT.  APPEND operations, errors, and warnings are also
flagged in similar fashion under the source file.  

At the end of the run, a summary line displays the number of unique snippets
extracted and the number of source files examined.  This is followed by a report
of all files with issues, and an index that maps snippets back to the files that
contain them.

## Errors

The following situations are considered problematic to varying degrees.  To the
extent possible, errors do not stop processing.

* Unrecognized snippet tag (see earlier section for supported tags).

* Text decoding error.  By default, source files are assumed to be UTF-8. To
  change the encoding used, sent the environment variable `SOURCE_ENCODING` to
  `utf16` or another encoding.  Use the Python name, which you can find here:

  https://docs.python.org/3/library/codecs.html#standard-encodings

  Generally you'd do this in the action file, not in the `bash` script.  Like:

```yaml
    # goes under the `steps` key
    env:
        SOURCE_ENCODING: utf-16
```

  If a file cannot be read, processing continues with the next file, if any.

* `snippet-start` for a snippet file that has already been extracted, *unless*
  the source file has the same filename and contains exactly the same code. This
  behavior supports multiple examples that contain the same source code for an
  incorporated Lambda function or other asset, where that code contains
  snippets. The former situation is an error, the latter a warning.

* `snippet-end` with no corresponding `snippet-start` or `snippet-append` in the
  same source file.

* Missing `snippet-end` corresponding to a `snippet-start` or `snippet-append`.

* `snippet-append` with no corresponding `snippet-start` in the same source file
  (you can't append to snippets created in a different source file since there's
  no guarantee the files will be processed in any particular order, or that all
  files will even be processed, leading to consistency issues).

* `snippet-echo` outside of a snippet.

* Insufficient whitespace at the beginning of a line to dedent it as required.

* Any source file contains both a tag and a tab character (ASCII 9), as
  indenting by tab is not well-supported in documentation.  This is a warning,
  and will not on its own stop extracted snippets from being checked in.

If there is at least one error, none of the extracted snippets will be checked
in.  Warnings *do not* prevent snippets from being checked in.

# README-SNIPPETS.txt

This text file is copied into the snippets directory as README.txt and should
provide information that users of the snippets should know.

# Version history

* v1.0.0 - Initial pull request against the CDK Examples repo.
* v1.1.0 - Test against SDK team's examples repo and fix the problems found.
           Continue processing after errors instead of stopping at the first.
           Generate report of files with issues.
           Generate index of files containing each snippet.
           Other fixes and tweaks.
* v1.2.0 - Allow log, issue report, and index to be selectively enabled.
           Don't try to call non-method attributes to handle snippet tags.
