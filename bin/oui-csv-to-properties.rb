#!/usr/bin/env ruby
#
# Copyright (C) 2016 Neo Visionaries Inc.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
# either express or implied. See the License for the specific
# language governing permissions and limitations under the
# License.

# coding: utf-8
Encoding.default_external = 'utf-8'

require 'date'
require 'fileutils'
require 'optparse'
require 'digest/sha1'

#==================================================
# Options
#==================================================
class Options < OptionParser
  attr_accessor :input_file, :output_file, :verbose

  def initialize
    super

    # The top directory of this source tree.
    top_dir = File.dirname(File.dirname($0))

    # Set the default values.
    @input_file  = "#{top_dir}/data/oui.csv"
    @output_file = "#{top_dir}/src/main/resources/com/neovisionaries/oui/oui.properties"
    @verbose     = 0

    # Option to specify an input file.
    on('-i FILE', '--input FILE',
       "Specify an input file. The default value is '#{@input_file}'.") do |file|
      @input_file = file
    end

    # Option to specify an output file.
    on('-o FILE', '--output FILE',
       "Specify an output file. The default value is '#{@output_file}'. '-' means the standard output.") do |file|
      @output_file = file
    end

    # Option to make this application verbose.
    on('-v', '--verbose', "Make this application verbose.") do
      @verbose += 1
    end
  end

  def self.parse(argv)
    options = Options.new
    options.parse(argv)

    if 0 < options.verbose
      STDERR.puts "Input file  = #{options.input_file}"
      STDERR.puts "Output file = #{options.output_file}"
    end

    return options
  end
end


#==================================================
# InputData
#==================================================
class InputData
  attr_accessor :number_to_name_hash

  # The pattern of data lines.
  DATA_LINE_PATTERN = /^MA-L,(\h{6}),(.+)$/
  DOUBLE_QUOTE      = '"'
  COMMA             = ','

  def initialize
    @number_to_name_hash = Hash.new
  end

  def add(number, name)
    @number_to_name_hash[number] = name
  end

  def self.parse(input_file)
    data = InputData.new

    # Open the input file.
    File.open(input_file, 'r:utf-8') do |file|
      # Line counter.
      line_number = 0;

      # For each line in the input file.
      file.each_line do |line|
        # Increment the line number.
        line_number += 1

        # Parse the line.
        line_data = parse_line(line_number, line)

        if line_data.nil? == false
          data.add(*line_data)
        end
      end
    end

    return data
  end

  def self.count_leading_char(str, char)
    count = 0

    str.each_char do |ch|
      if ch == char
        count += 1
      else
        break
      end
    end

    return count
  end

  def self.extract_field(input)
    quoted = (count_leading_char(input, DOUBLE_QUOTE) % 2) == 1
    index  = quoted ? 1 : 0
    output = ""

    while index < input.length
      ch = input[index]

      if ch == DOUBLE_QUOTE
        if ((index + 1) < input.length) and (input[index + 1] == DOUBLE_QUOTE)
          output << DOUBLE_QUOTE
          index += 2
          next
        end
        break
      end

      if (ch == COMMA) and (quoted == false)
        break
      end

      output << ch

      index += 1
    end

    return output
  end

  def self.parse_line(line_number, line)
    match = line.match(DATA_LINE_PATTERN)

    # If the line does not match the pattern.
    if match.nil?
      if (line_number != 1)
        STDERR.puts "Line##{line_number} does not match the pattern: #{line}"
      end

      return nil
    end

    number = match[1]
    name   = extract_field(match[2]).strip

    if 1 < $options.verbose
      STDERR.puts "Line##{line_number}: #{number} -> #{name}"
    end

    return [number, name]
  end
end


#==================================================
# Generator
#==================================================
class Generator
  def self.generate(input)
    if $options.output_file == '-'
      # Write a class to the standard output.
      write_to(input, STDOUT)
      return
    end

    # Create the directory of the output file if it does not exist.
    create_directory_if_not_exist()

    File.open($options.output_file, 'w') do |file|
      write_to(input, file)
    end
  end

  def self.create_directory_if_not_exist
    # The directory of the output file.
    dir = File.dirname($options.output_file)

    # If the directory already exists.
    if Dir.exists?(dir)
      # No need to create the directory.
      return
    end

    # Create the directory.
    FileUtils.mkdir_p(dir)
  end

  def self.write_to(input, output)
    number_to_name_array = input.number_to_name_hash.sort

    write_header(number_to_name_array, output)
    write_data(number_to_name_array, output)
  end

  def self.compute_digest(number_to_name_array)
    digest = Digest::SHA1.new

    number_to_name_array.each do |element|
      digest.update(element[0])
      digest.update(element[1])
    end

    return digest.hexdigest
  end

  def self.write_header(number_to_name_array, output)
    output.print <<-HEADER
# Generated on: #{Time.now.utc.to_s}
# Entry count:  #{number_to_name_array.length}
# Data digest:  #{compute_digest(number_to_name_array)}

    HEADER
  end

  def self.write_data(number_to_name_array, output)
    number_to_name_array.each do |element|
      number = element[0]
      name   = to_property_value(element[1]).strip
      output.print("#{number} = #{name}\n")
    end
  end

  def self.to_property_value(str)
    value = ""

    str.codepoints.each do |codepoint|
      if codepoint <= 0x7F
        value << codepoint.chr
      elsif codepoint <= 0xFFFF
        value << sprintf("\\u%04X", codepoint)
      else
        tmp   = codepoint - 0x10000
        upper = (tmp / 0x400).floor + 0xD800
        lower = (tmp % 0x400)       + 0xDC00

        value << sprintf("\\u%04X\\u%04X", upper, lower)
      end
    end

    return value
  end
end


#==================================================
# App
#==================================================
class App
  def main(argv)
    # Parse the command line arguments.
    $options = Options.parse(argv)

    # Parse the input data.
    input = InputData.parse($options.input_file)

    # Generate an output file.
    Generator.generate(input)
  end
end


#==================================================
# S T A R T
#==================================================
App.new.main(ARGV)
