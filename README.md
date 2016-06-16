# Node Cluster

Note the TTL for multicast network is set to '1'.

## How it works
1. Node start and send multicast hello sync message
2. The receiving nodes adds the new node to it's view
3. Hello ACK is received by the node and adds the responding node to it's view



## Build App
Use maven to build the application
```
mvn package
```

## Usage
```
usage: Main
 -c,--cluster-addr <arg>      Cluster addres used for cluster commuication
                              - default 192.168.81.1
 -cp,--cluster-port <arg>     Cluster port used for cluster communication
                              - default 5050
 -h,--help <arg>              Help
 -m,--multicast-addr <arg>    Multicast address used for auto discovery -
                              default 225.254.254.5
 -mp,--multicast-port <arg>   Multicast port used for auto discovery -
                              default 5000
 -n,--numNodes <arg>          Number of nodes in the cluster
```

## Example to run the application
Execute the JAR file created from the maven build.
```
java -jar target/node-1.0.jar -n 6 -cp 5052
```

When the application is started you will see the following output message:
```
java -jar target/node-1.0.jar -n 6 -cp 5050
Node Server listening on 192.168.81.1:5050
Waiting for connections ...
[NODE READY]Local Node UUID:18a0a894-fa43-422c-8ab1-1b818bc2b9ef
```
