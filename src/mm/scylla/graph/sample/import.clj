(ns mm.scylla.graph.sample.import
  (:require
    [clojure.java.io :as io]
    [clojure.string :as string]
    [clojurewerkz.ogre.core :as ogre]
    [clojusc.system-manager.core :as system-manager]
    [clojusc.twig :as logger]
    [mm.scylla.graph.api.db :as db]
    [mm.scylla.graph.components.core]
    [mm.scylla.graph.components.janus :as janus]
    [taoensso.timbre :as log])
  (:gen-class))

(def import-dir "sample-data/ml-1m/")
(def import-movies (str import-dir "movies.dat"))
(def import-ratings (str import-dir "ratings.dat"))
(def import-users (str import-dir "users.dat"))
(def import-separator #"::")
(def graph-data (str import-dir "movielens.gryo"))
(def occupations {
  0 "other"
  1 "academic/educator"
  2 "artist"
  3 "clerical/admin"
  4 "college/grad student"
  5 "customer service"
  6 "doctor/health care"
  7 "executive/managerial"
  8 "farmer"
  9 "homemaker"
  10 "K-12 student"
  11 "lawyer"
  12 "programmer"
  13 "retired"
  14 "sales/marketing"
  15 "scientist"
  16 "self-employed"
  17 "technician/engineer"
  18 "tradesman/craftsman"
  19 "unemployed"
  20 "writer"})

(defn parse-line
  [line]
  (string/split line import-separator))

(defn ingest-user-line
  [g line]
  (let [[raw-id gender raw-age occupation-id _] (parse-line line)
        id (int raw-id)
        age (int raw-age)
        occupation (get occupations occupation-id)
        props (->> [id gender age occupation]
                   (interleave ["id" "gender" "age"])
                   (partition 2))
        v (ogre/addV g "user")]
    (mapv #(apply ogre/property (concat [v] %)) props)
    (ogre/next! v))
  :ok)

(defn read-dat
  [filepath line-parser]
  (log/infof "Reading %s ..." filepath)
  (with-open [rdr (io/reader filepath)]
    (doseq [line (line-seq rdr)]
      (line-parser line)))
  :ok)

(defn -main
  [& args]
  (logger/set-level! '[mm.scylla.graph] :debug)
  (let [init (system-manager/setup-manager
             {:init 'mm.scylla.graph.components.core/cli})
        sys (system-manager/startup)
        g (ogre/traversal (janus/db sys))]
    ;; XXX users
    (read-dat import-users (partial ingest-user-line g))
    (db/commit (janus/db sys))
    ;; XXX movies
    ;; XXX ratings
    (system-manager/shutdown)))
