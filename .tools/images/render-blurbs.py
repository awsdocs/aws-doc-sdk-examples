import os

import jinja2
import yaml
from pathlib import Path

env = jinja2.Environment(
    loader=jinja2.FileSystemLoader(os.path.dirname(__file__)),
    autoescape=jinja2.select_autoescape(
        enabled_extensions=("html", "xml"), default_for_string=True
    ),
)

if __name__ == "__main__":
    sdk_metadata = Path(__file__).parent / ".." / ".." / "metadata" / "sdks.yaml"
    with open(sdk_metadata, "r") as file:
        metadata = yaml.safe_load(file)
        for language in metadata.keys():
            shortname = metadata[language]["property"]
            template = env.get_template("template.txt")
            print(template.render(language=language, shortname=shortname))
