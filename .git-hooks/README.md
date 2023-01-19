# AWS SDK Code Examples - Git hooks

This repository is configured with [git hooks](https://git-scm.com/docs/githooks). These hooks
run automated code-quality checks like unit tests and linters.

## Installation

By default, git checks `.git/hooks` for existing hooks. This repository hosts hooks
in a different location so they can be version controlled. Configure git to use these
hooks by running `git config core.hooksPath .git-hooks`.

## Usage

A hook is triggered when its corresponding event takes place. For example,
the "pre-commit" hook is triggered when a user initializes a commit, but before
that commit is created in the git history.

Hooks can be added to the `.git-hooks` folder. For a list of valid hooks, see https://git-scm.com/docs/githooks.

## Existing hooks

### [pre-commit](./pre-commit)

This hook will always run the metadata check. Once the metadata check has passed, it will look in each child
directory (non-recursive) for the following path: `hook_scripts/pre-commit.sh`.

If that file is found, and is executable, it will be run.
[The javascriptv3](../javascriptv3/hook_scripts/pre-commit.sh) has an example script that will be run on commit
if changes were made in the javascriptv3 directory.

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
