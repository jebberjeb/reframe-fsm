(ns reframe-fsm.core
  (:require
    [clojure.string :as string]
    [reagent.core :as reagent]
    [re-frame.core :as rf])
  (:import
    (goog.net XhrIo)))

;; -- Subscriptions -----------------------------------------------------------

(rf/reg-sub :input-value
            (fn [db [_ value-kw]]
              (get db value-kw "")))

(rf/reg-sub :error
            (fn [db _]
              (get db :error)))

(rf/reg-sub :failure
            (fn [db _]
              (case (get db :failure)
                "short-password" "Password too short"
                "match-password" "Passwords don't match"
                "missing-first-name" "Missing first name"
                "missing-last-name" "Missing last name"
                "")))

(rf/reg-sub :success
            (fn [db _]
              (get db :success)))

(rf/reg-sub :disable-submit
            (fn [db _]
              (some? (get db :disable-submit))))

;; -- Events ------------------------------------------------------------------

(defn handle-submit-success
  [{:keys [db]} [_ response]]
  {:db (-> db
           (dissoc :failure)
           (dissoc :error)
           (dissoc :disable-submit)
           (assoc :success true))})

(defn handle-submit-failure
  [{:keys [db]} [_ response]]
  {:db (assoc db :failure response)})

(defn handle-submit-error
  [{:keys [db]} [_ response]]
  {:db (assoc db :error response)})

(defn handle-submit
  [{:keys [db]} _]
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
                                   :submit-failure
                                   :submit-error) response]))))
  {:db (assoc db :disable-submit true)})

(defn handle-change-first-name
  [db [_ value]]
  (let [db (-> db
               (dissoc :error)
               (dissoc :disable-submit)
               (assoc :first-name value))]
    (if (= "missing first name" (:failure db))
      (dissoc db :failure)
      db)))

(defn handle-change-last-name
  [db [_ value]]
  (let [db (-> db
               (dissoc :error)
               (dissoc :disable-submit)
               (assoc :last-name value))]
    (if (= "missing last name" (:failure db))
      (dissoc db :failure)
      db)))

(defn handle-change-password
  [db [_ value]]
  (let [db (-> db
               (dissoc :error)
               (dissoc :disable-submit)
               (assoc :password value))]
    (if (and (:failure db)
             (string/index-of (:failure db) "password"))
      (dissoc db :failure)
      db)))

(defn handle-change-confirm-password
  [db [_ value]]
  (let [db (-> db
               (dissoc :error)
               (dissoc :disable-submit)
               (assoc :confirm-password value))]
    (if (and (:failure db)
             (string/index-of (:failure db) "password"))
      (dissoc db :failure)
      db)))

(rf/reg-event-fx :submit handle-submit)
(rf/reg-event-fx :submit-success handle-submit-success)
(rf/reg-event-fx :submit-failure handle-submit-failure)
(rf/reg-event-db :change-first-name handle-change-first-name)
(rf/reg-event-db :change-last-name handle-change-last-name)
(rf/reg-event-db :change-password handle-change-password)
(rf/reg-event-db :change-confirm-password handle-change-confirm-password)

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
      [:input {:disable @(rf/subscribe [:disable-submit])
               :type "button"
               :value "Register"
               :on-click (fn [e] (rf/dispatch [:submit]))}]])])

;; -- Entry Point -------------------------------------------------------------

(enable-console-print!)
(reagent/render [ui] (js/document.getElementById "app"))
