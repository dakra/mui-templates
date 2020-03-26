(ns mui-templates.main
  (:require
   [reagent.dom :as rdom]
   [re-frame.core :as rf]
   [day8.re-frame.tracing :refer-macros [fn-traced]]
   ["@material-ui/core" :refer [ThemeProvider createMuiTheme]]
   [mui-templates.routes :as routes]
   [mui-templates.dashboard :as dashboard]))


;;; Config

(def debug?
  ^boolean goog.DEBUG)

(defn dev-setup []
  (when debug?
    (println "dev mode")))

;;; DB

(def default-db
  {:dark-theme? false
   :drawer/open? true
   :dashboard/orders [{:id 0
                       :date "16 Mar, 2019"
                       :name "Elvis Presley"
                       :ship-to "Tupelo, MS"
                       :payment-method "VISA ⠀•••• 3719"
                       :amount 312.44}
                      {:id 1
                       :date "16 Mar, 2019"
                       :name "Paul McCartney"
                       :ship-to "London, UK"
                       :payment-method "VISA ⠀•••• 2574"
                       :amount 866.99}
                      {:id 2
                       :date "16 Mar, 2019"
                       :name "Tom Scholz"
                       :ship-to "Boston, MA"
                       :payment-method "MC ⠀•••• 1253"
                       :amount 100.81}
                      {:id 3
                       :date "16 Mar, 2019"
                       :name "Michael Jackson"
                       :ship-to "Gary, IN"
                       :payment-method "AMEX ⠀•••• 2000"
                       :amount 654.39}
                      {:id 4
                       :date "15 Mar, 2019"
                       :name "Bruce Springsteen"
                       :ship-to "Long Branch, NJ"
                       :payment-method "VISA ⠀•••• 5919"
                       :amount 212.79}]
   :dashboard/chart-data [{:time "00:00" :amount 0}
                          {:time "03:00" :amount 300}
                          {:time "06:00" :amount 600}
                          {:time "09:00" :amount 800}
                          {:time "12:00" :amount 1500}
                          {:time "15:00" :amount 2000}
                          {:time "18:00" :amount 2400}
                          {:time "21:00" :amount 2400}
                          {:time "24:00" :amount nil}]
   :album/cards (range 1 10)
   :pricing/tiers [{:title "Free"
                    :price "0"
                    :description ["10 users included"
                                  "2 GB of storage"
                                  "Help center access"
                                  "Email support"]
                    :button-text "Sign up for free"
                    :button-variant "outlined"}
                   {:title "Pro"
                    :subheader "Most popular"
                    :price "15"
                    :description ["20 users included"
                                  "10 GB of storage"
                                  "Help center access"
                                  "Priority email support"]
                    :button-text "Get started"
                    :button-variant "contained"}
                   {:title "Enterprise"
                    :price "30"
                    :description ["50 users included"
                                  "30 GB of storage"
                                  "Help center access"
                                  "Phone & email support"]
                    :button-text "Contact us"
                    :button-variant "outlined"}]
   :pricing/footers [{:title "Company"
                      :description ["Team" "History" "Contact us" "Locations"]}
                     {:title "Features"
                      :description ["Cool stuff" "Random feature"
                                    "Team feature" "Developer stuff" "Another one"]}
                     {:title "Resources"
                      :description ["Resource" "Resource name" "Another resource" "Final resource"]}
                     {:title "Legal"
                      :description ["Privacy policy" "Terms of use"]}]})


;;; Events

(rf/reg-event-db
 :initialize-db
 (fn-traced [_ _]
   default-db))

(rf/reg-event-db
 :toggle-dark-theme
 (fn-traced [db _]
   (update db :dark-theme? not)))

;;; Subs

(rf/reg-sub
 :db
 (fn [db]
   db))

(rf/reg-sub
 :dark-theme?
 (fn [db]
   (:dark-theme? db)))

(rf/reg-sub
 :errors
 (fn [db]
   (:errors db)))

;;; Styles

(defn custom-theme [dark-theme?]
  (createMuiTheme (clj->js {:palette {:type (if dark-theme? "dark" "light")}
                            :status {:danger "red"}})))

;;; Views

(defn main-shell [{:keys [router]}]
  (let [current-route @(rf/subscribe [:current-route])
        dark-theme? @(rf/subscribe [:dark-theme?])]
    [:> ThemeProvider {:theme (custom-theme dark-theme?)}
     [dashboard/page {:router router :current-route current-route}]]))

;;; Core

(defn ^:dev/after-load mount-root []
  (rf/clear-subscription-cache!)
  (routes/init-routes!)
  (rdom/render [main-shell {:router routes/router}]
               (.getElementById js/document "app")))

(defn init! []
  (rf/dispatch-sync [:initialize-db])
  (dev-setup)
  (mount-root))
