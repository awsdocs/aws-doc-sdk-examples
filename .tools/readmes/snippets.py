# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

import logging
import os

logger = logging.getLogger(__name__)

# This is in two parts so that it doesn't flag pre_validate
SNIPPET_START = "snippet-" + "start:["
IGNORE_FOLDERS = {
    ".pytest_cache",
    "__pycache__",
    "cdk.out",
    "node_modules",
    "Pods",
    "dist",
    "target",
    "venv",
    ".venv",
    "bin",
    "obj",
    ".doc_gen",
    ".git-hooks",
    ".github",
    ".git",
}
EXT_IGNORES = {
    ".png",
    ".zip",
    ".jpg",
    ".wav",
    ".ico",
    ".mp3",
    ".pdf",
    ".jar",
    ".swp",
    ".keystore",
}


class Snippet:
    def __init__(self, tag, path, line):
        self.tag = tag
        self.path = path
        self.line = line

    @staticmethod
    def tag_from_line(token, line) -> str:
        tag_start = line.find(token) + len(token)
        tag_end = line.find("]", tag_start)
        return line[tag_start:tag_end].strip()


def scan_for_snippets(root):
    snippets = {}
    for root, dirs, files in os.walk(root):
        dirs[:] = [
            d
            for d in dirs
            if d not in IGNORE_FOLDERS or ("rust_dev_preview" in root and "src" in root)
        ]
        for f in files:
            ext = os.path.splitext(f)[1].lower()
            if ext in EXT_IGNORES:
                continue
            try:
                with open(os.path.join(root, f), "r") as search_file:
                    for index, line in enumerate(search_file.readlines()):
                        if SNIPPET_START in line:
                            tag = Snippet.tag_from_line(SNIPPET_START, line)
                            snippets[tag] = Snippet(tag, search_file.name, index + 1)
            except UnicodeDecodeError:
                logger.debug("Skipping %s due to unicode decode error.", f)
    return snippets
