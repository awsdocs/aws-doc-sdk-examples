import os
import re

def create_files_from_md(file_path, target_dir):
    # Create the target directory if it doesn't exist
    os.makedirs(target_dir, exist_ok=True)

    # Read the content of the markdown file
    try:
        with open(file_path, 'r', encoding='utf-8') as file:
            content = file.read()
    except FileNotFoundError:
        print(f"File {file_path} not found.")
        return

    # Regex to find <file>...</file> blocks
    file_blocks = re.findall(r'<file>(.*?)</file>', content, re.DOTALL)

    for block in file_blocks:
        # Extract the name and contents of the file
        name = re.search(r'<name>(.*?)</name>', block, re.DOTALL)
        contents = re.search(r'<contents>(.*?)</contents>', block, re.DOTALL)

        if name and contents:
            name = name.group(1).strip()
            contents = contents.group(1).strip()
            file_path = os.path.join(target_dir, name)

            # Write the filtered contents to the respective file
            try:
                with open(file_path, 'w', encoding='utf-8') as new_file:
                    new_file.write(contents)
                    print(f"File created: {file_path}")
            except IOError as e:
                print(f"Failed to create file {name}. Error: {e}")
        else:
            print("Name or contents missing in one of the <file> tags.")

if __name__ == '__main__':
    create_files_from_md('04_PROCESS.md', 'app')

