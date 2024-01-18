import pytest
from pathlib import Path

import snippets


@pytest.mark.parametrize(
    "file_contents,expected_error_count",
    [
        (
            "snippet" + "-start:[this.is.a.snippet.tag]\n"
            "This is not code.\n"
            "snippet" + "-end:[this.is.a.snippet.tag]",
            0,
        ),
        (
            "snippet" + "-start:[this.is.a.snippet.tag]\n"
            "This is not code.\n"
            "snippet" + "-end:[this.is.a.different.snippet.tag]",
            2,
        ),
        ("snippet" + "-start:[this.is.a.snippet.tag]\n" "This is not code.", 1),
        ("This is not code.\n" "snippet" + "-end:[this.is.a.snippet.tag]", 1),
        (
            "snippet" + "-start:[this.is.a.snippet.tag]\n"
            "snippet" + "-start:[this.is.a.different.snippet.tag]\n"
            "This is not code.\n"
            "snippet" + "-end:[this.is.a.snippet.tag]\n"
            "snippet" + "-end:[this.is.a.different.snippet.tag]\n",
            0,
        ),
        (
            "snippet" + "-start:[this.is.a.snippet.tag]\n"
            "snippet" + "-start:[this.is.a.different.snippet.tag]\n"
            "This is not code.\n"
            "snippet" + "-end:[this.is.a.different.snippet.tag]\n"
            "snippet" + "-end:[this.is.a.snippet.tag]\n",
            0,
        ),
        (
            "snippet" + "-start:[this.is.a.snippet.tag]\n"
            "This is not code.\n"
            "snippet" + "-end:[this.is.a.snippet.tag.with.extra.stuff]\n",
            2,
        ),
        (
            "snippet" + "-start:[this.is.a.snippet.tag]\n"
            "snippet" + "-start:[this.is.a.snippet.tag]\n"
            "This is not code.\n"
            "snippet" + "-end:[this.is.a.snippet.tag]\n",
            1,
        ),
        (
            "snippet" + "-start:[this.is.a.snippet.tag]\n"
            "This is not code.\n"
            "snippet" + "-end:[this.is.a.snippet.tag]\n"
            "snippet" + "-end:[this.is.a.snippet.tag]\n",
            1,
        ),
    ],
)
def test_verify_snippet_start_end(file_contents: str, expected_error_count: int):
    """Test that various kinds of mismatched snippet-start and -end tags are
    counted correctly as errors."""
    _, errors = snippets.parse_snippets(file_contents.split("\n"), Path("test"))
    error_count = len(errors)
    assert error_count == expected_error_count
