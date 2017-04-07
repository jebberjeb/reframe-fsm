(ns reframe-fsm.fsm)

(def core-fsm {nil {:init :ready}
               :ready {:submit :submitting}
               :submitting {:submit-short-password :short-password
                            :submit-match-password :match-password
                            :submit-missing-first-name :missing-first-name
                            :submit-missing-last-name :missing-last-name
                            :submit-error :error
                            :submit-success :success}
               :short-password {:change-password :ready
                                :change-confirm-password :ready}
               :match-password {:change-password :ready
                                :change-confirm-password :ready}
               :missing-first-name {:change-first-name :ready}
               :missing-last-name {:change-last-name :ready}
               :error {:change-password :ready
                       :change-confirm-password :ready
                       :change-first-name :ready
                       :change-last-name :ready}
               :success nil})

(defn next-state
  [db event]
  (if-let [new-state (get-in core-fsm [(:state db) event])]
    (assoc db :state new-state)
    db))

(comment
  (require '[fsmviz.core])
  (fsmviz.core/generate-image
    reframe-fsm.fsm/core-fsm
    "fsm"))
