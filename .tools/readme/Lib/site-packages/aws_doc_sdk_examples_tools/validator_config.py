# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

from pathlib import Path
from typing import Set
from urllib.request import urlopen
import json

# Only files with these extensions are scanned.
EXT_LOOKUP = {
    ".abap": "SAP ABAP",
    ".c": "C",
    ".cmd": "AWS-CLI",
    ".cpp": "C++",
    ".cs": "C#",
    ".csx": "C#",
    ".css": "CSS",
    ".go": "Go",
    ".h": "C++",
    ".html": "JavaScript",
    ".java": "Java",
    ".js": "JavaScript",
    ".json": "JSON",
    ".jsx": "JavaScript",
    ".kt": "Kotlin",
    ".md": "Markdown",
    ".mjs": "JavaScript",
    ".mts": "TypeScript",
    ".php": "PHP",
    ".py": "Python",
    ".rb": "Ruby",
    ".rs": "Rust",
    ".sh": "AWS-CLI",
    ".swift": "Swift",
    ".tf": "Terraform",
    ".toml": "Toml",
    ".ts": "TypeScript",
    ".tsx": "TypeScript",
    ".txt": "CMake",
    ".yaml": "YAML",
    ".yml": "YAML",
}


def skip(path: Path) -> bool:
    return path.suffix.lower() not in EXT_LOOKUP or path.name in IGNORE_FILES


# If you get a lot of false-flagged 40-character errors
# in specific folders or files, you can omit them from
# these scans by adding them to the following lists.
# However, because this script is mostly run as a GitHub
# action in a clean environment (aside from testing),
# exhaustive ignore lists shouldn"t be necessary.

# Files to skip.
IGNORE_FILES = {
    ".moviedata.json",
    ".travis.yml",
    "AssemblyInfo.cs",
    "moviedata.json",
    "movies.json",
    "movies_5.json",
    "package-lock.json",
}

IGNORE_SPDX_SUFFIXES = {
    ".css",
    ".csv",
    ".html",
    ".json",
    ".md",
    ".toml",
    ".txt",
    ".yaml",
    ".yml",
}

SPDX_LEADER = r"^(#|//|\") "
SPDX_COPYRIGHT = r"Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved."
SPDX_LICENSE = r"SPDX-License-Identifier: (Apache-2.0|MIT-0)"

GOOD_WORDS = {
    "crash",
    "dp",
    "dummy",
    "massa",
    "jerry",
    "throat",
}

words: Set[str] = set()
try:
    DATA = urlopen(
        "https://raw.githubusercontent.com/zacanger/profane-words/5ad6c62fa5228293bc610602eae475d50036dac2/words.json"
    )
    words = set(json.load(DATA)).difference(GOOD_WORDS)
except:  # noqa: E722
    pass

WORDS = words

# List of words that should never be in code examples.
DENY_LIST = {"alpha-docs-aws.amazon.com", "integ-docs-aws.amazon.com"}.union(WORDS)

# Allowlist of 20- or 40-character strings to allow.
ALLOW_LIST = {
    # Well-known user credentials for testing
    "AIDA123456789EXAMPLE",
    "AIDA987654321EXAMPLE",
    "AROA123456789EXAMPLE",
    "AROA987654321EXAMPLE",
    "ASIAIOSFODNN7EXAMPLE",
    "ASIAI44QH8DHBEXAMPLE",
    "AKIAIOSFODNN7EXAMPLE",
    "AKIAI44QH8DHBEXAMPLE",
    "APKAEIBAERJR2EXAMPLE",
    "APKAEIVFHP46CEXAMPLE",
    "wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY",
    "je7MtGbClwBF/2Zp9Utk/h3yCo8nvbEXAMPLEKEY",
    # Commit SHAs
    "31c3650d70c243ca7141bb08705102cad89bd0e8",  # Fist commit of this repo
}


# Media file types.
MEDIA_FILE_TYPES = {"mp3", "wav", "jpg", "jpeg", "png"}
