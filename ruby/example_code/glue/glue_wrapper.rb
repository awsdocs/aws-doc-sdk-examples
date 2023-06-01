require 'aws-sdk-glue'

class GlueWrapper
  def initialize(glue_client)
    @glue_client = glue_client
  end

  def get_crawler(name)
    crawler = nil
    begin
      response = @glue_client.get_crawler(name: name)
      crawler = response.crawler
    rescue Aws::Glue::Errors::EntityNotFoundException
      puts "Crawler #{name} doesn't exist."
    rescue StandardError => e
      puts "Couldn't get crawler #{name}. Here's why: #{e.message}"
      raise
    end
    crawler
  end

  def create_crawler(name, role_arn, db_name, db_prefix, s3_target)
    begin
      @glue_client.create_crawler(
        name: name,
        role: role_arn,
        database_name: db_name,
        targets: {
          s3_targets: [
            {
              path: s3_target
            }
          ]
        }
      )
    rescue StandardError => e
      puts "Couldn't create crawler. Here's why: #{e.message}"
      raise
    end
  end

  def start_crawler(name)
    begin
      @glue_client.start_crawler(name: name)
    rescue StandardError => e
      puts "Couldn't start crawler #{name}. Here's why: #{e.message}"
      raise
    end
  end

  def get_database(name)
    begin
      response = @glue_client.get_database(name: name)
      response.database
    rescue StandardError => e
      puts "Couldn't get database #{name}. Here's why: #{e.message}"
      raise
    end
  end

  def get_tables(db_name)
    begin
      response = @glue_client.get_tables(database_name: db_name)
      response.table_list
    rescue StandardError => e
      puts "Couldn't get tables #{db_name}. Here's why: #{e.message}"
      raise
    end
  end

  def create_job(name, description, role_arn, script_location)
    begin
      @glue_client.create_job(
        name: name,
        description: description,
        role: role_arn,
        command: {
          name: 'glueetl',
          script_location: script_location,
          python_version: '3'
        },
        glue_version: '3.0'
      )
    rescue StandardError => e
      puts "Couldn't create job #{name}. Here's why: #{e.message}"
      raise
    end
  end

  def start_job_run(name, input_database, input_table, output_bucket_name)
    begin
      response = @glue_client.start_job_run(
        job_name: name,
        arguments: {
          '--input_database': input_database,
          '--input_table': input_table,
          '--output_bucket_url': "s3://#{output_bucket_name}/"
        }
      )
      response.job_run_id
    rescue StandardError => e
      puts "Couldn't start job run #{name}. Here's why: #{e.message}"
      raise
    end
  end

  def list_jobs
    begin
      response = @glue_client.list_jobs
      response.job_names
    rescue StandardError => e
      puts "Couldn't list jobs. Here's why: #{e.message}"
      raise
    end
  end

  def get_job_runs(job_name)
    begin
      response = @glue_client.get_job_runs(job_name: job_name)
      response.job_runs
    rescue StandardError => e
      puts "Couldn't get job runs. Here's why: #{e.message}"
    end
  end
end
