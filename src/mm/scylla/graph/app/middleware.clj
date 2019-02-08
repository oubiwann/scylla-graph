(ns mm.scylla.graph.app.middleware
  "Custom ring middleware for hexagram30 web apps and services."
  (:require
    [clojure.string :as string]
    [mm.scylla.graph.app.response :as response]
    [mm.scylla.graph.components.config :as config]
    [reitit.ring :as ring]
    [ring.middleware.content-type :as ring-ct]
    [ring.middleware.defaults :as ring-defaults]
    [ring.middleware.file :as ring-file]
    [ring.middleware.not-modified :as ring-nm]
    [ring.util.response :as ring-response]
    [taoensso.timbre :as log]))

(defn wrap-log-request
  [handler]
  (fn [req]
    (log/debug "Got request:" req)
    (handler req)))

(defn wrap-log-response
  [handler]
  (fn [req]
    (let [resp (handler req)]
      (log/debug "Sending response:" resp)
      resp)))

(defn wrap-trailing-slash
  "Ring-based middleware forremoving a single trailing slash from the end of the
  URI, if present."
  [handler]
  (fn [req]
    (let [uri (:uri req)]
      (handler (assoc req :uri (if (and (not= "/" uri)
                                        (.endsWith uri "/"))
                                 (subs uri 0 (dec (count uri)))
                                 uri))))))

(defn wrap-fallback-content-type
  [handler default-content-type]
  (fn [req]
    (condp = (:content-type req)
      nil (assoc-in (handler req)
                    [:headers "Content-Type"]
                    default-content-type)
      "application/octet-stream" (assoc-in (handler req)
                                           [:headers "Content-Type"]
                                           default-content-type)
      :else (handler req))))

(defn wrap-not-found
  [handler system]
  (fn [req]
    (let [response (handler req)
          status (:status response)]
      (cond (string/includes? (:uri req) "stream")
            (do
              (log/debug "Got streaming response; skipping 404 checks ...")
              response)

            ; (or (= 404 status) (nil? status))
            ; (do
            ;   (when (nil? status)
            ;     (log/debug "Got nil status in not-found middleware ..."))
            ;   (assoc (pages/not-found req {})
            ;          :status 404))

            :else
            response))))
