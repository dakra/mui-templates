(ns mui-templates.routes
  (:require
   [re-frame.core :as rf]
   [reitit.coercion.spec]
   [reitit.frontend]
   [reitit.frontend.easy :as rfe]
   [reitit.frontend.controllers :as rfc]
   [mui-templates.views.dashboard :refer [dashboard]]))


(defn log-fn [& args]
  (fn [& _] (apply js/console.log args)))


;;; Subs

(rf/reg-sub
 :current-route
 (fn [db]
   (:current-route db)))

;;; Events

(rf/reg-event-fx
 :navigate
 (fn [_cofx [_ & route]]
   {:navigate! route}))

;; Triggering navigation from events.
(rf/reg-fx
 :navigate!
 (fn [route]
   (apply rfe/push-state route)))


(rf/reg-event-db
 :navigated
 (fn [db [_ new-match]]
   (let [old-match   (:current-route db)
         controllers (rfc/apply-controllers (:controllers old-match) new-match)]
     (assoc db :current-route (assoc new-match :controllers controllers)))))

;;; Routes

(def routes
  ["/"
   [""
    {:name      ::home
     :view dashboard
     :link-text "Home"
     :icon :home
     :controllers
     [{;; Do whatever initialization needed for home page
       ;; I.e (rf/dispatch [::events/load-something-with-ajax])
       :start (log-fn "Entering home page")
       ;; Teardown can be done here.
       :stop  (log-fn "Leaving home page")}]}]
   ["sign-in"
    {:name      ::sign-in
     :view dashboard
     :link-text "Sign In"
     :icon :dashboard
     :controllers
     [{:start (log-fn "Entering login")
       :stop  (log-fn "Leaving login")}]}]
   ["sign-in-site"
    {:name      ::sign-in-site
     :view dashboard
     :link-text "Sign-in Site"
     :icon :shopping-cart
     :controllers
     [{:start (log-fn "Entering products")
       :stop  (log-fn "Leaving products")}]
     }]
   ["sign-up"
    {:name      ::sign-up
     :view dashboard
     :link-text "Sign Up"
     :icon :watch-later
     :controllers
     [{:start (log-fn "Entering watchdogs")
       :stop  (log-fn "Leaving watchdogs")}]}]
   ["album"
    {:name      ::album
     :view dashboard
     :link-text "Album"
     :icon :settings
     :controllers
     [{:start (log-fn "Entering settings")
       :stop  (log-fn "Leaving settings")}]}]
   ["pricing"
    {:name      ::pricing
     :view dashboard
     :link-text "Pricing"
     :icon :settings
     :controllers
     [{:start (log-fn "Entering settings")
       :stop  (log-fn "Leaving settings")}]}]
   ["charts"
    {:name      ::charts
     :view dashboard
     :link-text "Charts"
     :icon :settings
     :controllers
     [{:start (log-fn "Entering settings")
       :stop  (log-fn "Leaving settings")}]}]])

(def router
  (reitit.frontend/router
   routes
   {:data {:coercion reitit.coercion.spec/coercion}}))

(defn on-navigate [new-match]
  (when new-match
    (rf/dispatch [:navigated new-match])))

(defn init-routes! []
  (js/console.log "initializing routes")
  (rfe/start!
   router
   on-navigate
   {:use-fragment false}))
