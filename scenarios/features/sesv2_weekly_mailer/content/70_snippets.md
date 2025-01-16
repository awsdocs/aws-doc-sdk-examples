---
skip: true
prompt: |
  Create an example block of YAML to describe metadata snippets. This block
  describes one example for the EC2 StartInstances action, in Java, Python, and Rust.
---

```yaml
c2_StartInstances:
  title: Start an &EC2; instance using an &AWS; SDK
  title_abbrev: Start an instance
  synopsis: start an &EC2; instance.
  category:
  languages:
    Java:
      versions:
        - sdk_version: 2
          github: javav2/example_code/ec2
          sdkguide:
          excerpts:
            - description:
              snippet_tags:
                - ec2.java2.scenario.start_instance.main
    Python:
      versions:
        - sdk_version: 3
          github: python/example_code/ec2
          sdkguide:
          excerpts:
            - description:
              snippet_tags:
                - python.example_code.ec2.InstanceWrapper.decl
                - python.example_code.ec2.StartInstances
    Rust:
      versions:
        - sdk_version: 1
          github: rustv1/examples/ec2
          excerpts:
            - description:
              snippet_tags:
                - ec2.rust.start-instance
  services:
    ec2: { StartInstances }
```
