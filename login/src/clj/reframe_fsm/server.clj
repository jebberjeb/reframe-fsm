(ns reframe-fsm.server
  (:require
    [clojure.string :as string]
    [io.pedestal.http :as http]))

(defn login
  [request]
  (let [{:keys [email password]}
        (:query-params request)]
    ;; {:status 200 :body "success"}
    ;; {:status 400 :body "invalid password"}
    {:status 400 :body "user not found"}))

(def routes #{["/login" :get 'reframe-fsm.server/login]})

(defn start
  "The entry-point for 'lein run-dev'"
  [& args]
  (println "\nCreating your [DEV] server...")
  (-> {::http/routes routes
       ::http/resource-path "/public"
       ::http/type :jetty
       ::http/join? false ;; Don't block the REPL.
       ::http/port 8080}
      http/create-server
      http/start))
