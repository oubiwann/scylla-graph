(ns mm.scylla.graph.config
  (:require
    [clojusc.config.unified.yaml :as config]))

(def config-file "config/scylla-graph/config.yml")

(defn data
  ([]
    (data config-file))
  ([filename]
    (config/data filename)))
