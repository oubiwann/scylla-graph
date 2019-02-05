(ns mm.scylla.graph.components.janus
  (:require
    [mm.scylla.graph.api.db :as db]
    [mm.scylla.graph.api.factory :as factory]
    [mm.scylla.graph.components.config :as config]
    [com.stuartsierra.component :as component]
    [taoensso.timbre :as log])
  (:import
    (clojure.lang Keyword)
    (clojure.lang Symbol)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   JanusGraph Component API   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn get-conn
  [system]
  (get-in system [:janus :conn]))

(defn get-factory
  [system]
  (get-in system [:janus :factory]))

(defn db-call
  [system ^Symbol func args]
  (apply
    (resolve 'db/func)
    (concat [(get-conn system)] args)))

(defn factory-call
  [system ^Symbol func args]
  (apply
    (resolve 'factory/func)
    (concat [(get-factory system)] args)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Component Lifecycle Implementation   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defrecord JanusGraph [conn factory])

(defn start
  [this]
  (log/info "Starting JanusGraph component ...")
  (let [f (factory/create)
        conn (factory/connect f (config/storage-spec this))]
    (log/debug "Started JanusGraph component.")
    (assoc this :conn conn :factory f)))

(defn stop
  [this]
  (log/info "Stopping JanusGraph component ...")
  (db/disconnect (:conn this))
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
