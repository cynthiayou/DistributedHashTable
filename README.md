# Implemented various Distributed Hash Table (DHT)  and their corresponding load balancing solutions in Cloud storage systems
# This is to mimic the DHT solutions in Swift, Cassandra, Redis, Ceph(CRUSH)

## test
`test` folder includes test_single.sh which builds and runs a single server with three clients writing and reading random files.  In the `test\log` output logs are written.

## version
Latest release v0.1

## switch folder
cd /src   



## compile and run Ring server/ Data Node / Client

#1 compile Ring DHT
javac -classpath ../lib/\* dht/Ring/*.java dht/server/Command.java dht/common/Hashing.java dht/common/response/*.java storage_server/Datum.java control_client/control_client.java

#2 start all data node proceses residing on the same virtual machine of proxy server (without DHT)
java -classpath .:../lib/\* dht/Ring/ProxyServer run datanode

#2a start a single data node with specific IP and port
java -classpath .:../lib/\* dht/Ring/DataNode <IP> <port> <hashRange>

#3 run proxy server (pushing DHT to all data nodes)
java -classpath .:../lib/\* dht/Ring/ProxyServer

#4 start a normal client to connect to a data node with specific IP and port
java -classpath .:../lib/\* dht/Ring/client <IP> <port>

#4a start a normal client, connecting to a specific IP and port when reading or writing 
java -classpath .:../lib/\* dht/Ring/client



## compile and run Rush server

#1 compile Rush DHT
javac -classpath ../lib/\* dht/rush/*.java dht/rush/clusters/*.java dht/rush/commands/*.java dht/common/response/*.java storage_server/Datum.java dht/common/Hashing.java dht/rush/utils/*.java dht/server/Command.java -Xlint:unchecked

#2 start all data node proceses residing on the same virtual machine of proxy server (without DHT)
java -classpath .:../lib/\* dht/rush/CentralServer run datanode

#2a start a single data node with specific IP and port
java -classpath .:../lib/\* dht/rush/DataNode <IP> <port> <hashRange>

#3 run proxy server (pushing DHT to all data nodes)
java -classpath .:../lib/\* dht/rush/CentralServer

#4 start a normal client to connect to a data node with specific IP and port
java -classpath .:../lib/\* dht/rush/client <IP> <port>

#4a start a normal client, connecting to a specific IP and port when reading or writing 
java -classpath .:../lib/\* dht/rush/client



## compile and run Elastic DHT server

#1 compile elastic DHT
javac -classpath ../lib/\* dht/elastic_DHT_centralized/*.java dht/server/Command.java dht/common/Hashing.java dht/common/response/Response.java

#2 start all data node proceses residing on the same virtual machine of proxy server (without DHT)
java -classpath .:../lib/\* dht/elastic_DHT_centralized/ProxyServer run datanode

#2a start a single data node with specific IP and port
java -classpath .:../lib/\* dht/elastic_DHT_centralized/DataNode <IP> <port> <hashRange>

#3 run proxy server (pushing DHT to all data nodes)
java -classpath .:../lib/\* dht/elastic_DHT_centralized/ProxyServer

#4 start a normal client to connect to a data node with specific IP and port
java -classpath .:../lib/\* dht/elastic_DHT_centralized/client <IP> <port>

#4a start a normal client, connecting to a specific IP and port when reading or writing 
java -classpath .:../lib/\* dht/elastic_DHT_centralized/client



#5 compile and run control client
javac -classpath ../lib/\* control_client/*.java dht/server/Command.java dht/common/Hashing.java

java -classpath .:../lib/\* control_client/control_client


## Commands help

# for normal client
dht info	# show local dht epoch
dht print	# show local dht table
dht head	# fetch remote dht epoch
dht pull	# download remote dht table
info bucket	# show hash range of a data node

# for control client
# Ring
add <IP> <Port> <hash>
remove <hash>
loadbalance <delta> <hash>
loadcommand <path> | example: loadcommand /dht/Ring/ring_CCcommands.txt
info
help
exit

#Elastic DHT
loadcommand <path> | example: loadcommand /dht/elastic_DHT_centralized/elastic_CCcommands.txt
add <IP> <Port> <start> <end>
remove <IP> <Port>
loadbalance <fromIP> <fromPort> <toIP> <toPort> <numOfBuckets>
help
exit

#Rush
loadcommand <path> | example: loadcommand /dht/rush/cephControlClient.txt
addnode <subClusterId> <IP> <Port> <weight> | example: addnode S0 localhost 689 0.5
deletenode <subClusterId> <IP> <Port> | example: deletenode S0 localhost 689
getnodes <pgid> | example: getnodes PG1
loadbalancing <subClusterId>
getmap
changeweight <subClusterId> <ip> <port> <weight>
help
exit



## How to run the system for each DHT scheme
#1. compile code under each dht folder
example: javac -classpath ../lib/\* dht/Ring/*.java dht/server/Command.java dht/common/Hashing.java dht/common/response/*.java storage_server/Datum.java control_client/control_client.java
#2. run all data nodes using one command (without DHT)
example: java -classpath .:../lib/\* dht/Ring/ProxyServer run datanode
#3. run proxyserver (pushing DHT to all data nodes)
example: java -classpath .:../lib/\* dht/Ring/ProxyServer
#4. run normal client to connect to data node
#5. run control client to send add/remove/load balance request




