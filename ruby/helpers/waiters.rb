# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0
def custom_wait(seconds, tick = 12)
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
