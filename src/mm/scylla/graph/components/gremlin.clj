(ns mm.scylla.graph.components.gremlin
  (:require
    [mm.scylla.graph.components.config :as config]
    [com.stuartsierra.component :as component]
    [taoensso.timbre :as log])
  (:import
    (org.apache.tinkerpop.gremlin.server GremlinServer
                                         Settings)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Gremlin Component API   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; TBD

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Component Lifecycle Implementation   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defrecord Gremlin [server])

(defn start
  [this]
  (log/info "Starting Gremlin server component ...")
  (let [cfg (config/gremlin-config this)
        settings (Settings/read cfg)
        server (new GremlinServer settings)]
    (try
      (.start server)
      (catch Exception ex
        (log/error "Couldn't start gremlin:")
        (log/error ex)))
    (log/debug "Started Gremlin server component.")
    (assoc this :server server)))

(defn stop
  [this]
  (log/info "Stopping Gremlin server component ...")
  (try
    (.stop (:server this))
    (catch Exception ex
      (log/error "Couldn't stop gremlin server:")
      (log/error ex)))
  (log/debug "Stopped Gremlin server component.")
  (assoc this :server nil))

(def lifecycle-behaviour
  {:start start
   :stop stop})

(extend Gremlin
  component/Lifecycle
  lifecycle-behaviour)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Component Constructor   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn create-component
  ""
  []
  (map->Gremlin {}))
