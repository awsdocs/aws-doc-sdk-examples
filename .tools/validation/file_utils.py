# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

import os

from pathlib import Path
from collections.abc import Generator, Callable

from pathspec import GitIgnoreSpec


def match_path_to_specs(path: Path, specs: list[GitIgnoreSpec]) -> bool:
    """
    Return True if we should skip this path, that is, it is matched by a .gitignore.
    """
    for spec in specs:
        if spec.match_file(path):
            return True
    return False


def walk_with_gitignore(
    root: Path, specs: list[GitIgnoreSpec] = []
) -> Generator[Path, None, None]:
    """
    Starting from a root directory, walk the file system yielding a path for each file.
    However, it also reads `.gitignore` files, so that it behaves like `git ls-files`.
    It does not actively use `git ls-files` because it wouldn't catch new files without
    fiddling with a number of flags.
    """
    gitignore = root / ".gitignore"
    if gitignore.exists():
        with open(root / ".gitignore", "r", encoding="utf-8") as gitignore:
            specs = [*specs, GitIgnoreSpec.from_lines(gitignore.readlines())]
    for entry in os.scandir(root):
        if not match_path_to_specs(entry.path, specs):
            path = Path(entry.path)
            if entry.is_dir():
                yield from walk_with_gitignore(path, specs)
            else:
                yield path


def get_files(
    root: Path, skip: Callable[[Path], bool] = lambda _: False
) -> Generator[Path, None, None]:
    """
    Yield non-skipped files, that is, anything not matching git ls-files and not
    in the "to skip" files that are in git but are machine generated, so we don't
    want to validate them.
    """
    for path in walk_with_gitignore(root):
        if not skip(path):
            yield path
