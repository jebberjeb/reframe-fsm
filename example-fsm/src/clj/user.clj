(ns user
  (:require
    [cljs.build.api]
    [reframe-fsm.server :as server]))

(def build {:main 'reframe-fsm.core
            :output-to "resources/public/js/core.js"
            ;; Add :asset-path, :output-dir once a
            ;; webserver is involved
            :asset-path "/js/out"
            :output-dir "resources/public/js/out"})

;; This is enough, even though we're using cljc, since the classpath was
;; generated from the :source-paths in project.clj which include src/cljc.
(def src "src/cljs")

(defn build-cljs
  []
  (cljs.build.api/build src build))

(defn watch-cljs
  []
  (cljs.build.api/watch src build))

(defn go
  []
  (server/start))
