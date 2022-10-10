import { Box, Header, Link, SpaceBetween } from "@cloudscape-design/components";
export function Readme() {
  return (
    <SpaceBetween size="l">
      <Header variant="h1">Work Item Tracker</Header>
      <Box variant="p">
        A sample application that shows you how to track work items served by a
        REST endpoint.
      </Box>
      <Box variant="p">
        <Header variant="h2">Services used</Header>
        <ul>
          <li>
            <Link external href="https://docs.aws.amazon.com/rds/index.html">
              Aurora RDS
            </Link>
          </li>
          <li>
            <Link external href="https://docs.aws.amazon.com/ses/index.html">
              Simple Email Service
            </Link>
          </li>
        </ul>
      </Box>
      <Box variant="p">
        <Header variant="h2">Available backends</Header>
        <ul>
          <li>
            <Link
              external
              href="https://github.com/awsdocs/aws-doc-sdk-examples/tree/2142ca5b294e06b719b6e22a38339e86050d8fde/javav2/usecases/Creating_Spring_RDS_%20Rest"
            >
              Java Spring
            </Link>
          </li>
          <li>
            <Link
              external
              href="https://github.com/awsdocs/aws-doc-sdk-examples/tree/84ea245588ffe9043ed162f211cbfdc91f952640/kotlin/usecases/serverless_rds"
            >
              Kotlin
            </Link>
          </li>
        </ul>
      </Box>
    </SpaceBetween>
  );
}
