from pathlib import Path
import pytest

from aws_doc_sdk_examples_tools.doc_gen import DocGen
from aws_doc_sdk_examples_tools.yaml_writer import prepare_write


ROOT = Path(__file__).parent / "test_resources" / "doc_gen_test"


@pytest.fixture
def sample_doc_gen():
    return DocGen.from_root(ROOT)


def test_doc_gen(sample_doc_gen: DocGen):
    del sample_doc_gen.examples["sns_EntityFailures"]
    writes = prepare_write(sample_doc_gen.examples)
    assert writes

    writes = {k.replace(str(ROOT) + "/", ""): v for k, v in writes.items()}

    expected_writes = {
        ".doc_gen/metadata/aws_entity_metadata.yaml": """sns_EntitySuccesses:
  title: Title has &AWS; using an &AWS; SDK
  title_abbrev: Title Abbrev has &AWS; in it
  synopsis: this <programlisting>Synopsis programlisting has AWS in it.</programlisting>.
  synopsis_list:
  - Synopsis list code has <code>AWS</code> in it.
  category: Cat
  languages:
    Java:
      versions:
      - sdk_version: 1
        github: java/example_code/svc_EntityFailures
        excerpts:
        - description: This <emphasis><programlisting>Description programlisting has AWS in it</programlisting></emphasis> doesn't it.
          snippet_tags:
          - java.example_code.svc_EntityFailures.Test
  services:
    sns: {}
"""
    }

    assert writes == expected_writes
