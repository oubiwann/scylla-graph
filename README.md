# scylla-graph

[![Build Status][travis-badge]][travis]
[![Clojars Project][clojars-badge]][clojars]
[![Tag][tag-badge]][tag]
[![JDK version][jdk-v]](.travis.yml)
[![Clojure version][clojure-v]](project.clj)

[![][logo]][logo-large]

*Example usage of ScyllaDB as a graph database*

#### Contents

* [About](#about-)
* [Sample Data](#sample-data-)
   * [Download](#download-)
   * [Ingest](#ingest-)
* [Setup](#setup-)
   * [Using docker](#using-docker-)
   * [Using docker-compose](#using-docker-compose-)
   * [Using Kubernetes](#using-kubernetes-)
   * [Using Terraform](#using-terraform-)
* [Connecting](#connecting-)
* [Using Janusgraph on ScyllaDB](#using-janusgraph-on-scylladb-)
* [License](#license-)

## About [&#x219F;](#contents)

TBD

## Sample Data [&#x219F;](#contents)

### Download [&#x219F;](#contents)

If you'd like to try this out with sample data, you may do the following:

```
$ lein download-sample-data
```


### Ingest [&#x219F;](#contents)

TBD


## Setup [&#x219F;](#contents)

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


### Using `docker` [&#x219F;](#contents)

Create a docker network:
```
$ docker network create --subnet=172.27.27.0/27 scylla-net
```

Run a single node, port-forwarding to the Docker network we created above:
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


### Using `docker-compose` [&#x219F;](#contents)

When you use `docker-compose`, the network is created (and later destroyed)
for you (though you will need to have created the virtual NIC as above):

```
$ docker-compose -f resources/docker/compose.yml up
```

For both the `docker` and `docker-compose`, you can interact with the
running systems with the following:

* system shell: `docker exec -it ce332e5ce806 bash`
* cql shell: `docker exec -it ce332e5ce806 cqlsh -C --keyspace=janusgraph`
* nodetool:
  * status: `docker exec -it ce332e5ce806 nodetool status`
  * tablestats: `docker exec -it ce332e5ce806 nodetool tablestats`


## Using Kubernetes [&#x219F;](#contents)

TBD


## Using Terraform [&#x219F;](#contents)

TBD


## Connecting [&#x219F;](#contents)

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
2019-02-08T16:29:29.633 [nREPL-worker-4] INFO mm.scylla.graph.components.config:82 - Starting config component ...
2019-02-08T16:29:29.634 [nREPL-worker-4] INFO mm.scylla.graph.components.logging:22 - Starting logging component ...
2019-02-08T16:29:29.635 [nREPL-worker-4] INFO mm.scylla.graph.components.janus:44 - Starting JanusGraph component ...
2019-02-08T16:29:29.690 [nREPL-worker-4] INFO mm.scylla.graph.components.gremlin:24 - Starting Gremlin server component ...
2019-02-08T16:29:32.369 [nREPL-worker-4] INFO mm.scylla.graph.components.httpd:17 - Starting httpd component ...
:running
```


## Using Janusgraph on ScyllaDB [&#x219F;](#contents)

The following example uses the Clojure Gremlin library ogre to
demonstrate a running/working Janusgraph ScyllaDB cluster:

From the [TinkerPop docs](http://tinkerpop.apache.org/docs/current/tutorials/getting-started/), create a graph

```clj
  (require '[clojurewerkz.ogre.core :as ogre]
           '[mm.scylla.graph.api.db :as db]
           '[mm.scylla.graph.components.janus :as janus])
  (def graph (janus/db (system)))
  (def g (ogre/traversal graph))
```

Then create some vertices:

```clj
  (def v1 (-> (ogre/addV g "person")
              (ogre/property "name" "marko")
              (ogre/property "age" 29)
              (ogre/next!)))

  (db/commit graph)

  (def v2 (-> (ogre/addV g "software")
              (ogre/property "name" "lop")
              (ogre/property "lang" "clj")
              (ogre/next!)))

  (db/commit graph)
```

Now an edge, connecting the two:

```clj
  (def e (-> (ogre/addE g "created")
             (ogre/from v1)
             (ogre/to v2)
             (ogre/property "weight" 0.4)
             (ogre/next!)))

  (db/commit graph)
```

Now log into a node your Scylla cluster and execute some CQL to verify
data has been saved to the Janusgraph backend:

```sql
cqlsh:janusgraph> SELECT * FROM graphindex;
cqlsh:janusgraph> SELECT * FROM edgestore;
```

If you run those queries before any transactions, you will see no
results; afterwards, there will be several rows of obscure data.

Back in the REPL, let's query the vertices we created (from the
[ogre docs](http://ogre.clojurewerkz.org/articles/getting_started.html#the_traversal)):

```clj
  (ogre/traverse
    g
    ogre/V
    (ogre/into-seq!))
```
```
(...[... "v[4112]"])
```
Edges can't be accessed outside their original transaction, so after
the edge is committed, it needs to be refreshed
(see [https://docs.janusgraph.org/latest/tx.html](https://docs.janusgraph.org/latest/tx.html)) -- let's redefine it:

```clj
  (def e (ogre/traverse
          g
          (ogre/E e)))
  e
```
```
#object[... "[GraphStep(edge,[[GraphStep(edge,[e[...][4184-created->4112]])]])]"]
```

Let's examine the vertex name values (from the
[ogre docs](http://ogre.clojurewerkz.org/articles/getting_started.html#the_traversal)):

```clj
  (ogre/traverse
    g
    ogre/V
    (ogre/values :name)
    (ogre/into-seq!))
```
```clj
("marko" "lop")
```

We can be a bit more specific, too (from the
[TinkerPop docs](http://tinkerpop.apache.org/docs/current/tutorials/getting-started/)):

```clj
  (ogre/traverse
    g
    ogre/V
    (ogre/has :name "marko")
    (ogre/into-seq!))
```
```
(...[... "v[4184]"])
```
```clj
  (ogre/traverse
    g
    ogre/V
    (ogre/has :name "marko")
    (ogre/out :created)
    (ogre/values :name)
    (ogre/into-seq!))
```
```clj
("lop")
```


## License [&#x219F;](#contents)

Copyright © 2019, MediaMath, Inc.

Copyright © 2019, Duncan McGreggor

Apache License, Version 2.0.


<!-- Named page links below: /-->

[travis]: https://travis-ci.org/oubiwann/scylla-graph
[travis-badge]: https://travis-ci.org/oubiwann/scylla-graph.png?branch=master
[deps]: http://jarkeeper.com/oubiwann/scylla-graph
[deps-badge]: http://jarkeeper.com/oubiwann/scylla-graph/status.svg
[logo]: resources/images/ScyllaDB-Janusgraph.png
[logo-large]: resources/images/ScyllaDB-Janusgraph.png
[logo1]: resources/images/ScyllaDB.png
[logo-large1]: resources/images/ScyllaDB.png
[logo2]: resources/images/Janusgraph.png
[logo-large2]: resources/images/Janusgraph.png
[tag-badge]: https://img.shields.io/github/tag/oubiwann/scylla-graph.svg
[tag]: https://github.com/oubiwann/scylla-graph/tags
[clojure-v]: https://img.shields.io/badge/clojure-1.10.0-blue.svg
[jdk-v]: https://img.shields.io/badge/jdk-1.8+-blue.svg
[clojars]: https://clojars.org/mediamath/scylla-graph
[clojars-badge]: https://img.shields.io/clojars/v/mediamath/scylla-graph.svg
