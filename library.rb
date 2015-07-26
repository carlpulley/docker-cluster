require 'erb'
require 'ostruct'

class Node
  attr_accessor :name, :address, :port

  def initialize(name, address, port)
    raise "ERROR: name `#{name}` should only contain alphanumerics, underscores and minus" unless name =~ /^[a-zA-Z0-9_-]+$/
    raise "ERROR: address `#{address}` should be an IPv4 formatted address" unless address =~ /^[0-9]+\.[0-9]+\.[0-9]+\.[0-9]+$/
    raise "ERROR: port `#{port}` should be a positive integer" unless port > 0

    @name = name
    @address = address
    @port = port
  end

  def to_s
    "#{@address}:#{@port}"
  end
end

def erb(template, vars)
  ERB.new(File.read(template)).result(OpenStruct.new(vars).instance_eval { binding })
end
