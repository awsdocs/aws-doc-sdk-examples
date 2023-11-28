from pathlib import Path
from tree_sitter import Language, Parser

Language.build_library(
    # Store the library in the `build` directory
    "build/my-languages.so",
    # Include one or more languages
    [
        "vendors/tree-sitter-rust",
        "vendors/tree-sitter-javascript",
        "vendors/tree-sitter-python",
    ],
)


RS_LANGUAGE = Language("build/my-languages.so", "rust")
JS_LANGUAGE = Language("build/my-languages.so", "javascript")
PY_LANGUAGE = Language("build/my-languages.so", "python")

parser = Parser()
parser.set_language(RS_LANGUAGE)
with open(
    Path(__file__).parent / "../../rustv1/examples/aurora/src/aurora_scenario/mod.rs",
    "rb",
) as file:
    source = file.read()

tree = parser.parse(source)

query = RS_LANGUAGE.query(
    """
    (
        (function_item
            name: (identifier) @name (#eq? @name "set_engine")
        ) @fn
    )"""
)
# query = RS_LANGUAGE.query("""(function_item @fn name: (identifier) @name)""")
captures = query.captures(tree.root_node)
print(
    "\n".join(
        [str(capture[0].text, "utf-8") for capture in captures if capture[1] == "fn"]
    )
)

# print(tree.root_node.sexp())
