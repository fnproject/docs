#!/usr/bin/ruby

require 'rubygems'
require 'kramdown'
require 'pathname'


# Script to convert all the db directories into an index
class FnCliGen

    attr_accessor :contentArr, :fileTemplateArr

    def initialize(aCmdListFile)
        @cmdListFile = aCmdListFile # Get command list
        @cmdListArr = Array.new # Array of commands from file
        @fileNameArr = Array.new  # Array of output filenames
        @fnVersion = getFnVersion

        # @fileOutArr == Temp output array
        @cmdListArr = IO.readlines(File.expand_path(@cmdListFile))
        @cmdListArr.each {|command| command.gsub!("\n", "")}
        @fileNameArr = Marshal.load(Marshal.dump(@cmdListArr))

    end

    def getFnVersion
        # Get Current Fn Version
        tempArr = []
        tempArr.push(`fn version`)
        tempOutStr = tempArr[0].match(/.*(version.*)/)[1]
        return tempOutStr
    end



    # createFileNameArr
    # Creates an array of file names replacing space with dash.

    def createFileNameArr
        # Build Mirror Array of Filenames
        @fileNameArr.each do |filename|
            filename.gsub!(" ", "-")
        end

    end

    def buildFnCmdCard
        x = 0
        fileOutArr = Array.new

        # Build each Fn Command Card
        @cmdListArr.each do |command|
            # Start markdown file
            fileOutArr.push "# `" + command + "`\n\n"  #title
            fileOutArr.push "```c\n"  # Start help text code block
            fileOutArr.push "$ " + command + "\n"  # Command
            command = command + " --help"
            fileOutArr.push `#{command}`  # Execute command, get result
            fileOutArr.push "```\n\n"  # End Code block

            # Link code goes here
            fileOutArr.push "[Some link](#)\n\n"

            # Write .md file to disk
            puts "Writing: " + @fileNameArr[x] + ".md"
            File.open(@fileNameArr[x] + ".md", "w") do |f|
                f.puts(fileOutArr)
            end

            fileOutArr.clear # Clear array for next loop
            x = x + 1  # Counter for syncing arrays
        end
    end


    # Build README.md file for all commands.
    def buildReadMeIndex
        x = 0
        fileOutArr = Array.new

        # Build each Fn Command Card
        fileOutArr.push "# Fn Command Reference\n\n"
        @cmdListArr.each do |command|
            # Start markdown file
            fileOutArr.push "[" + command + "](" + @fileNameArr[x] + ".md" + ")  \n"
            x = x + 1  # Counter for syncing arrays
        end

        # Write README.md file to disk
        puts "Writing: " + "README" + ".md"
        File.open("README.md", "w") do |f|
            f.puts(fileOutArr)
        end

    end

    # Build Markdown Parent index file REFLIST.md containting all the .md files
    def buildParentReadMeIndex
        x = 0
        fileOutArr = Array.new

        # Build list of reference pages
        fileOutArr.push "### Fn Command Reference\n\n"
        @cmdListArr.each do |command|
            # Add each command to output
            fileOutArr.push "[" + command + "](ref/" + @fileNameArr[x] + ".md" + ")  \n"
            x = x + 1  # Counter for syncing arrays
        end
        
        # Add Fn Version
        fileOutArr.push("\n<sub>" + @fnVersion + "</sub>")


        # Write REFLIST.md file to disk
        puts "Writing: " + "REFLIST" + ".md"
        File.open("REFLIST.md", "w") do |f|
            f.puts(fileOutArr)
        end

    end

    def main
        createFileNameArr()
        buildFnCmdCard()
        buildReadMeIndex()
        buildParentReadMeIndex()
    end
end


# If the Ruby class is called from another file, the code in the if is ignored.
# Works normally if called from the command line.
if __FILE__ == $0

    if ARGV.length != 1
        puts "Usage: FnCliGen FnCommandList"
        exit
    end

    app = FnCliGen.new(ARGV[0])
    app.main
end
