#!/usr/bin/env python3
# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

from pathlib import Path
from tree_sitter import Language, Parser

Language.build_library(
    # Store the library in the `build` directory
    "vendors/languages.so",
    # Include one or more languages
    [
        "vendors/tree-sitter-rust",
        "vendors/tree-sitter-javascript",
        "vendors/tree-sitter-python",
    ],
)


"""
Ideas for updated yaml:
excerpts:
  - description: My thing
    snippet_files:
      - foo.rs
    snippet_query:
      - file: bar.rs
        function: get_engine
      - file: baz.rs
        class: Engine
      - file: bang.rs
        constant: MY_CLIENT

      # Java and Kotlin (Not sure I like this dedicated syntax)
      - classpath: com.amazon.examples.Foo::get_engine
"""

# https://tree-sitter.github.io/tree-sitter/playground
LANGUAGES = {
    "rs": {
        "name": "rust",
        "functions": """((line_comment)* . (function_item name: (identifier) @name)) @fn""",
        "function": """
        (
            (line_comment)* .
            (function_item name: (identifier) @name (#eq? @name "{name}"))
        )
        @fn
        """,
        "class": """(impl_item name: (identifier) @name (#eq? @name "{name}")) @fn""",
        "constant": "",
    },
    "js": {
        "name": "javascript",
        "function": """(
            (comment)*
            .
            [
                (function_declaration name: (identifier) @name (#eq? @name "{name}"))
                (assignment_expression
                    left: (identifier) @name (#eq? @name "{name}")
                    right: (arrow_function)
                )
            ]
            )
            @fn
        """,
        "class": """(
            (comment)* .
            (impl_item name: (identifier) @name (#eq? @name "{name}"))
            ) @fn""",
        "constant": "",
    },
}

SNIPPETS = [
    (
        "rustv1/examples/aurora/src/aurora_scenario/mod.rs",
        "set_engine",
    ),  # Rust Function
    (
        "javascriptv3/example_code/dynamodb/actions/document-client/put.js",
        "main",
    ),  # JS Arrow Function
    (
        "javascriptv3/example_code/cross-services/textract-react/src/App.js",
        "App",
    ),  # JS Function Declaration
]
ROOT = Path(__file__).parent / "../../"


def get_tree(file):
    parser = Parser()
    ext = file.name.split(".")[-1]
    language = LANGUAGES[ext]
    sitter = Language("vendors/languages.so", language["name"])
    parser.set_language(sitter)
    with open(ROOT / file, "rb") as file:
        source = file.read()

    tree = parser.parse(source)
    return (tree, language, sitter)


def find_snippets():
    for file, name in SNIPPETS:
        file = Path(file)
        tree, language, sitter = get_tree(file)
        query_string = language["function"].format(name=name)
        query = sitter.query(query_string)
        captures = query.captures(tree.root_node)
        print(file.name, name, query_string.replace("\n", ""))
        if len(captures) == 0:
            print("No captures")
        for capture in captures:
            print(capture[1], str(capture[0].text, "utf-8")[0:20])

        # TODO remove snippet tags in post


def find_functions():
    for file in ROOT.glob("rustv1/**/*.rs"):
        tree, language, sitter = get_tree(file)
        query_string = language["functions"]
        query = sitter.query(query_string)
        captures = query.captures(tree.root_node)
        print(
            file.name,
            "captures:",
            ", ".join(
                [str(node.text, "utf-8") for (node, name) in captures if name == "name"]
            ),
        )


if __name__ == "__main__":
    # find_functions()
    find_snippets()
