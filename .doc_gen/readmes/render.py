from ipaddress import v6_int_to_packed
import jinja2
import yaml
import os

env = jinja2.Environment(
    loader=jinja2.FileSystemLoader(os.path.dirname(__file__))
)
template = env.get_template("readme.txt")

# get language details
with open("language.yml") as file:
    language = yaml.safe_load(file)

# sear
for service in language["covered_services"]:
    
    # get general service data
    with open("general.yml") as file:
        general = yaml.safe_load(file)
        for x in general['services']:
            if service in x['keyword']:
                service_data = x

    # get service data
    with open("services.yml") as file:
        services = yaml.safe_load(file)
        
        # get service actions
        for k, v in services['actions'].items():
            if k in service:
                actions = v
    
        # get service scenarios
        scenarios = ''
        for k, v in services['scenarios'].items():
            if k in service:
                scenarios = v

    # determine which code running instructions apply to service code
    if scenarios:
        code_instructions = language["running_code"][0]["scenario"]
    else:
        code_instructions = language["running_code"][0]["no_scenario"]

    # determine which testing instructions apply to service code
    base = f"../../{language['name']}/example_code/{service_data['keyword']}"
    dirs = [x[0] for x in os.walk(base)]
    if 'test' in base or 'tests' in base:
        test_instructions = language["tests"][0]["minitest"]
    elif 'spec' in base or 'specs' in base:
        test_instructions = language["tests"][0]["rspec"]
    else:
        test_instructions = language["tests"][0]["no_tests"]

    readme_text = template.render(
        language_name=language["name"],
        short_service_name = service_data["subsequent_use"],
        code_examples_actions = service_data["code_examples_actions"],
        service_blurb_from_website = service_data["service_blurb_from_website"],
        service_link = service_data["service_link"],
        short_sdk_name=language["short_sdk_name"],
        code_instructions=code_instructions,
        test_instructions=test_instructions,
        service_developer_guide=language["service_developer_guide"],
        service_api_reference_guide=language["service_api_reference_guide"],
        language_sdk_reference_guide=language["language_sdk_reference_guide"],
        actions=actions,
        scenarios=scenarios,
        )
    # breakpoint()
    with open(f'{base}/README.md', 'w') as f:
        f.write(readme_text)