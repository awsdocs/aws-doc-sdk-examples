# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Helper class for running scenarios at a command prompt. Asks questions, validates
and converts input, and returns answers.
"""


# snippet-start:[python.demo_tools.Question]
def ask(question, *validators):
    """
    Asks a single question and validates it against a list of validators.
    When an answer fails validation, the complaint is printed and the question
    is asked again.

    :param question: The question to ask.
    :param validators: The list of validators that the answer must pass.
    :return: The answer, converted to its final form by the validators.
    """
    answer = None
    while answer is None:
        answer = input(question)
        for validator in validators:
            answer, complaint = validator(answer)
            if answer is None:
                print(complaint)
                break
    return answer


def non_empty(answer):
    """
    Validates that the answer is not empty.
    :return: The non-empty answer, or None.
    """
    return answer if answer != '' else None, "I need an answer. Please?"


def is_yesno(answer):
    """
    Validates a yes/no answer.
    :return: True when the answer is 'y'; otherwise, False.
    """
    return answer.lower() == 'y', ""


def is_int(answer):
    """
    Validates that the answer can be converted to an int.
    :return: The int answer; otherwise, None.
    """
    try:
        int_answer = int(answer)
    except ValueError:
        int_answer = None
    return int_answer, f"{answer} must be a valid integer."


def is_letter(answer):
    """
    Validates that the answer is a letter.
    :return The letter answer, converted to uppercase; otherwise, None.
    """
    return answer.upper() if answer.isalpha() else None, f"{answer} must be a single letter."


def is_float(answer):
    """
    Validate that the answer can be converted to a float.
    :return The float answer; otherwise, None.
    """
    try:
        float_answer = float(answer)
    except ValueError:
        float_answer = None
    return float_answer, f"{answer} must be a valid float."


def in_range(lower, upper):
    """
    Validate that the answer is within a range. The answer must be of a type that can
    be compared to the lower and upper bounds.
    :return: The answer, if it is within the range; otherwise, None.
    """
    def _validate(answer):
        return (
            answer if lower <= answer <= upper else None,
            f"{answer} must be between {lower} and {upper}.")
    return _validate
# snippet-end:[python.demo_tools.Question]
