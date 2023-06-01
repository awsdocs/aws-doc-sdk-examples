require 'aws-sdk-glue'
require 'aws-sdk-iam'
require 'aws-sdk-s3'
require 'io/console'
require 'pp'
require 'logger'
require 'optparse'
require 'cli/ui'
require 'yaml'
require 'pry'

require_relative('glue_wrapper')

@logger = Logger.new($stdout)
@logger.level = Logger::WARN

# snippet-start:[ruby.example_code.glue.Scenario_GetStartedCrawlersJobs]
class GlueCrawlerJobScenario
  def initialize(glue_client, glue_service_role, glue_bucket)
    @glue_client = glue_client
    @glue_service_role = glue_service_role
    @glue_bucket = glue_bucket
  end

  def wait(seconds, tick = 12)
    progress = '|/-\\'
    waited = 0
    while waited < seconds
      tick.times do |frame|
        print "\r#{progress[frame % progress.length]}"
        sleep(1.0 / tick)
      end
      waited += 1
    end
  end

  def upload_job_script(file_path)
    begin
      File.open(file_path) do |file|
      @glue_bucket.client.put_object({
        body: file_path,
        bucket: @glue_bucket.name,
        key: file_path
      })
      end
      puts "Uploaded job script '#{file_path}' to the example bucket."
    rescue Aws::S3::Errors::S3UploadFailedError => e
      puts "Couldn't upload job script. Here's why: #{e.message}"
      raise
    end
  end

  def run(crawler_name, db_name, db_prefix, data_source, job_script, job_name)
    wrapper = GlueWrapper.new(@glue_client)
    puts "Checking for crawler #{crawler_name}."
    crawler = wrapper.get_crawler(crawler_name)
    if crawler.nil?
      puts "Creating crawler #{crawler_name}."
      wrapper.create_crawler(crawler_name, @glue_service_role.arn, db_name, db_prefix, data_source)
      puts "Created crawler #{crawler_name}."
      crawler = wrapper.get_crawler(crawler_name)
    end
    pp crawler
    puts '-' * 88

    puts "When you run the crawler, it crawls data stored in #{data_source} and creates a metadata database in the AWS Glue Data Catalog that describes the data in the data source."
    puts "In this example, the source data is in CSV format."
    ready = false
    until ready
      print 'Ready to start the crawler? (y/n) '
      ready = is_yesno(gets.chomp)
    end
    wrapper.start_crawler(crawler_name)
    puts "Let's wait for the crawler to run. This typically takes a few minutes."
    crawler_state = nil
    while crawler_state != 'READY'
      wait(10)
      crawler = wrapper.get_crawler(crawler_name)
      crawler_state = crawler['State']
      puts "Crawler is #{crawler['State']}."
    end
    puts '-' * 88

    database = wrapper.get_database(db_name)
    puts "The crawler created database #{db_name}:"
    pp database
    puts 'The database contains these tables:'
    tables = wrapper.get_tables(db_name)
    tables.each_with_index do |table, index|
      puts "\t#{index + 1}. #{table['Name']}"
    end
    table_index = 0
    until table_index.between?(1, tables.length)
      print 'Enter the number of a table to see more detail: '
      table_index = gets.chomp.to_i
    end
    pp tables[table_index - 1]
    puts '-' * 88

    puts "Creating job definition #{job_name}."
    wrapper.create_job(job_name, 'Getting started example job.', @glue_service_role.arn, "s3://#{@glue_bucket.name}/#{job_script}")
    puts 'Created job definition.'
  end

end
def main
  puts '-' * 88
  puts "Welcome to the AWS Glue getting started with crawlers and jobs scenario."
  puts '-' * 88

  resource_names = YAML.load_file('resource_names.yaml')
  job_script_filepath = 'job_script.py'

  glue_client = Aws::Glue::Client.new(region: 'us-east-1')

  iam = Aws::IAM::Resource.new(region: 'us-east-1')
  iam_role_name = resource_names['glue_service_role']
  iam_role = iam.role(iam_role_name)

  s3 = Aws::S3::Resource.new(region: 'us-east-1')
  s3_bucket_name = resource_names['glue_bucket']
  s3_bucket = s3.bucket(s3_bucket_name)

  scenario = GlueCrawlerJobScenario.new(glue_client, iam_role, s3_bucket)

  scenario.upload_job_script(job_script_filepath)

  scenario.run(
    'doc-example-crawler',
    'doc-example-database',
    'doc-example-',
    's3://crawler-public-us-east-1/flight/2016/csv',
    job_script_filepath,
    'doc-example-job'
  )

  puts '-' * 88
  puts "To destroy scaffold resources, including the IAM role and S3 bucket used in this scenario, run 'ruby scaffold.rb destroy'."
  puts "\nThanks for watching!"
  puts '-' * 88
# rescue Exception => e
#   @logger.error("Something went wrong with the example:\n #{e}")
end

if __FILE__ == $0
  main
end