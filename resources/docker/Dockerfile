FROM clojure:openjdk-11-lein
LABEL maintainer "Duncan McGreggor <oubiwann@gmail.com>"

RUN cd / && \
    git clone https://github.com/oubiwann/scylla-graph.git && \
    cd scylla-graph && \
    lein check-deps && \
    lein ltest
WORKDIR /scylla-graph

# CMD ["lein", "trampoline", "run"]
# CMD ["lein", "trampoline", "repl", \
#      ":headless", ":host", "0.0.0.0", ":port", "8821"]

