#!/usr/bin/env python3
# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

import pytest
from pathlib import Path
from typing import List

from .metadata_errors import MetadataError, MetadataErrors
from .spdx import verify_spdx, MissingSPDX


@pytest.mark.parametrize(
    "ext,contents,expected_errors",
    [
        (
            "py",
            (
                "# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.\n"
                "# SPDX-License-Identifier: Apache-2.0\n"
                "\n"
                "def foo():\n\tpass"
            ),
            [],
        ),
        (
            "py",
            (
                "#!/usr/bin/python\n"
                "# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.\n"
                "# SPDX-License-Identifier: Apache-2.0\n"
                "\n"
                "def foo():\n\tpass"
            ),
            [],
        ),
        (
            "swift",
            (
                "// swift-tools-version: 5.5\n"
                "// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.\n"
                "// SPDX-License-Identifier: Apache-2.0\n"
            ),
            [],
        ),
        (
            "py",
            ("def foo():\n\tpass"),
            [MissingSPDX(file=Path("/tmp/file.py"))],
        ),
    ],
)
def test_verify_spdx(ext: str, contents: str, expected_errors: List[MetadataError]):
    errors = MetadataErrors()
    verify_spdx(contents, Path(f"/tmp/file.{ext}"), errors)
    assert expected_errors == [*errors]
