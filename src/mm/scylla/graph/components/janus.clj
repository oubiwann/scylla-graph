(ns mm.scylla.graph.components.janus
  (:require
    [mm.scylla.graph.api.db :as db-lib]
    [mm.scylla.graph.api.factory :as factory-lib]
    [mm.scylla.graph.components.config :as config]
    [com.stuartsierra.component :as component]
    [taoensso.timbre :as log])
  (:import
    (clojure.lang Keyword)
    (clojure.lang Symbol)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   JanusGraph Component API   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn db
  [system]
  (get-in system [:janus :conn]))

(defn factory
  [system]
  (get-in system [:janus :factory]))

(defn reslv
  [^Symbol lib ^Keyword func]
  (ns-resolve lib (symbol (name func))))

(defn db-call
  ([system ^Keyword func]
    (db-call system func []))
  ([system ^Keyword func args]
    (apply
      (reslv 'mm.scylla.graph.api.db func)
      (concat [(db system)] args))))

(defn factory-call
  ([system ^Keyword func]
    (factory-call system func []))
  ([system ^Keyword func args]
    (apply
      (reslv 'mm.scylla.graph.api.factory func)
      (concat [(factory system)] args))))

(defn call
  [system call-type & args]
  (case call-type
    :db (apply db-call (concat [system] args))
    :factory (apply factory-call (concat [system] args))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Component Lifecycle Implementation   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defrecord JanusGraph [conn factory])

(defn start
  [this]
  (log/info "Starting JanusGraph component ...")
  (let [f (factory-lib/create)
        conn (factory-lib/connect f (config/storage-spec this))]
    (log/debug "Started JanusGraph component.")
    (assoc this :conn conn :factory f)))

(defn stop
  [this]
  (log/info "Stopping JanusGraph component ...")
  (db-lib/disconnect (:conn this))
  (log/debug "Stopped JanusGraph component.")
  (assoc this :conn nil :factory nil))

(def lifecycle-behaviour
  {:start start
   :stop stop})

(extend JanusGraph
  component/Lifecycle
  lifecycle-behaviour)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Component Constructor   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn create-component
  ""
  []
  (map->JanusGraph {}))
