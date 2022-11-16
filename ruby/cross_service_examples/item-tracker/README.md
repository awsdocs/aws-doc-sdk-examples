# Item Tracker Cross-Sercice Example

This example code comprises a "real-world" reference implementation of Amazon Relational Database Service (RDS) Aurora.

This example is an application for managing fictitious work items.
This application includes a front-end interface, a back-end API, and MySQL database implemented with Amazon RDS Aurora.

To run this example, you will use a local environment to simultaneously run a front-end React app and Sinatra API written in Ruby.

In tandem, you will create a single Aurora cluster running an RDS instance containing a table of data.
This data will undergo CRUD transformations that originate from the front-end interface and relay through the API.

This table is called `work_items` and has a schema which includes basic information like `item_id`, `creation_date`, and `assignee`.

# About the application

The basic unit of data, the work item, is modeled in [item.rb](models/item.rb). This data includes attributes such as:
* work_item_id
* created_date
* assignee

The React application is run using `resources/clients/elwing.js` and accessed via web browser on localhost:8080.

The Sinatra API is run using `app.rb` and is accessed by the React app on port 3000.

Written in Ruby, this API will interact with the Aurora Cluster using the [AWS SDK for Ruby](https://docs.aws.amazon.com/sdk-for-ruby/v3/developer-guide/welcome.html).

This SDK code can be found in `db_wrapper.rb` and primarily demonstrates the [exucute_command](https://docs.aws.amazon.com/sdk-for-ruby/v3/api/Aws/RDSDataService/Client.html#execute_statement-instance_method) method call. 