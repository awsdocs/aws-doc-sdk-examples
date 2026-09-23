# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Integration test for the Amazon S3 Object Annotations scenario.

Runs the scenario end to end against real AWS (no mocking of the S3 client),
feeding the interactive prompts through the shared input_mocker fixture and
asserting on the scenario's output. Follows the pattern used by the other S3
scenario integration tests (see scenarios/conditional_requests/test).
"""

import pytest

from s3_wrapper import S3AnnotationsWrapper
from scenario_object_annotations import ObjectAnnotationsScenario


@pytest.mark.integ
def test_run_object_annotations_scenario_integ(input_mocker, capsys):
    scenario = ObjectAnnotationsScenario(S3AnnotationsWrapper.from_client())

    # Answers, in order, for the scenario's prompts:
    #   1. bucket name prefix, then four "Press Enter to continue" prompts.
    input_mocker.mock_answers(
        [
            "annotations-integ-test",
            "",
            "",
            "",
            "",
        ]
    )

    scenario.run_scenario()

    capt = capsys.readouterr()
    assert "Cleanup complete!" in capt.out
    assert "All annotations cleaned up." in capt.out
