(ns mm.scylla.graph.components.config
  (:require
    [clojure.java.io :as io]
    [com.stuartsierra.component :as component]
    [mm.scylla.graph.config :as config-lib]
    [taoensso.timbre :as log]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Utility Functions   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn get-cfg
  [system]
  (get-in system [:config :data]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Config Component API   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn log-level
  [system]
  (read-string (get-in (get-cfg system) [:logging :level])))

(defn log-nss
  [system]
  (mapv symbol
        (get-in (get-cfg system) [:logging :nss])))

(defn graph-graphname
  [system]
  (get-in (get-cfg system) [:janus :graph :graphname]))

(defn gremlin-graph
  [system]
  (get-in (get-cfg system) [:janus :gremlin :graph]))

(defn storage-backend
  [system]
  (get-in (get-cfg system) [:janus :storage :backend]))

(defn storage-hostname
  [system]
  (get-in (get-cfg system) [:janus :storage :hostname]))

(defn storage-port
  [system]
  (get-in (get-cfg system) [:janus :storage :port]))

(defn storage-directory
  [system]
  (get-in (get-cfg system) [:janus :storage :directory]))

(defn storage-spec
  [system]
  {:graph-graphname (graph-graphname system)
   :gremlin-graph (gremlin-graph system)
   :storage-backend (storage-backend system)
   :storage-hostname (storage-hostname system)
   :storage-port (storage-port system)
   :storage-directory (storage-directory system)})

(defn httpd-host
  [system]
  (get-in (get-cfg system) [:httpd :host]))

(defn httpd-port
  [system]
  (get-in (get-cfg system) [:httpd :port]))

(defn httpd-index-dirs
  [system]
  (get-in (get-cfg system) [:httpd :index-dirs]))

(defn gremlin-config-file
  [system]
  (get-in (get-cfg system) [:gremlin :config-file]))

(defn gremlin-config
  [system]
  (io/input-stream
    (io/resource
      (gremlin-config-file system))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Component Lifecycle Implementation   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defrecord Config [data])

(defn start
  [this]
  (log/info "Starting config component ...")
    (log/trace "Using configuration:" (:data this))
    (log/debug "Started config component.")
    this)

(defn stop
  [this]
  (log/info "Stopping config component ...")
  (log/debug "Stopped config component.")
  (assoc this :data nil))

(def lifecycle-behaviour
  {:start start
   :stop stop})

(extend Config
  component/Lifecycle
  lifecycle-behaviour)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Component Constructor   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn create-component
  ""
  [data]
  (map->Config {:data data}))
