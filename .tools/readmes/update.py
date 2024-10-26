# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

import logging
from pathlib import Path
from subprocess import DEVNULL, PIPE, CalledProcessError, run
from sys import executable


def update():
    logging.info("Updating WRITEME environment")
    try:
        run(
            [executable, "-m", "pip", "install", "-e", Path(__file__).parent],
            check=True,
            stderr=PIPE,
            stdin=DEVNULL,
            stdout=DEVNULL,
        )
    except CalledProcessError as _cpe:
        raise RuntimeError(f"Update failed: {_cpe.stderr}")
