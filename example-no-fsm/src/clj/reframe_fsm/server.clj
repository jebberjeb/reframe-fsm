(ns reframe-fsm.server
  (:require
    [clojure.string :as string]
    [io.pedestal.http :as http]))

(defn respond-foo
  [request]
  (let [{:keys [first-name last-name password confirm-password]}
        (:query-params request)]
    (cond (not= password confirm-password)
          {:status 400 :body "passwords must match"}
          (> 6 (count password))
          {:status 400 :body "password too short"}
          (string/blank? first-name)
          {:status 400 :body "missing first name"}
          (string/blank? last-name)
          {:status 400 :body "missing last name"}
          :else
          {:status 200 :body "success"})))

(def routes #{["/foo" :get 'reframe-fsm.server/respond-foo]})

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
