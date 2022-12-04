#!/usr/bin/env python3

# Original copyright (c) 2020 Peter Hill
# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX - License - Identifier: Apache - 2.0

# The original source was part of the GitHub action Clang Tidy Review, https://github.com/ZedThree/clang-tidy-review.
# This is an alteration of Clang Tidy Review which posts to annotations instead of pull-request
# comments.  It also is enabled for workflows.
# This code also filters some errors to deal with issues particular to AWS SDK sample code.
# Installation of the aws-cpp-sdk is not practical at the moment for a lint test in GitHub.
# Because the sdk is missing, the review had to be modified to remove verbose false negatives.

import argparse
import contextlib
import datetime
import itertools
import fnmatch
import json
import os
from operator import itemgetter
import pathlib
import re
import requests
import subprocess
import textwrap
import unidiff
import yaml
from github import Github
from typing import List

BAD_CHARS_APT_PACKAGES_PATTERN = "[;&|($]"
DIFF_HEADER_LINE_LENGTH = 5
FIXES_FILE = "clang_tidy_review.yaml"
HAS_COMPILE_COMMANDS = "HAS_COMPILE_COMMANDS"
AWS_DOCS_ACCOUNT = "awsdocs"


class Commit:

    def __init__(self, pr_repo: str, head_ref: str, token: str):
        self.pr_repo = pr_repo  # account/repository
        self.head_ref = head_ref
        self.token = token

    def headers(self, media_type: str):
        return {
            "Accept": f"application/vnd.github.{media_type}",
            "Authorization": f"token {self.token}",
        }

    @property
    def base_url_compare(self):
        return f"https://api.github.com/repos/{self.pr_repo}/compare"

    def get_for_compare(self, media_type: str, extra: str = "") -> str:
        url = f"{self.base_url_compare}{extra}"

        response = requests.get(url, headers=self.headers(media_type))
        response.raise_for_status()
        return response.text

    def get_commit_diff(self) -> List[unidiff.PatchSet]:
        """Download the compare diff, return a list of PatchedFile"""
        diffs = self.get_for_compare("diff", f"/main...{self.head_ref}")

    # PatchSet is the easiest way to construct what we want, but the
    # diff_line_no property on lines is counted from the top of the
    # whole PatchSet, whereas GitHub is expecting the "position"
    # property to be line count within each file's diff. So we need to
    # do this  bit of faff to get a list of file-diffs with
    # their own diff_line_no range
        diff = [unidiff.PatchSet(str(file))[0] for file in unidiff.PatchSet(diffs)]

        return diff


class PullRequest:
    """Add some convenience functions not in PyGithub"""

    def __init__(self, repo: str, pr_number: int, token: str):
        self.repo = repo
        self.pr_number = pr_number
        self.token = token

        github = Github(token)
        repo_object = github.get_repo(f"{repo}")

        self._pull_request = repo_object.get_pull(pr_number)

    def headers(self, media_type: str):
        return {
            "Accept": f"application/vnd.github.{media_type}",
            "Authorization": f"token {self.token}",
        }

    @property
    def base_url(self):
        return f"https://api.github.com/repos/{self.repo}/pulls/{self.pr_number}"

    def get(self, media_type: str, extra: str = "") -> str:
        url = f"{self.base_url}{extra}"

        response = requests.get(url, headers=self.headers(media_type))
        response.raise_for_status()
        return response.text

    def get_pr_diff(self) -> List[unidiff.PatchSet]:
        """Download the PR diff, return a list of PatchedFile"""

        diffs = self.get("v3.diff")

        # PatchSet is the easiest way to construct what we want, but the
        # diff_line_no property on lines is counted from the top of the
        # whole PatchSet, whereas GitHub is expecting the "position"
        # property to be line count within each file's diff. So we need to
        # do this bit of faff to get a list of file-diffs with
        # their own diff_line_no range
        diff = [unidiff.PatchSet(str(file))[0] for file in unidiff.PatchSet(diffs)]
        return diff


@contextlib.contextmanager
def message_group(title: str):
    print(f"::group::{title}", flush=True)
    try:
        yield
    finally:
        print("::endgroup::", flush=True)


def make_file_line_lookup(diff):
    """Get a lookup table for each file in diff, to convert between source
    line number to line number in the diff

    """
    lookup = {}
    for file in diff:
        filename = file.target_file[2:]
        lookup[filename] = {}
        for hunk in file:
            for line in hunk:
                if line.diff_line_no is None:
                    continue
                if not line.is_removed:
                    lookup[filename][line.target_line_no] = (
                        line.diff_line_no - DIFF_HEADER_LINE_LENGTH
                    )
    return lookup


def make_file_offset_lookup(filenames):
    """Create a lookup table to convert between character offset and line
    number for the list of files in `filenames`.

    This is a dict of the cumulative sum of the line lengths for each file.

    """
    lookup = {}

    for filename in filenames:
        with open(filename, "r") as file:
            lines = file.readlines()
        # Length of each line
        line_lengths = map(len, lines)
        # Cumulative sum of line lengths => offset at end of each line
        lookup[os.path.abspath(filename)] = [0] + list(
            itertools.accumulate(line_lengths)
        )

    return lookup


def get_diagnostic_file_path(clang_tidy_diagnostic, build_dir):
    
    # Sometimes, clang-tidy gives us an absolute path, so everything is fine.
    # Sometimes however it gives us a relative path that is relative to the
    # build directory, so we prepend that.
    
    # Modern clang-tidy
    if ("DiagnosticMessage" in clang_tidy_diagnostic) and ("FilePath" in clang_tidy_diagnostic["DiagnosticMessage"]):
        file_path = clang_tidy_diagnostic["DiagnosticMessage"]["FilePath"]
        if file_path == "":
            return ""
        elif os.path.isabs(file_path):
            return os.path.normpath(os.path.abspath(file_path))
        else:
            # Make the relative path absolute with the build dir
            if "BuildDirectory" in clang_tidy_diagnostic:
                return os.path.normpath(os.path.abspath(os.path.join(clang_tidy_diagnostic["BuildDirectory"],
                                                                     file_path)))
            else:
                return os.path.normpath(os.path.abspath(file_path))
      
    # Pre-clang-tidy-9 format
    elif "FilePath" in clang_tidy_diagnostic:
        file_path = clang_tidy_diagnostic["FilePath"]
        if file_path == "":
            return ""
        else:
            return os.path.normpath(os.path.abspath(os.path.join(build_dir, file_path)))
        
    else:
        return ""


def find_line_number_from_offset(offset_lookup, filename, offset):
    """Work out which line number `offset` corresponds to using `offset_lookup`.

    The line number (0-indexed) is the index of the first line offset
    which is larger than `offset`.

    """
    name = str(pathlib.Path(filename).resolve().absolute())

    if name not in offset_lookup:
        # Let's make sure we've the file offsets for this other file
        offset_lookup.update(make_file_offset_lookup([name]))

    for line_num, line_offset in enumerate(offset_lookup[name]):
        if line_offset > offset:
            return line_num - 1
    return -1


def read_one_line(filename, line_offset):
    """Read a single line from a source file"""
    # Could cache the files instead of opening them each time?
    with open(filename, "r") as file:
        file.seek(line_offset)
        return file.readline().rstrip("\n")


def collate_replacement_sets(diagnostic, offset_lookup):
    """Return a dict of replacements on the same or consecutive lines, indexed by line number

    We need this as we have to apply all the replacements on one line at the same time

    This could break if there are replacements in with the same line
    number but in different files.

    """

    # First, make sure each replacement contains "LineNumber", and
    # "EndLineNumber" in case it spans multiple lines
    for replacement in diagnostic["Replacements"]:
        # It's possible the replacement is needed in another file?
        # Not really sure how that could come about, but let's
        # cover our behinds in case it does happen:
        if replacement["FilePath"] not in offset_lookup:
            # Let's make sure we've the file offsets for this other file
            offset_lookup.update(make_file_offset_lookup([replacement["FilePath"]]))

        replacement["LineNumber"] = find_line_number_from_offset(
            offset_lookup, replacement["FilePath"], replacement["Offset"]
        )
        replacement["EndLineNumber"] = find_line_number_from_offset(
            offset_lookup,
            replacement["FilePath"],
            replacement["Offset"] + replacement["Length"],
        )

    # Now we can group them into consecutive lines
    groups = []
    for index, replacement in enumerate(diagnostic["Replacements"]):
        if index == 0:
            # First one starts a new group, always
            groups.append([replacement])
        elif (
            replacement["LineNumber"] == groups[-1][-1]["LineNumber"]
            or replacement["LineNumber"] - 1 == groups[-1][-1]["LineNumber"]
        ):
            # Same or adjacent line to the last line in the last group
            # goes in the same group
            groups[-1].append(replacement)
        else:
            # Otherwise, start a new group
            groups.append([replacement])

    # Turn the list into a dict
    return {g[0]["LineNumber"]: g for g in groups}


def replace_one_line(replacement_set, line_num, offset_lookup):
    """Apply all the replacements in replacement_set at the same time"""

    filename = replacement_set[0]["FilePath"]
    # File offset at the start of the first line
    line_offset = offset_lookup[filename][line_num]

    # List of (start, end) offsets from line_offset
    insert_offsets = [(0, 0)]
    # Read all the source lines into a dict, so we only get one copy of
    # each line, though we might read the same line in multiple times
    source_lines = {}
    for replacement in replacement_set:
        start = replacement["Offset"] - line_offset
        end = start + replacement["Length"]
        insert_offsets.append((start, end))

        # Make sure to read any extra lines we need too
        for replacement_line_num in range(
            replacement["LineNumber"], replacement["EndLineNumber"] + 1
        ):
            replacement_line_offset = offset_lookup[filename][replacement_line_num]
            source_lines[replacement_line_num] = (
                read_one_line(filename, replacement_line_offset) + "\n"
            )

    # Replacements might cross multiple lines, so squash them all together
    source_line = "".join(source_lines.values()).rstrip("\n")

    insert_offsets.append((None, None))

    fragments = []
    for (_, start), (end, _) in zip(insert_offsets[:-1], insert_offsets[1:]):
        fragments.append(source_line[start:end])

    new_line = ""
    for fragment, replacement in zip(fragments, replacement_set):
        new_line += fragment + replacement["ReplacementText"]

    return source_line, new_line + fragments[-1]


def format_ordinary_line(source_line, line_offset):
    """Format a single C++ line with a diagnostic indicator"""

    return textwrap.dedent(
        f"""\
         ```cpp
         {source_line}
         {line_offset * " " + "^"}
         ```
         """
    )


def format_diff_line(diagnostic, offset_lookup, line_num):
    """Format a replacement as a GitHub suggestion or diff block"""

    end_line = line_num

    # We're going to be appending to this
    code_blocks = ""

    replacement_sets = collate_replacement_sets(diagnostic, offset_lookup)

    for replacement_line_num, replacement_set in replacement_sets.items():
        old_line, new_line = replace_one_line(
            replacement_set, replacement_line_num, offset_lookup
        )

        print(f"----------\n{old_line=}\n{new_line=}\n----------")

        # If the replacement is for the same line as the
        # diagnostic (which is where the comment will be), then
        # format the replacement as a suggestion. Otherwise,
        # format it as a diff
        if replacement_line_num == line_num:
            code_blocks += f"""
```suggestion
{new_line}
```
"""
            end_line = replacement_set[-1]["EndLineNumber"]
        else:
            # Prepend each line in the replacement line with "+ "
            # in order to make a nice diff block. The extra
            # whitespace is so the multiline dedent-ed block below
            # doesn't come out weird.
            whitespace = "\n                "
            new_line = whitespace.join([f"+ {line}" for line in new_line.splitlines()])
            old_line = whitespace.join([f"- {line}" for line in old_line.splitlines()])

            rel_path = try_relative(replacement_set[0]["FilePath"])
            code_blocks += textwrap.dedent(
                f"""\

                {rel_path}:{replacement_line_num}:
                ```diff
                {old_line}
                {new_line}
                ```
                """
            )
    return code_blocks, end_line


def try_relative(path):
    """Try making `path` relative to current directory, otherwise make it an absolute path"""
    try:
        here = pathlib.Path.cwd()
        return pathlib.Path(path).relative_to(here)
    except ValueError:
        return pathlib.Path(path).resolve()


def format_notes(notes, offset_lookup):
    """Format an array of notes into a single string"""

    code_blocks = ""

    for note in notes:
        filename = note["FilePath"]

        if filename == "":
            return note["Message"]

        resolved_path = str(pathlib.Path(filename).resolve().absolute())

        line_num = find_line_number_from_offset(
            offset_lookup, resolved_path, note["FileOffset"]
        )
        line_offset = note["FileOffset"] - offset_lookup[resolved_path][line_num]
        source_line = read_one_line(
            resolved_path, offset_lookup[resolved_path][line_num]
        )

        path = try_relative(resolved_path)
        message = f"**{path}:{line_num}:** {note['Message']}"
        code = format_ordinary_line(source_line, line_offset)
        code_blocks += f"{message}\n{code}"

    return code_blocks


def make_comment_from_diagnostic(diagnostic_name, diagnostic, filename, offset_lookup, notes):
    """Create a comment from a diagnostic

    Comment contains the diagnostic message, plus its name, along with
    code block(s) containing either the exact location of the
    diagnostic, or suggested fix(es).

    """

    line_num = find_line_number_from_offset(
        offset_lookup, filename, diagnostic["FileOffset"]
    )
    line_offset = diagnostic["FileOffset"] - offset_lookup[filename][line_num]

    source_line = read_one_line(filename, offset_lookup[filename][line_num])
    end_line = line_num

    print(
        f"""{diagnostic}
    {line_num=};    {line_offset=};    {source_line=}
    """
    )

    if diagnostic["Replacements"]:
        code_blocks, end_line = format_diff_line(
            diagnostic, offset_lookup, line_num
        )
    else:
        # No fixit, so just point at the problem
        code_blocks = format_ordinary_line(source_line, line_offset)

    code_blocks += format_notes(notes, offset_lookup)

    comment_body = (
        f"warning: {diagnostic['Message']} [{diagnostic_name}]\n{code_blocks}"
    )

    return comment_body, end_line + 1


def comment_diagnostic_to_log(diagnostic, source_line, log_messages, http_prefix):

    if 'DiagnosticMessage' in diagnostic:
        diagnostic_message = diagnostic['DiagnosticMessage']
        message = diagnostic_message['Message'] + " (" + diagnostic['DiagnosticName'] + ")"
        file_path = diagnostic_message['FilePath']
    else:
        message = diagnostic['Message']
        file_path = diagnostic['FilePath']

    try:
        index = file_path.index("cpp/")
        file_path = file_path[index:]
        http_path = http_prefix + "/" + file_path
        http_path = http_path.replace(" ", "%20")
    except LookupError as e:
        print(f"error {e} finding 'cpp/' in {file_path}")
        http_path = file_path

    log_messages.append(
        f"::error ::{message} {http_path}#L{source_line} {file_path}:{source_line}")


def make_comments(diagnostics, diff_lookup, offset_lookup, build_dir, has_compile_commands, http_prefix):

    ignored_diagnostics = []
    if not has_compile_commands:
        # Clang-tidy will not be able to find aws-sdk headers. Ignore the error generated for missing headers.
        ignored_diagnostics.append("clang-diagnostic-error")
        # Because of missing headers, clang-tidy generates too many false negatives for the following warnings.
        ignored_diagnostics.append("cppcoreguidelines-init-variables")
        ignored_diagnostics.append("cppcoreguidelines-avoid-non-const-global-variables")
    log_messages = []
    for diagnostic in diagnostics:
        try:
            diagnostic_message = diagnostic["DiagnosticMessage"]
        except KeyError:
            # Pre-clang-tidy-9 format
            diagnostic_message = diagnostic

        if diagnostic_message["FilePath"] == "":
            continue

        if diagnostic["DiagnosticName"] in ignored_diagnostics:
            print(f'ignoring diagnostic {diagnostic["DiagnosticName"]}')
            continue

        comment_body, end_line = make_comment_from_diagnostic(
            diagnostic["DiagnosticName"],
            diagnostic_message,
            get_diagnostic_file_path(diagnostic, build_dir),
            offset_lookup,
            notes=diagnostic.get("Notes", []),
        )

        rel_path = str(try_relative(get_diagnostic_file_path(diagnostic, build_dir)))
        # diff lines are 1-indexed
        source_line = 1 + find_line_number_from_offset(
            offset_lookup,
            get_diagnostic_file_path(diagnostic, build_dir),
            diagnostic_message["FileOffset"],
        )

        if rel_path not in diff_lookup or end_line not in diff_lookup[rel_path]:
            print(
                f"WARNING: Skipping comment for file '{rel_path}' not in PR change set. \
                Comment body is:\n{comment_body}"
            )
            continue

        comment_diagnostic_to_log(diagnostic, source_line, log_messages, http_prefix)

    return log_messages


def get_line_ranges(diff, files):
    """Return the line ranges of added lines in diff, suitable for the
    line-filter argument of clang-tidy

    """

    lines_by_file = {}
    for filename in diff:
        if filename.target_file[2:] not in files:
            continue
        added_lines = []
        for hunk in filename:
            for line in hunk:
                if line.is_added:
                    added_lines.append(line.target_line_no)

        for _, group in itertools.groupby(
            enumerate(added_lines), lambda ix: ix[0] - ix[1]
        ):
            groups = list(map(itemgetter(1), group))
            lines_by_file.setdefault(filename.target_file[2:], []).append(
                [groups[0], groups[-1]]
            )

    line_filter_json = []
    for name, lines in lines_by_file.items():
        line_filter_json.append(str({"name": name, "lines": lines}))
    return json.dumps(line_filter_json, separators=(",", ":"))


def get_clang_tidy_warnings(
    line_filter, build_dir, clang_tidy_checks, clang_tidy_binary, config_file, files
):
    """Get the clang-tidy warnings"""

    if config_file != "":
        config = f"-config-file=\"{config_file}\""
    else:
        config = f"-checks={clang_tidy_checks}"

    print(f"Using config: {config}")

    if os.path.exists(os.path.join(build_dir, "compile_commands.json")):
        build_dir_arg = f"-p={build_dir}"
        has_compile_commands = True

    else:
        build_dir_arg = ""
        has_compile_commands = False

    command = f"{clang_tidy_binary} {build_dir_arg} {config} -line-filter={line_filter}   \
              {files} --export-fixes={FIXES_FILE}"

    start = datetime.datetime.now()
    try:
        with message_group(f"Running:\n\t{command}"):
            subprocess.run(
                command, capture_output=True, shell=True, check=True, encoding="utf-8"
            )
    except subprocess.CalledProcessError:
        pass

    end = datetime.datetime.now()

    print(f"Took: {end - start}")

    try:
        with open(FIXES_FILE, "r") as fixes_file:
            warnings_result = yaml.safe_load(fixes_file)
            warnings_result[HAS_COMPILE_COMMANDS] = has_compile_commands
            return warnings_result
    except FileNotFoundError:
        return {HAS_COMPILE_COMMANDS: has_compile_commands}


def main(
        repo,
        pr_number,
        build_dir,
        clang_tidy_checks,
        clang_tidy_binary,
        config_file,
        token,
        include_pattern,
        exclude_pattern,
        max_comments,
        lgtm_comment_body,
        ref,  # set during workflow runs, not set for PR
        head_ref,  # set for PR, not set during workflows
):
    source_actor = os.getenv('GITHUB_ACTOR')
    if pr_number is not None and pr_number != "":
        pull_request = PullRequest(repo, int(pr_number), token)
        diff = pull_request.get_pr_diff()
        branch = head_ref.replace("'", "")
    elif ref is not None:
        branch = ref[ref.rindex("/") + 1:]
        branch = branch.replace("'", "")
        commit = Commit(repo, branch, token)
        diff = commit.get_commit_diff()
    else:
        print("No pull request or workflow reference. Unable to review.")
        return 0

    source_repo = source_actor + repo[repo.find("/"):]
    http_prefix = f"https://github.com/{source_repo}/tree/{branch}"

    changed_files = [filename.target_file[2:] for filename in diff]
    files = []
    for pattern in include_pattern:
        files.extend(fnmatch.filter(changed_files, pattern))
    for pattern in exclude_pattern:
        files = [f for f in files if not fnmatch.fnmatch(f, pattern)]

    if not files:
        print("No files to check!")
        return 0

    print(f"Checking these files: {files}", flush=True)

    line_ranges = get_line_ranges(diff, files)
    if line_ranges == "[]":
        print("No lines added in this PR!")
        return 0

    clang_tidy_warnings = get_clang_tidy_warnings(
        line_ranges,
        build_dir,
        clang_tidy_checks,
        clang_tidy_binary,
        config_file,
        '"' + '" "'.join(files) + '"',
    )

    if "Diagnostics" not in clang_tidy_warnings:
        print(lgtm_comment_body)
        return 0

    diff_lookup = make_file_line_lookup(diff)
    offset_lookup = make_file_offset_lookup(files)

    with message_group("Creating annotations from warnings"):
        log_messages = make_comments(
            clang_tidy_warnings["Diagnostics"], diff_lookup, offset_lookup, build_dir,
            clang_tidy_warnings[HAS_COMPILE_COMMANDS], http_prefix
        )

    if not log_messages:
        print("No warnings to report, LGTM!")
        return 0

    for index in range(min(len(log_messages), max_comments)):
        print(log_messages[index])

    return 1


def strip_enclosing_quotes(string: str) -> str:
    stripped = string.strip()

    for quote in ['"', "'", '"']:
        if stripped.startswith(quote) and stripped.endswith(quote):
            stripped = stripped[1:-1]
    return stripped


def fix_absolute_paths(absolute_paths, base_dir):
    """Update absolute paths in compile_commands.json to new location, if
    compile_commands.json was created outside the Actions container
    """

    basedir = pathlib.Path(base_dir).resolve()
    newbasedir = pathlib.Path(".").resolve()

    if basedir == newbasedir:
        return

    print(f"Found '{absolute_paths}', updating absolute paths")
    # We might need to change some absolute paths if we're inside
    # a docker container
    with open(absolute_paths, "r") as f:
        compile_commands = json.load(f)

    print(f"Replacing '{basedir}' with '{newbasedir}'", flush=True)

    modified_compile_commands = json.dumps(compile_commands).replace(
        str(basedir), str(newbasedir)
    )

    with open(absolute_paths, "w") as f:
        f.write(modified_compile_commands)


if __name__ == "__main__":
    parser = argparse.ArgumentParser(
        description="Create a review from clang-tidy warnings"
    )
    parser.add_argument("--repo", help="Repo name in form 'owner/repo'")
    parser.add_argument("--pr", help="PR number")
    parser.add_argument(
        "--clang_tidy_binary", help="clang-tidy binary", default="clang-tidy-12"
    )
    parser.add_argument(
        "--build_dir", help="Directory with compile_commands.json", default="."
    )
    parser.add_argument(
        "--base_dir",
        help="Absolute path of initial working directory if compile_commands.json generated outside of Action",
        default=".",
    )
    parser.add_argument(
        "--clang_tidy_checks",
        help="checks argument",
        default="'-*,performance-*,readability-*,bugprone-*,clang-analyzer-*,cppcoreguidelines-*,mpi-*,misc-*'",
    )
    parser.add_argument(
        "--config_file",
        help="Path to .clang-tidy config file. If not empty, takes precedence over --clang_tidy_checks",
        default="",
    )
    parser.add_argument(
        "--include",
        help="Comma-separated list of files or patterns to include",
        type=str,
        nargs="?",
        default="*.[ch],*.[ch]xx,*.[ch]pp,*.[ch]++,*.cc,*.hh",
    )
    parser.add_argument(
        "--exclude",
        help="Comma-separated list of files or patterns to exclude",
        nargs="?",
        default="",
    )
    parser.add_argument(
        "--apt-packages",
        help="Comma-separated list of apt packages to install",
        type=str,
        default="",
    )
    parser.add_argument(
        "--cmake-command",
        help="If set, run CMake as part of the action with this command",
        type=str,
        default="",
    )
    parser.add_argument(
        "--max-comments",
        help="Maximum number of comments to post at once",
        type=int,
        default=25,
    )
    parser.add_argument(
        "--lgtm-comment-body",
        help="Message to post on PR if no issues are found. An empty string will post no LGTM comment.",
        type=str,
        default='clang-tidy review says "All clean, LGTM! :+1:"',
    )
    parser.add_argument("--token", help="github auth token")
    parser.add_argument("--head_ref", help="github head ref")
    parser.add_argument("--ref", help="github ref")
    parser.add_argument(
        "--dry-run", help="Run and generate review, but don't post", action="store_true"
    )

    args = parser.parse_args()

    # Remove any enclosing quotes and extra whitespace
    exclude = strip_enclosing_quotes(args.exclude).split(",")
    include = strip_enclosing_quotes(args.include).split(",")

    if args.apt_packages:
        # Try to make sure only 'apt install' is run
        apt_packages = re.split(BAD_CHARS_APT_PACKAGES_PATTERN, args.apt_packages)[
            0
        ].split(",")
        with message_group(f"Installing additional packages: {apt_packages}"):
            subprocess.run(
                ["apt-get", "install", "-y", "--no-install-recommends"] + apt_packages
            )

    build_compile_commands = f"{args.build_dir}/compile_commands.json"

    cmake_command = strip_enclosing_quotes(args.cmake_command)

    # If we run CMake as part of the action, then we know the paths in
    # the compile_commands.json file are going to be correct
    if cmake_command:
        with message_group(f"Running cmake: {cmake_command}"):
            subprocess.run(cmake_command, shell=True, check=True)

    elif os.path.exists(build_compile_commands):
        fix_absolute_paths(build_compile_commands, args.base_dir)

    result = main(
        repo=args.repo,
        pr_number=args.pr,
        build_dir=args.build_dir,
        clang_tidy_checks=args.clang_tidy_checks,
        clang_tidy_binary=args.clang_tidy_binary,
        config_file=args.config_file,
        token=args.token,
        include_pattern=include,
        exclude_pattern=exclude,
        max_comments=args.max_comments,
        lgtm_comment_body=strip_enclosing_quotes(args.lgtm_comment_body),
        head_ref=args.head_ref,
        ref=args.ref,
     )

    if result == 1:
        exit(1)
