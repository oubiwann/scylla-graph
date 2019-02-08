(ns mm.scylla.graph.app.core
  (:require
    [mm.scylla.graph.app.handler :as handler]
    [mm.scylla.graph.app.middleware :as middleware]
    [mm.scylla.graph.app.routes :as routes]
    [mm.scylla.graph.components.config :as config]
    [ring.middleware.defaults :as ring-defaults]))

(defn main
  [httpd-component]
  (-> httpd-component
      routes/all
      middleware/wrap-log-request
      (ring-defaults/wrap-defaults ring-defaults/api-defaults)
      middleware/wrap-trailing-slash
      (middleware/wrap-not-found httpd-component)
      middleware/wrap-log-response))
