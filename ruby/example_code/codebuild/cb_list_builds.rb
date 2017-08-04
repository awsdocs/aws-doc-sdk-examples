# Copyright 2010-2017 Amazon.com, Inc. or its affiliates. All Rights Reserved.
#
# This file is licensed under the Apache License, Version 2.0 (the "License").
# You may not use this file except in compliance with the License. A copy of the
# License is located at
#
# http://aws.amazon.com/apache2.0/
#
# This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
# OF ANY KIND, either express or implied. See the License for the specific
# language governing permissions and limitations under the License.

require 'aws-sdk'
require 'os'

if OS.windows?
  Aws.use_bundled_cert!
end

# main
verbose = false

if ENV['VERBOSE'] != nil
  verbose = true
end

client = Aws::CodeBuild::Client.new(region: 'us-west-2')

begin
  resp = client.list_builds({sort_order: "ASCENDING",})

  resp.ids.each do |b|
    puts b

    if verbose
      r = client.batch_get_builds({ids: [b],})

      r.builds.each do |bld|
        puts 'Project:    ' + bld.project_name
        puts 'Start time: ' + bld.start_time.utc.strftime('%m/%d/%Y %H:%M %p')

        if bld.end_time != nil
          puts 'End time:   ' + bld.end_time.utc.strftime('%m/%d/%Y %H:%M %p')
        else
          puts 'End time:   N/A'
        end

        puts 'Phase:      ' + bld.current_phase
        puts 'Status:     ' + bld.build_status

        puts 'Phases:'
        bld.phases.each do |p|
          puts '  Phase: ' + p.phase_type #=> String, one of "SUBMITTED", "PROVISIONING", "DOWNLOAD_SOURCE", "INSTALL", "PRE_BUILD", "BUILD", "POST_BUILD", "UPLOAD_ARTIFACTS", "FINALIZING", "COMPLETED"

          if p.phase_status != nil
            puts '  Status: ' + p.phase_status #=> String, one of "SUCCEEDED", "FAILED", "FAULT", "TIMED_OUT", "IN_PROGRESS", "STOPPED"
          else
            puts '  Status: unknown'
          end

          if p.duration_in_seconds != nil
            puts '  Duration (seconds): ' + p.duration_in_seconds.to_s #=> Integer
          else
            puts '  Duration (seconds): unknown'
          end

          begin
            if p.contexts != nil
              if contexts.count > 0
                puts '  Contexts:'

                contexts.each do |c|
                  puts '    Status code:' + c.status_code
                  puts '    Message:    ' + c.message
                  puts
                end
              end
            end
          rescue
            puts p
          end

          puts
        end
      end

      puts
    end
  end

  puts
rescue Exception => ex
  puts 'Error listing builds: ' + ex.message
end
