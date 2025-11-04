import os
from pathlib import Path
from typing import Generator, List
from tree_sitter import Language, Parser, Tree, Node
import tree_sitter_python

def extract_paths_from_markdown(markdown_file: str, sdk_name: str) -> dict:
    """Extract paths from premium-ex.md for given SDK"""
    paths = {"basics": [], "feature-scenario": [], "complex-feature-scenario": []}
    
    with open(markdown_file, 'r') as f:
        content = f.read()
    
    current_section = None
    for line in content.split('\n'):
        line = line.strip()
        if line.startswith('## basics:'):
            current_section = "basics"
        elif line.startswith('## feature-scenario:'):
            current_section = "feature-scenario"
        elif line.startswith('## complex-feature-scenario:'):
            current_section = "complex-feature-scenario"
        elif line.startswith('##'):
            current_section = None
        elif current_section and line.startswith('/'):
            paths[current_section].append(f"./{sdk_name}{line}")
    
    return paths

def extract_subtrees(tree: Tree) -> List[Node]:
    """Extract terminal subtrees from AST"""
    terminal = [
        'function_definition', 'async_function_definition', 'class_definition',
        'if_statement', 'while_statement', 'for_statement', 'try_statement',
        'with_statement', 'import_statement', 'import_from_statement'
    ]
    
    def extract_subtree(subtree_root):
        queue = [subtree_root]
        subtree_nodes = []
        while queue:
            current_node = queue.pop(0)
            for child in current_node.children:
                if str(child.type) not in ["\n"]:
                    queue.append(child)
                if str(child.type) in terminal:
                    subtree_nodes.append(child)
        return subtree_nodes

    root = tree.root_node
    all_subtrees = []
    queue = [root]
    while queue:
        current_node = queue.pop(0)
        if str(current_node.type) in terminal:
            all_subtrees.append(current_node)
        else:
            subtree = extract_subtree(current_node)
            all_subtrees.extend(subtree)
            children = [x for x in current_node.children]
            queue.extend(children)
    return all_subtrees

def process_python_file(file_path: str, level: str):
    PY_LANGUAGE = Language(tree_sitter_python.language())
    parser = Parser()
    parser.language = PY_LANGUAGE
    
    code = Path(file_path).read_text()
    tree = parser.parse(bytes(code, "utf8"))
    subtrees = extract_subtrees(tree)
    
    os.makedirs(f"./extracted_snippets/{level}", exist_ok=True)
    
    for i, subtree in enumerate(subtrees):
        chunk_text = code[subtree.start_byte:subtree.end_byte]
        output_file = f"./extracted_snippets/{level}/{Path(file_path).stem}_chunk_{i}_{subtree.type}.py"
        
        with open(output_file, 'w') as f:
            f.write(chunk_text)
    except Exception as e:
        print(f"Error processing {file_path}: {e}")

def main():
    sdk_name = os.environ.get('sdk_name', 'python')
    markdown_file = f"./{sdk_name}/premium-ex.md"
    paths = extract_paths_from_markdown(markdown_file, sdk_name)
    
    for level, directories in paths.items():
        for directory in directories:
            if os.path.exists(directory):
                for root, dirs, files in os.walk(directory):
                    for file in files:
                        if file.endswith('.py'):
                            py_file = os.path.join(root, file)
                            process_python_file(py_file, level)

if __name__ == "__main__":
    main()