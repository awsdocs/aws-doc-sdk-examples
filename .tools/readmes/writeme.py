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

    # This import from runner must remain in __main__, after calling update(),
    # as it depends on things that got changed during update().
    from runner import writeme

    from typer import run

    # Run writeme and ensure proper exit code handling
    try:
        result = run(writeme)
        if result is not None and result != 0:
            sys.exit(result)
    except SystemExit as e:
        # Ensure we exit with the proper code
        sys.exit(e.code)
else:
    from .runner import writeme

    main = writeme
