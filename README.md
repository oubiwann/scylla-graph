# scylla-graph

*Example usage of ScyllaDB as a graph database*


## About

TBD


## Setup

All of the below instructions assume you have created a virtual NIC on the host
machine with the IP address of `172.27.27.2`. On a Mac, for instance, this
would be done with:

```
$ sudo ifconfig en0 inet 172.27.27.2 netmask 255.255.255.224 alias
```

I chose .224 (and CIDR /27) due to the fact that I'm not going to nee more than
32 hosts to experiment with various setups.

Below are outlined several ways in which you can start up and test ScyallDB's
graph database capabilities (using Janusgraph). These include:

* Plain old `docker`
* `docker-compose`
* ...

**Important**: choose only one!


### Using `docker`

Create a docker network:
```
$ docker network create --subnet=172.27.27.0/27 scylla-net
```

Port-forward to the network:
```
docker run -it \
  --net scylla-net \
  --ip 172.27.27.2 \
  -p 172.27.27.2:9042:9042 \
  -p 172.27.27.2:7001:7001 \
  -p 172.27.27.2:10000:10000 \
  -p 172.27.27.2:9160:9160 \
  -p 172.27.27.2:7000:7000 \
  scylladb/scylla
```


### Using `docker-compose`

When you use `docker-compose`, the network is created (and later destroyed)
for you (though you will need to have created the virtual NIC as above).
Additionally, the app's remote REPL will be started for you as well:

```
$ docker-compose -f resources/docker/compose.yml up
```


## Connecting

At this point, you can start up a Clojure REPL and bring up the system:

```
$ lein repl
```
```
  \  |          |_)        \  |       |   |     )
 |\/ |  _ \  _` | |  _` | |\/ |  _` | __| __ \ /  __|
 |   |  __/ (   | | (   | |   | (   | |   | | | \__ \
_|  _|\___|\__,_|_|\__,_|_|  _|\__,_|\__|_| |_| ____/


               :| :|                                 :|
<::< .::/ :\:| :| :| .::\ :::::| /::| :::| .::\ :::\ :::|
>::> `::\ `::| :| :| `::|        \::| :|   `::| :::/ :|:|
          .,:'                   ,.:/           :|

```
```
[mm.scylla.graph.repl] => (startup)
```
```
2019-02-05T15:17:19.306 [nREPL-worker-2] INFO mm.scylla.graph.components.config:59 - Starting config component ...
2019-02-05T15:17:19.306 [nREPL-worker-2] DEBUG mm.scylla.graph.components.config:61 - Started config component.
2019-02-05T15:17:19.306 [nREPL-worker-2] INFO mm.scylla.graph.components.logging:22 - Starting logging component ...
2019-02-05T15:17:19.307 [nREPL-worker-2] DEBUG mm.scylla.graph.components.logging:25 - Setting up logging with level :debug
2019-02-05T15:17:19.307 [nREPL-worker-2] DEBUG mm.scylla.graph.components.logging:26 - Logging namespaces: [mm.scylla.graph]
2019-02-05T15:17:19.307 [nREPL-worker-2] DEBUG mm.scylla.graph.components.logging:28 - Started logging component.
2019-02-05T15:17:19.308 [nREPL-worker-2] INFO mm.scylla.graph.components.janus:44 - Starting JanusGraph component ...
2019-02-05T15:17:19.308 [nREPL-worker-2] DEBUG mm.scylla.graph.api.factory:66 - Configuring builder with: {:storage-backend "cassandra", :storage-hostname "172.27.27.2", :storage-port 9160, :storage-directory "data/graphdb/janus"}
2019-02-05T15:17:30.622 [nREPL-worker-2] DEBUG mm.scylla.graph.components.janus:47 - Started JanusGraph component.
:running
```
