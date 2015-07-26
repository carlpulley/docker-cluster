# -*- mode: ruby -*-
# vi: set ft=ruby :

load("./library.rb")

VAGRANTFILE_API_VERSION = "2"

# By default, we launch the `dev` model - acceptable models are `dev` and `devops`
MODEL = ENV["MODEL"] || "dev"

# Configuration for the `dev` model is performed in docker-compose-vagrant.yaml

# Configuration for the `devops` model
if MODEL == "devops"
  nodes = [ Node.new("node-0", "192.168.42.41", 2552), Node.new("node-1", "192.168.42.42", 2552) ]
  seed_nodes = [ nodes[0].to_s ]
end

# Verify that the Vagrant plugin vagrant-docker-compose is installed
unless Vagrant.has_plugin?("vagrant-docker-compose")
  puts "WARNING: I'm about to install the vagrant-docker-compose plugin - enter yes (y) to allow this: "
  unless ["y", "yes"].include? gets.chomp.strip.downcase
    exit
  end
  `vagrant plugin install vagrant-docker-compose`
end

# Launch the model...
Vagrant.configure(VAGRANTFILE_API_VERSION) do |config|

  config.vm.box = "ubuntu-utopic64"
  config.vm.box_url = "https://cloud-images.ubuntu.com/vagrant/utopic/current/utopic-server-cloudimg-amd64-vagrant-disk1.box"

  if MODEL == "dev"

    config.vm.network "private_network", ip: "192.168.42.41"

    config.vm.provision :docker
    config.vm.provision :docker_compose, yml: "/vagrant/docker-compose-vagrant.yml", rebuild: true, project_name: "docker-cluster", run: "always"

  elsif MODEL == "devops"

    nodes.each do |node|
      config.vm.define node.name do |cluster|
        cluster.vm.network "private_network", ip: node.address

        script = <<SCRIPT
cat >/home/vagrant/docker-compose-#{node.name}.yml <<END
#{erb("./templates/node.yml.erb", :node => node, :seed_nodes => seed_nodes.join(","))}
END
SCRIPT
        cluster.vm.provision :shell, inline: script

        cluster.vm.provision :docker
        cluster.vm.provision :docker_compose, yml: "/home/vagrant/docker-compose-#{node.name}.yml", rebuild: true, project_name: "docker-cluster", run: "always"
      end
    end

  else

    puts "ERROR: `#{MODEL}` is an invalid model name - expected the environment variable MODEL to be `dev` (the default value) or `devops`"

  end

end
