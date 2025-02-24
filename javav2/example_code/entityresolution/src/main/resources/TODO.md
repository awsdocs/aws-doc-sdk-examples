# Suggestions to improve the scenario

Need to delete the schema mapping when you delete the workflow.

Use two input data sources, since that is what a customer would do at a minimum. The input data for the scenario should contain records that do 
and don't match. Make the second data source in CSV.

When the job completes, display the results from the S3 bucket--both success and error.