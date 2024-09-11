import os
import re

# Define the root directory for the project
PROJECT_ROOT = os.path.abspath(".")

# Pattern to detect f-strings that do not contain any placeholders
FSTRING_NO_PLACEHOLDER_PATTERN = re.compile(r'f"([^{}]*)"')


def fix_fstring_no_placeholders(file_content):
    """
    Replace all unnecessary f-strings (those without placeholders) with regular strings.
    Example: "Enter the code" -> "Enter the code"
    """
    # Replace all f-strings without placeholders by removing the `f` prefix
    content = FSTRING_NO_PLACEHOLDER_PATTERN.sub(r'"\1"', file_content)

    return content


def process_file(file_path):
    """Process each file and fix F541 violations (f-strings missing placeholders)."""
    with open(file_path, "r", encoding="utf-8") as f:
        content = f.read()

    # Fix f-strings with no placeholders
    fixed_content = fix_fstring_no_placeholders(content)

    # If the content has changed, write it back to the file
    if fixed_content != content:
        with open(file_path, "w", encoding="utf-8") as f:
            f.write(fixed_content)
        print(f"Fixed f-strings in: {file_path}")


def process_directory(directory):
    """Recursively process all Python files in the directory."""
    for root, dirs, files in os.walk(directory):
        for file in files:
            if file.endswith(".py"):
                file_path = os.path.join(root, file)
                process_file(file_path)


if __name__ == "__main__":
    # Start processing from the top-level directory
    process_directory(PROJECT_ROOT)
