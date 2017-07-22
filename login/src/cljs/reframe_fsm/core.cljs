(ns reframe-fsm.core
  (:require
    [ajax.core :as ajax]
    [clojure.string :as string]
    [day8.re-frame.http-fx]
    [reagent.core :as reagent]
    [re-frame.core :as rf]))

(def login-state-machine
  {nil                {:init               :ready}
   :ready             {:login-no-password  :password-required
                       :login-no-email     :email-required
                       :try-login          :logging-in}
   :logging-in        {:login-bad-password :invalid-password
                       :login-no-user      :user-not-exist
                       :login-success      :logged-in}
   :email-required    {:set-email          :ready}
   :password-required {:set-password       :ready}
   :user-not-exist    {:set-email          :ready}
   :invalid-password  {:set-password       :ready}})

;; -- Subscriptions -----------------------------------------------------------

(rf/reg-sub
  :state
  (fn [db _] (get db :state)))

(rf/reg-sub
  :failure
  (fn [db _] (rf/subscribe [:state]))
  (fn [state _] (case state
                  :email-required "email required"
                  :password-required "password required"
                  :user-not-exist "user not found"
                  :invalid-password "invalid password"
                  nil)))

(rf/reg-sub
  :password
  (fn [db _] (get db :password)))

(rf/reg-sub
  :email
  (fn [db _] (get db :email)))

(rf/reg-sub
  :login-disabled?
  (fn [db _] (rf/subscribe [:state]))
  (fn [state _] (not= state :ready)))

;; -- Events ------------------------------------------------------------------

(defn next-state
  [fsm current-state transition]
  (get-in fsm [current-state transition]))

(defn update-next-state
  [db event]
  (if-let [new-state (next-state login-state-machine (:state db) event)]
    (assoc db :state new-state)
    db))

(defn handle-next-state
  [db [event _]]
  (update-next-state db event))

(defn handle-set-email
  [db [event email]]
  (-> db
      (assoc :email email)
      (update-next-state event)))

(defn handle-set-password
  [db [event password]]
  (-> db
      (assoc :password password)
      (update-next-state event)))

(defn handle-login-clicked
  [{:keys [db]} _]
  (let [{:keys [email password]} db]
    {:db db
     :dispatch (cond (string/blank? email)
                     [:login-no-email]
                     (string/blank? password)
                     [:login-no-password]
                     :else
                     [:try-login])}))

(defn handle-try-login
  [{:keys [db]} [event _]]
  (let [{:keys [email password]} db]
    {:db (update-next-state db event)
     :http-xhrio {:uri (str "/login?email=" email
                            "&password=" password)
                  :response-format (ajax/text-response-format)
                  :method :get
                  :on-success [:login-success]
                  :on-failure [:login-failure]}}))

(defn handle-login-failure
  [{:keys [db]} [_ {:keys [response]}]]
  {:db db
   :dispatch (case response
               "user not found"
               [:login-no-user]
               "invalid password"
               [:login-bad-password])})

(def debug (rf/after (fn [db event]
                       (.log js/console "=======")
                       (.log js/console "state: " (str (:state db)))
                       (.log js/console "event: " (str event)))))

(def interceptors [debug])

(rf/reg-event-db :init interceptors handle-next-state)
(rf/reg-event-db :set-email interceptors handle-set-email)
(rf/reg-event-db :set-password interceptors handle-set-password)
;; Not a state machine transition
(rf/reg-event-fx :login-clicked interceptors handle-login-clicked)
(rf/reg-event-db :login-no-email interceptors handle-next-state)
(rf/reg-event-db :login-no-password interceptors handle-next-state)
(rf/reg-event-fx :try-login interceptors handle-try-login)
;; Not a state machine transition
(rf/reg-event-fx :login-failure interceptors handle-login-failure)
(rf/reg-event-db :login-no-user interceptors handle-next-state)
(rf/reg-event-db :login-bad-password interceptors handle-next-state)
(rf/reg-event-db :login-success interceptors handle-next-state)

;; -- Rendering ---------------------------------------------------------------

(defn ui
  []
  [:div
   (when-let [failure @(rf/subscribe [:failure])]
     [:div {:style {:color "red"}} failure])
   [:form
    "Email" [:br]
    [:input
     {:value @(rf/subscribe [:email])
      :on-change #(rf/dispatch [:set-email (-> % .-target .-value)])}] [:br]
    "Password" [:br]
    [:input
     {:value @(rf/subscribe [:password])
      :on-change #(rf/dispatch [:set-password (-> % .-target .-value)])
      :type "password"}] [:br]
    "Password" [:br]
    [:input {:type "button"
             :value "Login"
             :disabled @(rf/subscribe [:login-disabled?])
             :on-click (fn [e] (rf/dispatch [:login-clicked]))}]]])

;; -- Entry Point -------------------------------------------------------------

(enable-console-print!)
(reagent/render [ui] (js/document.getElementById "app"))
(rf/dispatch [:init])

;; TODO - shouldn't need :init handler anymore, next-state should work.

(comment
  (require '[fsmviz.core])
  (fsmviz.core/generate-image login-state-machine "fsm"))
