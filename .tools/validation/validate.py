import argparse
from pathlib import Path
from sys import exit
from metadata_validator import validate_metadata
from project_validator import check_files, verify_sample_files


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument(
        "--quiet",
        action="store_true",
        help="Suppresses output of filenames while parsing. " "The default is False.",
    )
    parser.add_argument(
        "--root",
        default=f"{Path(__file__).parent / '..' / '..'}",
        help="The root path from which to search for files "
        "to check. The default is the current working "
        "folder.",
    )
    parser.add_argument(
        "--doc-gen",
        default=f"{Path(__file__).parent / '..' / '..' / '.doc_gen'}",
        help="The folder that contains schema and metadata files.",
        required=False,
    )
    args = parser.parse_args()
    root_path = Path(args.root).resolve()

    error_count = check_files(root_path)
    error_count += verify_sample_files(root_path)
    error_count += validate_metadata(Path(args.doc_gen))

    if error_count > 0:
        print(f"{error_count} errors found, please fix them.")
    else:
        print("All checks passed, you are cleared to check in.")

    return error_count


if __name__ == "__main__":
    exit(main())
