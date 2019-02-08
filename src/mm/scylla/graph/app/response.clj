(ns mm.scylla.graph.app.response
  "This namespace defines a default set of transform functions suitable for use
  in presenting results to HTTP clients.

  Note that ring-based middleeware may take advantage of these functions either
  by single use or composition."
  (:require
    [cheshire.core :as json]
    [clojure.string :as string]
    [ring.util.http-response :as response]
    [taoensso.timbre :as log]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Utility functions   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn stream->str
  [body]
  (if (string? body)
    body
    (slurp body)))

(defn parse-json-body
  [body]
  (let [str-data (stream->str body)
        json-data (json/parse-string str-data true)]
    (log/trace "str-data:" str-data)
    (log/trace "json-data:" json-data)
    json-data))

(defn json-errors
  [body]
  (:errors (parse-json-body body)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Global operations   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn ok
  [_request & args]
  (response/ok args))

(defn not-found
  [_request]
  (response/content-type
   (response/not-found "Not Found")
   "text/html"))

(defn json
  [data]
  (log/trace "Got data for JSON:" data)
  (-> data
      (assoc :body (json/generate-string data))
      (response/content-type "application/json")))

(defn text
  [data]
  (-> data
      (assoc :body data)
      (response/content-type "text/plain")))

(defn html
  [data]
  (-> data
      (assoc :body data)
      (response/content-type "text/html")))
