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

(defproject mediamath/scylla-graph "0.1.0-SNAPSHOT"
  :description "Example usage of ScyllaDB as a graph database"
  :url "https://github.com/oubiwann/scylla-graph"
  :scm {
    :name "git"
    :url "https://github.com/oubiwann/scylla-graph"}
  :license {
    :name "Apache License Version 2.0"
    :url "https://www.apache.org/licenses/LICENSE-2.0"}
  :exclusions [
    [com.google.guava/guava]
    [com.googlecode.json-simple/json-simple]
    [commons-codec]
    [commons-lang]
    [org.apache.tinkerpop/gremlin-core]
    [org.apache.tinkerpop/gremlin-shaded]
    [org.clojure/clojure]
    [org.clojure/core.rrb-vector]
    [org.clojure/tools.reader]
    [org.flatland/ordered]
    [org.javassist/javassist]
    [org.slf4j/slf4j-api]
    [org.xerial.snappy/snappy-java]
    [org.yaml/snakeyaml]
    [potemkin]]
  :dependencies [
    ;; JDK 11 Fixes
    [org.clojure/core.rrb-vector "0.0.14"]
    [org.flatland/ordered "1.5.7"]
    ;; Jarfile Conflict Fixes
    [com.google.guava/guava "25.1-jre"]
    [com.googlecode.json-simple/json-simple "1.1.1"]
    [commons-codec/commons-codec "1.11"]
    [commons-lang/commons-lang "2.6"]
    [org.apache.tinkerpop/gremlin-core  "3.4.0"]
    [org.apache.tinkerpop/gremlin-shaded "3.4.0"]
    [org.clojure/tools.reader "1.3.2"]
    [org.slf4j/slf4j-api "1.7.25"]
    [org.xerial.snappy/snappy-java "1.1.7.2"]
    [org.yaml/snakeyaml "1.23"]
    [potemkin "0.4.5"]
    ;; Actual Dependencies
    [cheshire "5.8.1"]
    [clojurewerkz/ogre "3.3.4.0"]
    [clojusc/system-manager "0.3.0"]
    [clojusc/twig "0.4.1"]
    [clojusc/unified-config "0.5.0-SNAPSHOT"]
    [com.stuartsierra/component "0.4.0"]
    [http-kit "2.3.0"]
    [metosin/reitit-core "0.2.13"]
    [metosin/reitit-ring "0.2.13"]
    [metosin/ring-http-response "0.9.1"]
    [org.apache.tinkerpop/gremlin-server "3.4.0"]
    [org.clojure/clojure "1.10.0"]
    [org.janusgraph/janusgraph-cassandra "0.3.1"]
    [ring/ring-core "1.7.1"]
    [ring/ring-codec "1.1.1"]
    [ring/ring-defaults "0.3.2"]]
  :profiles {
    :ubercompile {
      :aot :all}
    :custom-repl {
      :repl-options {
        :init-ns mm.scylla.graph.repl
        :prompt ~get-prompt
        :init ~(println (get-banner))
        ; :host "0.0.0.0"
        ; :port 8821
        }}
    :dev {
      :source-paths ["dev-resources/src"]
      ; :main mm.scylla.graph.main
      :dependencies [
        [clojusc/trifl "0.4.2"]
        ; [nrepl "0.6.0"]
        [org.clojure/tools.namespace "0.2.11"]
        [org.clojure/tools.nrepl "0.2.13"]]
      :plugins [
        [lein-shell "0.5.0"]
        ; [nrepl/lein-nrepl "0.3.2"]
        [oubiwann/venantius-ultra "0.5.4-SNAPSHOT" :exclusions [org.clojure/clojure]]]}
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
      ["with-profile" "+dev,+test,+custom-repl" "repl"]
    "build"
      ^{:doc "Perform build tasks for CI/CD & releases\n\nAdditional aliases:"}
      ["with-profile" "+test" "do"
        ["check-vers"]
        ["ubercompile"]
        ["lint"]
        ["test"]
        ["uberjar"]]
    "docker-build"
      ["shell" "docker" "build"
       "-t" "oubiwann/scylla-graph"
       "./resources/docker/"]})
