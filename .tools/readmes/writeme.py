#!/usr/bin/env python3
# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# For debugging, engineers can skip update with the --no-update flag. Yes, it's
# a double negative, but it's quick and early in the startup because of the
# reliance on the side-effect imports from `update` and needing them to happen
# before importing runner, which means before importing the runner argparser.
NO_UPDATE_FLAG = "--no-update"

if __name__ == "__main__":
    import sys

    if NO_UPDATE_FLAG not in sys.argv:
        from update import update

        update()
    else:
        sys.argv.remove(NO_UPDATE_FLAG)

    # This import must remain in the main, after the update, as it depends on
    # importing the things that got changed during update.
    from typer import run

    from runner import writeme

    run(writeme)
else:
    from .runner import writeme

    main = writeme
