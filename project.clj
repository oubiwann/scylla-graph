(defn get-banner
  []
  (str
    (slurp "resources/text/banner.txt")
    #_(slurp "resources/text/loading.txt")))

(defn get-prompt
  [ns]
  (str "\u001B[35m[\u001B[34m"
       ns
       "\u001B[35m]\u001B[33m =>\u001B[m "))

(defproject MediaMath/scylla-graph "0.1.0-SNAPSHOT"
  :description "Example usage of ScyllaDB as a graph database"
  :url "https://github.com/oubiwann/scylla-graph"
  :scm {
    :name "git"
    :url "https://github.com/oubiwann/scylla-graph"}
  :license {
    :name "Apache License Version 2.0"
    :url "https://www.apache.org/licenses/LICENSE-2.0"}
  :dependencies [
    [clojusc/system-manager "0.3.0"]
    [clojusc/twig "0.4.1"]
    [clojusc/unified-config "0.5.0-SNAPSHOT"]
    [com.stuartsierra/component "0.4.0"]
    [org.apache.tinkerpop/gremlin-server "3.4.0"]
    [org.janusgraph/janusgraph-cassandra "0.3.1"]
    [org.clojure/clojure "1.10.0"]]
  :profiles {
    :ubercompile {
      :aot :all}
    :custom-repl {
      :repl-options {
        :init-ns mm.scylla.graph.repl
        :prompt ~get-prompt
        :init ~(println (get-banner))}}
    :dev {
      :source-paths ["dev-resources/src"]
      :main mm.scylla.graph.main
      :dependencies [
        [clojusc/trifl "0.4.2"]
        [org.clojure/tools.namespace "0.2.11"]]
      :plugins [
        [lein-shell "0.5.0"]
        [venantius/ultra "0.5.2"]]}
    :lint {
      :plugins [
        [jonase/eastwood "0.3.5"]
        [lein-kibit "0.1.6"]]}
    :test {
      :dependencies [
        [clojusc/ltest "0.4.0-SNAPSHOT"]]
      :plugins [
        [lein-ancient "0.6.15"]
        [lein-ltest "0.4.0-SNAPSHOT"]]
      :test-selectors {
        :select :select}}
    :docs {
      :dependencies [
        [codox-theme-rdash "0.1.2"]]
      :plugins [
        [lein-codox "0.10.5"]
        [lein-marginalia "0.9.1"]]
      :codox {
        ; :project {
        ;   :name "MediaMath/scylla-graph"
        ;   :description "Example usage of ScyllaDB as a graph database"}
        :namespaces [#"^mm\.scylla].graph\.(?!repl)"]
        :themes [:rdash]
        :output-path "docs/current"
        :doc-paths ["resources/docs"]
        :metadata {
          :doc/format :markdown
          :doc "Documentation forthcoming"}}}}
  :aliases {
    "ubercompile" ["with-profile" "+ubercompile" "compile"]
    "check-vers" ["with-profile" "+test" "ancient" "check" ":all"]
    "check-jars" ["with-profile" "+test" "do"
      ["deps" ":tree"]
      ["deps" ":plugin-tree"]]
    "check-deps"
      ^{:doc "Check if any deps have out-of-date versions"}
      ["do"
        ["check-jars"]
        ["check-vers"]]
    "kibit" ["with-profile" "+lint" "kibit"]
    "eastwood" ["with-profile" "+lint" "eastwood" "{:namespaces [:source-paths]}"]
    "lint"
      ^{:doc "Perform lint checking"}
      ["do"
        ["kibit"]
        ;["eastwood"]
        ]
    "ltest" ["with-profile" "+test" "ltest"]
    "init-content"
      ^{:doc "Add blog content branch as a submodule"}
      ["shell" "git" "submodule" "update" "--init" "--recursive"]
    "repl"
      ^{:doc "A custom REPL that overrides the default one"}
      ["with-profile" "+test,+custom-repl" "repl"]
    "build"
      ^{:doc "Perform build tasks for CI/CD & releases\n\nAdditional aliases:"}
      ["with-profile" "+test" "do"
        ["check-vers"]
        ["ubercompile"]
        ["lint"]
        ["test"]
        ["uberjar"]]})
