import jinja2
import yaml
import os

env = jinja2.Environment(
    loader=jinja2.FileSystemLoader(os.path.dirname(__file__))
)

with open('../metadata/sdks.yaml', 'r') as file:
    metadata = yaml.safe_load(file)
    for language in metadata.keys():
        metadata[language]
        shortname = metadata[language]['property']
        template = env.get_template("template.txt")
        print(template.render(language=language, shortname=shortname))



