(ns mm.scylla.graph.repl
  "Project development namespace."
  (:require
    [clojure.java.io :as io]
    [clojure.pprint :refer [pprint]]
    [clojure.string :as string]
    [clojure.tools.namespace.repl :as repl]
    [clojusc.system-manager.core :refer :all]
    [clojusc.twig :as logger]
    [clojurewerkz.ogre.core :as ogre]
    [com.stuartsierra.component :as component]
    [mm.scylla.graph.components.config :as config]
    [mm.scylla.graph.components.core]
    [mm.scylla.graph.components.janus :as janus]
    [mm.scylla.graph.config :as config-lib]
    [trifl.java :refer [show-methods]])
  (:import
    (java.net URI)
    (java.nio.file Paths)
    (java.security SecureRandom)
    (org.apache.tinkerpop.gremlin.structure T)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Initial Setup & Utility Functions   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def setup-options {
  :init 'mm.scylla.graph.components.core/init
  :after-refresh 'mm.scylla.graph.repl/init-and-startup
  :throw-errors false})

(defn init
  "This is used to set the options and any other global data.

  This is defined in a function for re-use. For instance, when a REPL is
  reloaded, the options will be lost and need to be re-applied."
  []
  (logger/set-level! '[mm.scylla.graph] :debug)
  (setup-manager setup-options))

(defn init-and-startup
  "This is used as the 'after-refresh' function by the REPL tools library.
  Not only do the options (and other global operations) need to be re-applied,
  the system also needs to be started up, once these options have be set up."
  []
  (init)
  (startup))

;; It is not always desired that a system be started up upon REPL loading.
;; Thus, we set the options and perform any global operations with init,
;; and let the user determine when then want to bring up (a potentially
;; computationally intensive) system.
(init)

(defn banner
  []
  (println (slurp (io/resource "text/banner.txt")))
  :ok)

(comment

  ;; From http://tinkerpop.apache.org/docs/current/tutorials/getting-started/

  ;; Creating a graph
  (def graph (janus/db (system)))
  (def g (ogre/traversal graph))

  ;; Created vertices

  (def v1 (-> (ogre/addV g "person")
              ; (ogre/property T/id 1)
              (ogre/property "name" "marko")
              (ogre/property "age" 29)
              (ogre/next!)))

  (-> graph (.tx) (.commit))

  (def v2 (-> (ogre/addV g "software")
              ; (ogre/property T/id 3)
              (ogre/property "name" "lop")
              (ogre/property "lang" "clj")
              (ogre/next!)))

  (-> graph (.tx) (.commit))

  ;; Creating an edge

  (def e (-> (ogre/addE g "created")
             (ogre/from v1)
             (ogre/to v2)
             ; (ogre/property T/id 9)
             (ogre/property "weight" 0.4)
             (ogre/next!)))

  (-> graph (.tx) (.commit))

  ;; Verify that changes have been made in the cluster:
  $ docker exec -it ce332e5ce806 cqlsh -C --keyspace=janusgraph

  cqlsh:janusgraph> SELECT * FROM graphindex;
  cqlsh:janusgraph> SELECT * FROM edgestore;

  ;; From http://ogre.clojurewerkz.org/articles/getting_started.html#the_traversal

  ;; Querying the graph
  (ogre/traverse
    g
    ogre/V
    (ogre/into-seq!))

  ;; From https://docs.janusgraph.org/latest/tx.html

  ;; Edges can't be accessed outside their original transaction, so
  ;; after the edge is committed, it needs to be refreshed:

  (def e (ogre/traverse
          g
          (ogre/E e)))


  ;; From http://ogre.clojurewerkz.org/articles/getting_started.html#the_traversal

  (ogre/traverse
    g
    ogre/V
    (ogre/values :name)
    (ogre/into-seq!))

  ;; From http://tinkerpop.apache.org/docs/current/tutorials/getting-started/

  (ogre/traverse
    g
    ogre/V
    (ogre/has :name "marko")
    (ogre/into-seq!))

  (ogre/traverse
    g
    ogre/V
    (ogre/has :name "marko")
    (ogre/out :created)
    (ogre/values :name)
    (ogre/into-seq!))

  ;; end comments
  )
