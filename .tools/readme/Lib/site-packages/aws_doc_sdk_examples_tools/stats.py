from typing import List
from pathlib import Path
from pprint import pformat

from .doc_gen import DocGen


def main(roots: List[str]):
    base = DocGen.empty()
    for root in roots:
        docgen_root = Path(root)
        doc_gen = base.clone().for_root(docgen_root)
        doc_gen.collect_snippets()
        print(f"Root	{docgen_root.name}")
        stats = doc_gen.stats()
        print(f"SDKs	{stats['sdks']}")
        print(f"Services	{stats['services']}")
        print(f"Examples	{stats['examples']}")
        print(f"Version	{stats['versions']}")
        print(f"Snippets	{stats['snippets']}")
        genai = pformat(dict(stats["genai"]))
        print(f"GenAI	{genai}")


if __name__ == "__main__":
    from sys import argv

    main(argv[1:])
