(ns mui-templates.main
  (:require
   [reagent.core :as reagent]
   [re-frame.core :as rf]
   ;; ["@material-ui/core/colors/purple" :refer [purple]]
   ;; ["@material-ui/core/colors/green" :refer [green]]
   ["@material-ui/core" :refer [Typography Container Button Grid Icon Slider Paper ThemeProvider createMuiTheme
                                makeStyles Input Switch FormGroup FormControlLabel]]
   ;; [re-com.core :as rc]
   [day8.re-frame.tracing :refer-macros [fn-traced defn-traced]]
   [mui-templates.routes :as routes]
   [mui-templates.dashboard :as dashboard]))


;;; Config
(def debug?
  ^boolean goog.DEBUG)

(defn dev-setup []
  (when debug?
    (println "dev mode")))

;;; DB
(def default-db {})

;;; Events
(rf/reg-event-db
 ::initialize-db
 (fn-traced [_ _]
   default-db))

;;; Subs
(rf/reg-sub
 :db
 (fn [db]
   db))


;;; Styles
(def custom-theme (createMuiTheme (clj->js {:palette {:type "light"}
                                            :status {:danger "red"}})))

;;; Views

(defn main-shell [{:keys [router]}]
  (let [current-route @(rf/subscribe [:current-route])]
    [:> ThemeProvider {:theme custom-theme}
     [dashboard/page {:router router :current-route current-route}]]))

;;; Core
(defn ^:dev/after-load mount-root []
  (rf/clear-subscription-cache!)
  (routes/init-routes!)
  (reagent/render [main-shell {:router routes/router}]
                  (.getElementById js/document "app")))

(defn init! []
  (rf/dispatch-sync [::initialize-db])
  (dev-setup)
  (mount-root))
