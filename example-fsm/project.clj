(defproject reframe-fsm "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.9.0-alpha14"]

                 ;; Clojurescript
                 [org.clojure/clojurescript "1.9.293" :scope "provided"]

                 ;; Pedestal
                 [io.pedestal/pedestal.service "0.5.2"]
                 [io.pedestal/pedestal.jetty "0.5.2"]

                 ;; Reframe
                 [reagent  "0.6.0-rc"]
                 [re-frame "0.9.0"]

                 ;; Generate FSM diagram
                 [fsmviz "0.1.0"]]

  :source-paths ["src/clj" "src/cljc"])
