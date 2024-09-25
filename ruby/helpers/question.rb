# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# Purpose
#
# Helper class for running scenarios at a command prompt. Asks questions, validates
# and converts input, and returns answers.

# snippet-start:[ruby.example_code.dynamodb.helper.Question]
# Asks a single question and validates it against a list of validators.
# When an answer fails validation, the complaint is printed and the question
# is asked again.
#
# @param question [String] The question to ask.
# @param validators [Array] The list of validators that the answer must pass.
# @return The answer, converted to its final form by the validators.
class Question
  def self.ask(question, *validators)
    answer = nil
    while answer.nil?
      puts(question)
      answer = gets.chomp
      validators.unshift(method(:non_empty)) unless validators[0] == method(:non_empty)
      validators.each do |validator|
        answer, complaint = validator.call(answer)
        if answer.nil?
          puts(complaint)
          break
        end
      end
    end
    answer
  end
end

# Validates that the answer is not empty.
# @return [Array] The non-empty answer, or nil.
def non_empty(answer)
  answer = nil unless answer != ''
  [answer, 'I need an answer. Please?']
end

# Validates a yes/no answer.
# @return [Array] True when the answer is 'y'; otherwise, False.
def is_yesno(answer)
  [answer.downcase == 'y', '']
end

# Validates that the answer can be converted to an int.
# @return [Array] The int answer; otherwise, nil.
def is_int(answer)
  int_answer = answer.to_i
  int_answer = nil if int_answer.zero?
  [int_answer, "#{answer} must be a valid integer."]
end

# Validates that the answer can be converted to a float.
# :return [Array] The float answer; otherwise, None.
def is_float(answer)
  float_answer = answer.to_f
  float_answer = nil if float_answer == 0.0
  [float_answer, "#{answer} must be a valid float."]
end

# Validates that the answer is within a range. The answer must be of a type that can
# be compared to the lower and upper bounds.
# @return [Proc] A Proc that can be called to determine whether the answer is within
#                the expected range.
def in_range(lower, upper)
  proc do |answer|
    range_answer = answer.between?(lower, upper) ? answer : nil
    [range_answer, "#{answer} must be between #{lower} and #{upper}."]
  end
end
# snippet-end:[ruby.example_code.dynamodb.helper.Question]
