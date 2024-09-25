import re
from pathlib import Path

class RubocopAutoFixer:
    def __init__(self, rubocop_output, file_directory):
        self.rubocop_output = rubocop_output
        self.file_directory = Path(file_directory)
        self.violations = self.parse_violations()

    def parse_violations(self):
        violations = []
        file_pattern = r"^([^\s]+):(\d+):\d+: (C|W|E|F): ([^\[]+)"
        lines = self.rubocop_output.splitlines()
        current_file = None
        
        for line in lines:
            match = re.match(file_pattern, line)
            if match:
                file_path, line_number, level, message = match.groups()
                violations.append({
                    "file": file_path,
                    "line": int(line_number),
                    "level": level,
                    "message": message.strip(),
                })
        return violations

    def apply_fixes(self):
        for violation in self.violations:
            if "Line is too long" in violation['message']:
                self.fix_line_length(violation)
            elif "Avoid parameter lists longer than" in violation['message']:
                self.fix_parameter_list(violation)
            elif "Do not prefix reader method names with get_" in violation['message']:
                self.fix_naming_convention(violation, "get_")
            elif "Do not prefix writer method names with set_" in violation['message']:
                self.fix_naming_convention(violation, "set_")
            elif "has_" in violation['message'] and "to" in violation['message']:
                self.fix_predicate_name(violation)

    def fix_line_length(self, violation):
        """ Break long lines by inserting line breaks at appropriate places """
        file_path = self.file_directory / violation['file']
        with open(file_path, 'r') as file:
            lines = file.readlines()

        long_line = lines[violation['line'] - 1]
        if len(long_line) > 120:
            # Break the line intelligently
            new_lines = self.break_long_line(long_line)
            lines[violation['line'] - 1] = new_lines
        
        with open(file_path, 'w') as file:
            file.writelines(lines)

    def break_long_line(self, line):
        """ Simple line breaker by commas for long lines """
        if ',' in line:
            return line.replace(',', ',\n')  # Basic breaking logic, you can improve it
        return line

    def fix_parameter_list(self, violation):
        """ Add a fix to split long parameter lists """
        file_path = self.file_directory / violation['file']
        with open(file_path, 'r') as file:
            lines = file.readlines()
        
        # Example of fixing parameter list by adding line breaks
        long_line = lines[violation['line'] - 1]
        if '(' in long_line and ')' in long_line:
            new_lines = long_line.replace(', ', ',\n')
            lines[violation['line'] - 1] = new_lines

        with open(file_path, 'w') as file:
            file.writelines(lines)

    def fix_naming_convention(self, violation, prefix):
        """ Rename method to follow proper naming convention """
        file_path = self.file_directory / violation['file']
        with open(file_path, 'r') as file:
            lines = file.readlines()

        target_line = lines[violation['line'] - 1]
        if prefix in target_line:
            new_line = target_line.replace(prefix, "")
            lines[violation['line'] - 1] = new_line
        
        with open(file_path, 'w') as file:
            file.writelines(lines)

    def fix_predicate_name(self, violation):
        """ Rename predicate methods like `has_` to proper names """
        file_path = self.file_directory / violation['file']
        with open(file_path, 'r') as file:
            lines = file.readlines()

        target_line = lines[violation['line'] - 1]
        if 'has_' in target_line:
            new_line = target_line.replace('has_', '')
            lines[violation['line'] - 1] = new_line

        with open(file_path, 'w') as file:
            file.writelines(lines)

    def run(self):
        print("Applying fixes...")
        self.apply_fixes()
        print("Fixes applied where possible.")

# Example usage:
rubocop_output = <RuboCop output string>
fixer = RubocopAutoFixer(rubocop_output, ".")
fixer.run()
