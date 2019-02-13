(ns mm.scylla.graph.components.core
  (:require
    [com.stuartsierra.component :as component]
    [mm.scylla.graph.components.config :as config]
    [mm.scylla.graph.components.gremlin :as gremlin]
    [mm.scylla.graph.components.httpd :as httpd]
    [mm.scylla.graph.components.janus :as janus]
    [mm.scylla.graph.components.logging :as logging]
    [mm.scylla.graph.config :as config-lib]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Common Configuration Components   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn cfg
  [data]
  {:config (config/create-component data)})

(def log
  {:logging (component/using
             (logging/create-component)
             [:config])})

(def janus
  {:janus (component/using
           (janus/create-component)
           [:config :logging :gremlin])})

(def janus-no-logging
  {:janus (component/using
           (janus/create-component)
           [:config :gremlin])})

(def grmln
  {:gremlin (component/using
             (gremlin/create-component)
             [:config :logging])})

(def grmln-no-logging
  {:gremlin (component/using
             (gremlin/create-component)
             [:config])})

(def http-server
  {:httpd (component/using
           (httpd/create-component)
           [:config :logging :janus :gremlin])})

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Component Initializations   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn initialize-bare-bones
  []
  (let [cfg-data (config-lib/data)]
    (component/map->SystemMap
      (merge (cfg cfg-data)
             log))))

(defn initialize-with-backend
  []
  (let [cfg-data (config-lib/data)]
    (component/map->SystemMap
      (merge (cfg cfg-data)
             log
             grmln
             janus))))

(defn initialize-with-backend-no-logging
  []
  (let [cfg-data (config-lib/data)]
    (component/map->SystemMap
      (merge (cfg cfg-data)
             grmln-no-logging
             janus-no-logging))))

(defn initialize-with-web
  []
  (let [cfg-data (config-lib/data)]
    (component/map->SystemMap
      (merge (cfg cfg-data)
             log
             grmln
             janus
             http-server))))

(def init-lookup
  {:basic #'initialize-bare-bones
   :backend #'initialize-with-backend
   :web #'initialize-with-web
   :cli #'initialize-with-backend-no-logging})

(defn init
  ([]
    (init :web))
  ([mode]
    ((mode init-lookup))))

(def cli #(init :cli))
