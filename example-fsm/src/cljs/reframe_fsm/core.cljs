(ns reframe-fsm.core
  (:require
    [clojure.string :as string]
    [reagent.core :as reagent]
    [re-frame.core :as rf])
  (:import
    (goog.net XhrIo)))

;; -- FSM ---------------------------------------------------------------------

(def fsm {:start {:submit :submitting}
          :submitting {:submit-short-password :short-password
                       :submit-match-password :match-password
                       :submit-missing-first-name :missing-first-name
                       :submit-missing-last-name :missing-last-name
                       :submit-error :error
                       :submit-success :success}
          :short-password {:change-password :start
                           :change-confirm-password :start}
          :match-password {:change-password :start
                           :change-confirm-password :start}
          :missing-first-name {:change-first-name :start}
          :missing-last-name {:change-last-name :start}
          :error {:change-password :start
                  :change-confirm-password :start
                  :change-first-name :start
                  :change-last-name :start}
          :success nil})

(defn next-state
  [db event]
  (if-let [new-state (get-in fsm [(:state db) event])]
    (assoc db :state new-state)
    db))

;; -- Subscriptions -----------------------------------------------------------

(rf/reg-sub :input-value
            (fn [db [_ value-kw]]
              (get db value-kw "")))

(rf/reg-sub :failure
            (fn [db _]
              (case (:state db)
                :error "Something went wrong!"
                :short-password "Password too short"
                :match-password "Passwords don't match"
                :missing-first-name "Missing first name"
                :missing-last-name "Missing last name"
                nil)))

(rf/reg-sub :success
            (fn [db _]
              (= :success (:state db))))

(rf/reg-sub :disable-submit
            (fn [db _]
              (= :start (:state db))))

;; -- Events ------------------------------------------------------------------

(defn handle-init
  [{:keys [db]} _]
  (assoc db :state :start))

(defn handle-next-state
  [db [event _]]
  (next-state db event))

(defn handle-submit
  [{:keys [db]} [event _]]
  (let [{:keys [first-name last-name password confirm-password]} db]
    (XhrIo/send (str "/foo?first-name=" first-name
                     "&last-name=" last-name
                     "&password=" password
                     "&confirm-password=" confirm-password)
                #(let [response (-> % .-target .getResponse)
                       status (-> % .-target .getStatus)]
                   (rf/dispatch [(case status
                                   200
                                   :submit-success
                                   400
                                   (keyword (str "submit-" response))
                                   :submit-error) response]))))
  {:db (next-state db event)})

(defn handle-change-field
  [field-kw db [event value]]
  (-> db
      (next-state event)
      (assoc field-kw value)))

(rf/reg-event-db :init handle-init)
(rf/reg-event-fx :submit handle-submit)
(rf/reg-event-db :submit-success handle-next-state)
(rf/reg-event-db :submit-match-password handle-next-state)
(rf/reg-event-db :submit-short-password handle-next-state)
(rf/reg-event-db :submit-missing-first-name handle-next-state)
(rf/reg-event-db :submit-missing-last-name handle-next-state)
(rf/reg-event-db :change-first-name (partial handle-change-field :first-name))
(rf/reg-event-db :change-last-name (partial handle-change-field :last-name))
(rf/reg-event-db :change-password (partial handle-change-field :password))
(rf/reg-event-db :change-confirm-password (partial handle-change-field
                                                   :confirm-password))

;; -- Rendering ---------------------------------------------------------------

(defn input
  [value-kw]
  [:input {:value @(rf/subscribe [:input-value value-kw])
           :on-change (fn [e] (rf/dispatch [(keyword (str "change-"
                                                          (name value-kw)))
                                            (-> e .-target .-value)]))}])

(defn ui
  []
  [:div
   (when-let [failure @(rf/subscribe [:failure])]
     [:div {:style {:color "red"}} failure])
   (if @(rf/subscribe [:success])
     [:div {:style {:color "green"}}
      "Thank you, your account has been created!"]
     [:form
      "First name" [:br]
      [input :first-name] [:br]
      "Last name" [:br]
      [input :last-name] [:br]
      "Password" [:br]
      [input :password] [:br]
      "Confirm Password" [:br]
      [input :confirm-password] [:br]
      [:input {:type "button"
               :value "Register"
               :on-click (fn [e] (rf/dispatch [:submit]))}]])])

;; -- Entry Point -------------------------------------------------------------

(enable-console-print!)
(reagent/render [ui] (js/document.getElementById "app"))
(rf/dispatch [:init])
