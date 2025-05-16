import pytest
from unittest.mock import patch, mock_open, MagicMock
from argparse import Namespace
from pathlib import Path

from .categories import Category
from .doc_gen import DocGen, MetadataError, Example
from .doc_gen_cli import main
from .metadata import DocFilenames, Language, SDKPageVersion, Version
from .sdks import Sdk, SdkVersion
from .services import Service


@pytest.fixture
def mock_example():
    return Example(
        file=Path("test_cpp.yaml"),
        id="medical-imaging_GoodScenario",
        title="Scenario title",
        title_abbrev="Scenario title abbrev",
        synopsis="scenario synopsis.",
        category="Scenarios",
        doc_filenames=DocFilenames(
            service_pages={
                "medical-imaging": "link",
            },
            sdk_pages={
                "cpp": {
                    1: SDKPageVersion(actions_scenarios={"medical-imaging": f"link"})
                }
            },
        ),
        services={
            "medical-imaging": {"GoodOne"},
        },
        languages={
            "C++": Language(
                name="C++", property="cpp", versions=[Version(sdk_version=1)]
            )
        },
    )


@pytest.fixture
def mock_doc_gen(mock_example):
    doc_gen = DocGen.empty()
    doc_gen.errors._errors = [
        MetadataError(file="a.yaml", id="Error 1"),
        MetadataError(file="b.yaml", id="Error 2"),
    ]
    doc_gen.categories = {"Actions": Category(key="Actions", display="Action")}
    doc_gen.services = {
        "medical-imaging": Service(
            long="&AHIlong;",
            short="&AHI;",
            sort="HealthImaging",
            version="medical-imaging-2023-07-19",
            sdk_id="Medical Imaging",
        )
    }
    doc_gen.sdks = {
        "JavaScript": Sdk(
            name="JavaScript",
            display="JavaScript",
            versions=[SdkVersion(version=3, long="&JS;", short="&JSlong")],
            guide="",
            property="javascript",
            is_pseudo_sdk=False,
        )
    }
    doc_gen.examples = {"ex": mock_example}
    return doc_gen


@pytest.fixture
def patched_environment(mock_doc_gen):
    mock_doc_gen.validate = MagicMock()
    with patch("argparse.ArgumentParser.parse_args") as mock_parse_args, patch(
        "aws_doc_sdk_examples_tools.doc_gen.DocGen.empty", return_value=mock_doc_gen
    ), patch("aws_doc_sdk_examples_tools.doc_gen.DocGen.from_root"), patch(
        "json.dumps"
    ) as mock_json_dump, patch(
        "builtins.open", mock_open()
    ):
        yield mock_parse_args, mock_json_dump


@pytest.mark.parametrize("strict,should_raise", [(True, True), (False, False)])
def test_doc_gen_strict_option(strict, should_raise, patched_environment):
    mock_parse_args, mock_json_dump = patched_environment
    mock_args = Namespace(
        from_root=["/mock/path"],
        write_json="mock_output.json",
        write_snippets="",
        strict=strict,
        skip_entity_expansion=False,
    )
    mock_parse_args.return_value = mock_args

    if should_raise:
        with pytest.raises(SystemExit) as exc_info:
            main()
        assert exc_info.value.code == 1
    else:
        main()


def test_skip_entity_expansion(patched_environment):
    mock_parse_args, mock_json_dump = patched_environment
    mock_args = Namespace(
        from_root=["/mock/path"],
        write_json="mock_output.json",
        write_snippets="",
        strict=False,
        skip_entity_expansion=True,
    )
    mock_parse_args.return_value = mock_args

    with patch(
        "aws_doc_sdk_examples_tools.doc_gen.DocGen.expand_entities"
    ) as mock_expand_entities:
        main()
        assert not mock_expand_entities.called


def test_default_entity_expansion(patched_environment):
    mock_parse_args, mock_json_dump = patched_environment
    mock_args = Namespace(
        from_root=["/mock/path"],
        write_json="mock_output.json",
        write_snippets="",
        strict=False,
        skip_entity_expansion=False,
    )
    mock_parse_args.return_value = mock_args

    with patch(
        "aws_doc_sdk_examples_tools.doc_gen.DocGen.expand_entities"
    ) as mock_expand_entities:
        mock_expand_entities.return_value = None, []
        main()
        assert mock_expand_entities.called
