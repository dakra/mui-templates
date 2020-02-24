(ns mui-templates.routes
  (:require
   [re-frame.core :as rf]
   [reitit.coercion.spec]
   [reitit.frontend]
   [reitit.frontend.easy :as rfe]
   [reitit.frontend.controllers :as rfc]
   [mui-templates.views.sign-in :as sign-in]
   [mui-templates.views.sign-in-site :as sign-in-site]
   [mui-templates.views.album :as album]
   [mui-templates.views.dashboard :as dashboard]))


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
    {:name      :routes/home
     :view dashboard/main
     :link-text "Home"
     :icon dashboard/drawer-icon
     :controllers
     [{;; Do whatever initialization needed for home page
       ;; I.e (rf/dispatch [:events/load-something-with-ajax])
       :start (log-fn "Entering home page")
       ;; Teardown can be done here.
       :stop  (log-fn "Leaving home page")}]}]
   ["sign-in"
    {:name      :routes/sign-in
     :view sign-in/main
     :link-text "Sign In"
     :icon sign-in/drawer-icon
     :controllers
     [{:start (log-fn "Entering sign-in")
       :stop  (log-fn "Leaving sign-in")}]}]
   ["sign-in-site"
    {:name      :routes/sign-in-site
     :view sign-in-site/main
     :link-text "Sign-in Site"
     :icon sign-in-site/drawer-icon
     :controllers
     [{:start (log-fn "Entering sign-in-site")
       :stop  (log-fn "Leaving sign-in-site")}]
     }]
   ["sign-up"
    {:name      :routes/sign-up
     :view dashboard/main
     :link-text "Sign Up"
     :icon dashboard/drawer-icon
     :controllers
     [{:start (log-fn "Entering sign-up")
       :stop  (log-fn "Leaving sign-up")}]}]
   ["album"
    {:name      :routes/album
     :view album/main
     :link-text "Album"
     :icon album/drawer-icon
     :controllers
     [{:start (log-fn "Entering album")
       :stop  (log-fn "Leaving album")}]}]
   ["pricing"
    {:name      :routes/pricing
     :view dashboard/main
     :link-text "Pricing"
     :icon dashboard/drawer-icon
     :controllers
     [{:start (log-fn "Entering settings")
       :stop  (log-fn "Leaving settings")}]}]
   ["charts"
    {:name      :routes/charts
     :view dashboard/main
     :link-text "Charts"
     :icon dashboard/drawer-icon
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
