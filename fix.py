import ast
import os
import fnmatch

class FStringFixer(ast.NodeVisitor):
    def __init__(self):
        self.f_strings_without_placeholders = []

    def visit_JoinedStr(self, node):
        # Check if f-string has no placeholder (i.e., no expressions inside curly braces)
        if all(isinstance(value, ast.Str) for value in node.values):
            self.f_strings_without_placeholders.append((node.lineno, node.col_offset))
        self.generic_visit(node)

def fix_f_strings_in_file(filename):
    with open(filename, "r") as file:
        source = file.read()

    try:
        tree = ast.parse(source)
    except SyntaxError:
        print(f"Skipping file due to syntax error: {filename}")
        return

    fixer = FStringFixer()
    fixer.visit(tree)

    lines = source.splitlines()

    # Fix each f-string without placeholders
    for lineno, col_offset in fixer.f_strings_without_placeholders:
        line = lines[lineno - 1]
        # Replace f-string (e.g., f"Hello") with a regular string (e.g., "Hello")
        lines[lineno - 1] = line[:col_offset] + line[col_offset:].replace('f"', '"', 1)

    # Write the modified content back to the file
    with open(filename, "w") as file:
        file.write("\n".join(lines))

def fix_f_strings_in_directory(directory):
    # Walk through the directory and all subdirectories
    for root, dirs, files in os.walk(directory):
        for filename in fnmatch.filter(files, "*.py"):
            filepath = os.path.join(root, filename)
            print(f"Processing {filepath}...")
            fix_f_strings_in_file(filepath)

if __name__ == "__main__":
    import sys
    if len(sys.argv) != 2:
        print("Usage: python fix_fstrings.py <directory>")
    else:
        fix_f_strings_in_directory(sys.argv[1])