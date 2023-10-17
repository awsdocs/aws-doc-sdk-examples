# SDK Code Examples Tools

These tools help SDK Code Example developers' integration with internal docs tooling.
This is mainly in the form of .doc_gen SOS metadata validation, with other tools as necessary.

SDK Code Examples tools team uses Python for our tools because of its cross-platform runtime and broad knowledge base.

## Tooling Guidelines

1. Use Python latest for tooling (this is not the same rule as AWS SDK for Python, be aware).
2. Our “standard library” adds `flake8`, `black`, `pyyaml`, `yamale`, and `requests`.
   - Current versions are in base_requirements.txt.
3. Run `.tools/verify_python.py` to ensure python tools are configured as expected.
   - This will install our “standard library” to site_packages and ensure that the python version is at least 3.(current - 1).
4. Use a venv in `.venv` if the project needs packages beyond our standard library.
   1. Include those libraries in the `requirements.txt`.
   2. Copy `.tools/base-requirements.txt` to the tool’s folder to seed `requirements.txt` if you like.
   3. With an active venv, run `pip freeze > requirements.txt` to update the tool’s requirements.
5. Keep tools self executable.
   1. Mark them executable (`chmod a+x <script.py>` in \*nix).
   2. Add a shebang `#!/usr/bin/env python3`.
   3. Use `__name__ == "__main__"` check that delegates to a `main` function.
6. Use f-strings for string building.
7. Use `logging` to write diagnostics to the console.
8. Format using Black with default settings.
9. Lint using `flake8` with overrides for Black default settings. Treat Warnings as errors.
10. Verify type annotations using mypy.
    - Type annotations are strongly recommended.
11. Run tests using pytest.
12. Parse arguments using argparse.
    - All scripts must run headless, without user interaction.
13. Use pathlib for files and paths.
    - Prefer os.scandir to os.listdir.
14. Parse & dump yaml using PyYAML (imports from `yaml`).
    - Validate yaml schemas using `yamale`.
15. When working with dates, import from `datetime` and always include a timezone (usually `timezone.utc`).
16. Prefer data classes or immutable tuples for base data types.
17. Use requests for web calls, but prefer a native wrapper (like boto3 or pygithub) when available.
    - If you http/2, you might consider httpx.
18. Use subprocess for system calls, but prefer a native wrapper (like gitpython) when available.

## Some PEPs you might want to know about:

Python 3.12:

- [PEP 701](http://www.python.org/dev/peps/pep-0701) -- More flexible f-string parsing.
- [PEP 695](http://www.python.org/dev/peps/pep-0695) -- New type annotations syntax for generic classes.

Python 3.11:

- [PEP 654](http://www.python.org/dev/peps/pep-0654) -- Exception Groups and except\*.

Python 3.10:

- [PEP 604](http://www.python.org/dev/peps/pep-0604) -- Allow writing union types as X | Y.
- [PEP 636](http://www.python.org/dev/peps/pep-0636) -- Structural Pattern Matching: Tutorial.

Python 3.9:

- [PEP 585](http://www.python.org/dev/peps/pep-0585), Type Hinting Generics In Standard Collections.
- [PEP 602](http://www.python.org/dev/peps/pep-0602), Python adopts a stable annual release cadence.

Python 3.8

- [PEP 572](http://www.python.org/dev/peps/pep-0572), Assignment expressions.
- [PEP 586](http://www.python.org/dev/peps/pep-0586), Literal types.
- [PEP 589](http://www.python.org/dev/peps/pep-0589), TypedDict.

Python 3.7

- [PEP 557](http://www.python.org/dev/peps/pep-0557), Data Classes.

## VSCode Extensions

- https://marketplace.visualstudio.com/items?itemName=ms-python.mypy-type-checker
- https://marketplace.visualstudio.com/items?itemName=ms-python.flake8
- https://marketplace.visualstudio.com/items?itemName=ms-python.black-formatter
