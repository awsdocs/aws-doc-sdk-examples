# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

from dataclasses import dataclass
from pathlib import Path
from typing import Any, Dict, Iterable, List, Optional, Set, Tuple
import re

from .validator_config import skip
from .file_utils import get_files, clear
from .metadata import Example
from .metadata_errors import MetadataErrors, MetadataError
from .project_validator import (
    verify_no_deny_list_words,
    verify_no_secret_keys,
    ValidationConfig,
)
from . import validator_config

SNIPPET_START = "snippet-start:["
SNIPPET_END = "snippet-end:["


@dataclass
class Snippet:
    id: str
    file: str  # Not a path, but a path fragment.
    line_start: int
    line_end: int
    code: str


@dataclass
class SnippetError(MetadataError):
    line: Optional[int] = None
    tag: Optional[str] = None

    def prefix(self):
        return super().prefix() + f" at l{self.line} for {self.tag}: "


@dataclass
class DuplicateSnippetStartError(SnippetError):
    def message(self):
        return "duplicate snippet-start tag"


@dataclass
class DuplicateSnippetEndError(SnippetError):
    def message(self):
        return "duplicate snippet-end tag"


@dataclass
class MissingSnippetStartError(SnippetError):
    def message(self):
        return "snippet-end with no matching start"


@dataclass
class MissingSnippetEndError(SnippetError):
    def message(self):
        return "snippet-start with no matching end"


@dataclass
class SnippetAlreadyWritten(MetadataError):
    def message(self):
        return "Snippet file already exists, which means this tag is defined more than once in separate files."


@dataclass
class SnippetWriteError(MetadataError):
    error: Any = None

    def message(self):
        return "Error writing snippet file."


@dataclass
class MetadataUnicodeError(MetadataError):
    err: Optional[UnicodeDecodeError] = None

    def message(self):
        return f" unicode error: {str(self.err)}"


@dataclass
class FileReadError(MetadataError):
    err: Optional[Exception] = None

    def message(self):
        return f" exception: {str(self.err)}"


def _tag_from_line(token: str, line: str, prefix: str) -> str:
    tag_start = line.find(token) + len(token)
    tag_end = line.find("]", tag_start)
    return prefix + line[tag_start:tag_end].strip()


def parse_snippets(
    lines: List[str], file: Path, prefix: str
) -> Tuple[Dict[str, Snippet], MetadataErrors]:
    snippets: Dict[str, Snippet] = {}
    errors = MetadataErrors()
    open_tags: Set[str] = set()
    for line_idx, line in enumerate(lines):
        if SNIPPET_START in line:
            tag = _tag_from_line(SNIPPET_START, line, prefix)
            if tag in snippets:
                errors.append(
                    DuplicateSnippetStartError(file=file, line=line_idx, tag=tag)
                )
            else:
                snippets[tag] = Snippet(
                    id=tag,
                    file=str(file),
                    line_start=line_idx,
                    line_end=-1,
                    code="",
                )
                open_tags.add(tag)
        elif SNIPPET_END in line:
            tag = _tag_from_line(SNIPPET_END, line, prefix)
            if tag not in snippets:
                errors.append(
                    MissingSnippetStartError(file=file, line=line_idx, tag=tag)
                )
            elif tag not in open_tags:
                errors.append(
                    DuplicateSnippetEndError(file=file, line=line_idx, tag=tag)
                )
            else:
                open_tags.remove(tag)
                snippets[tag].line_end = line_idx
        else:
            for tag in open_tags:
                snippets[tag].code += line

    for tag in open_tags:
        errors.append(
            MissingSnippetEndError(file=file, line=snippets[tag].line_start, tag=tag)
        )
    return snippets, errors


def find_snippets(file: Path, prefix: str) -> Tuple[Dict[str, Snippet], MetadataErrors]:
    errors = MetadataErrors()
    snippets: Dict[str, Snippet] = {}
    try:
        with open(file, encoding="utf-8") as snippet_file:
            try:
                snippets, errs = parse_snippets(snippet_file.readlines(), file, prefix)
                errors.extend(errs)
            except UnicodeDecodeError as err:
                errors.append(MetadataUnicodeError(file=file, err=err))
    except FileNotFoundError:
        pass
    except Exception as e:
        errors.append(FileReadError(file=file, err=e))
    return snippets, errors


def collect_snippets(
    root: Path, prefix: str = ""
) -> Tuple[Dict[str, Snippet], MetadataErrors]:
    snippets: Dict[str, Snippet] = {}
    errors = MetadataErrors()
    for file in get_files(root, skip):
        snips, errs = find_snippets(file, prefix)
        snippets.update(snips)
        errors.extend(errs)
    return snippets, errors


def collect_snippet_files(
    examples: Iterable[Example],
    snippets: Dict[str, Snippet],
    prefix: str,
    errors: MetadataErrors,
    root: Path,
):
    for example in examples:
        for lang in example.languages:
            language = example.languages[lang]
            for version in language.versions:
                for excerpt in version.excerpts:
                    for snippet_file in excerpt.snippet_files:
                        if not (root / snippet_file).exists():
                            # Ensure all snippet_files exist
                            errors.append(
                                MissingSnippetFile(
                                    file=example.file,
                                    snippet_file=snippet_file,
                                    id=f"{lang}:{version.sdk_version}",
                                )
                            )
                            continue
                        if re.search(win_unsafe_re, str(snippet_file)):
                            errors.append(
                                WindowsUnsafeSnippetFile(
                                    file=example.file,
                                    snippet_file=snippet_file,
                                    id=f"{lang}:{version.sdk_version}",
                                )
                            )
                            continue
                        name = prefix + str(snippet_file).replace("/", ".")
                        with open(root / snippet_file, encoding="utf-8") as file:
                            code = file.readlines()
                            snippets[name] = Snippet(
                                id=name,
                                file=snippet_file,
                                line_start=0,
                                line_end=len(code),
                                code="".join(
                                    strip_snippet_tags(strip_spdx_header(code))
                                ),
                            )


def strip_snippet_tags(lines: List[str]) -> List[str]:
    return [line for line in lines if not has_snippet_tag_or_spdx_header(line)]


def strip_spdx_header(file: List[str]) -> List[str]:
    [a, b, c, *_] = file + [""] * 3
    has_copyright = (
        re.match(validator_config.SPDX_LEADER + validator_config.SPDX_COPYRIGHT, a)
        is not None
    )
    has_license = (
        re.match(validator_config.SPDX_LEADER + validator_config.SPDX_LICENSE, b)
        is not None
    )
    has_spacer = "" == c
    if has_copyright and has_license:
        if has_spacer:
            return file[3:]
        else:
            return file[2:]
    else:
        return file


def has_snippet_tag_or_spdx_header(line: str) -> bool:
    return "snippet-start" in line or "snippet-end" in line


@dataclass
class MissingSnippet(MetadataError):
    tag: Optional[str] = None

    def message(self):
        return f"missing snippet {self.tag}"


@dataclass
class MissingSnippetFile(MetadataError):
    snippet_file: Optional[str] = None

    def message(self):
        return f"missing snippet_file {self.snippet_file}"


@dataclass
class WindowsUnsafeSnippetFile(MetadataError):
    snippet_file: Optional[str] = None

    def message(self):
        return f"snippet_file with unsafe Windows name {self.snippet_file}"


# This set is from https://superuser.com/a/358861, but does not include / or \ as those are verified as the entire path
win_unsafe_re = r'[:*?"<>|]'


def validate_snippets(
    examples: Iterable[Example],
    snippets: Dict[str, Snippet],
    validation: ValidationConfig,
    errors: MetadataErrors,
    root: Path,
):
    for example in examples:
        for lang in example.languages:
            language = example.languages[lang]
            for version in language.versions:
                for excerpt in version.excerpts:
                    for snippet_tag in excerpt.snippet_tags:
                        if snippet_tag not in snippets:
                            # Ensure all metadata snippets are found
                            errors.append(
                                MissingSnippet(
                                    file=example.file,
                                    id=f"{lang}:{version.sdk_version}",
                                    tag=snippet_tag,
                                )
                            )

    for snippet in snippets.values():
        verify_no_deny_list_words(snippet.code, root / snippet.file, errors)
        verify_no_secret_keys(snippet.code, root / snippet.file, validation, errors)


def write_snippets(root: Path, snippets: Dict[str, Snippet], check: bool = False):
    errors = MetadataErrors()
    for tag in snippets:
        name = root / f"{tag}.txt"
        if check and name.exists():
            errors.append(SnippetAlreadyWritten(file=name))
        else:
            try:
                with open(name, "w", encoding="utf-8") as file:
                    file.write(snippets[tag].code)
            except Exception as error:
                errors.append(SnippetWriteError(file=name, error=error))
    return errors


def main():
    from argparse import ArgumentParser

    parser = ArgumentParser()
    parser.add_argument(
        "--root",
        default=f"{Path(__file__).parent.parent}",
        help="The root path from which to search for files to check. The default is the root of the git repo (two up from this file).",
    )
    args = parser.parse_args()
    root = Path(args.root).resolve()
    snippets, errors = collect_snippets(root)
    print(f"Found {len(snippets)} snippets")
    out = root / ".snippets"
    clear(out)
    errors.maybe_extend(write_snippets(out, snippets))
    if len(errors) > 0:
        print(errors)
    print(f"Wrote snippets to {out}")


if __name__ == "__main__":
    main()
