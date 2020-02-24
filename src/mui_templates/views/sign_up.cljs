(ns mui-templates.views.sign-up
  (:require
   [reagent.core :as reagent]
   [re-frame.core :as rf]
   [day8.re-frame.tracing :refer-macros [fn-traced]]
   ["@material-ui/core/CssBaseline" :default CssBaseline]
   ["@material-ui/core" :refer [Link Box Typography Container]]
   ["@material-ui/core/styles" :refer [withStyles]]
   ["@material-ui/icons/VpnKey" :default VpnKeyIcon]))

;;; Styles

(defn sign-up-styles [^js/Mui.Theme theme]
  (clj->js
   {:paper {:marginTop (.spacing theme 8)
            :display "flex"
            :flexDirection "column"
            :alignItems "center"}
    :avatar {:margin (.spacing theme 1)
             :backgroundColor (.. theme -palette -secondary -main)}
    :form {:width "100%"  ; Fix IE 11 issue
           :marginTop (.spacing theme 1)}
    :submit {:margin (.spacing theme 3 0 2)}}))

(def with-sign-up-styles (withStyles sign-up-styles))

;;; Subs

(rf/reg-sub
 :sign-up/errors
 :<- [:errors]
 (fn [errors _]
   (:sign-up errors)))

;;; Events

(rf/reg-event-fx
 :sign-up
 (fn-traced [{:keys [db]} [_ {:keys [userid password remember?]}]]
   {:db (-> db
            (assoc-in [:auth :user-id] userid)
            (assoc-in [:auth :remember?] remember?))
    :navigate! [:routes/home]}))

(rf/reg-event-db
 :sign-up/clear-errors
 (fn-traced [db [_ field]]
   (update-in db [:errors :sign-up] dissoc field)))


;; Components

(defn drawer-icon []
  [:> VpnKeyIcon])

(defn copyright []
  [:> Typography {:variant "body2" :color "textSecondary" :align "center"}
   "Copyright ©"
   [:> Link {:color "inherit" :href "https://material-ui.com"}
    "Your Website"]
   (.getFullYear (js/Date.))])

(defn sign-up [{:keys [classes] :as props}]
  [:> Typography {:component "h1" :variant "h5"}
   "FIXME: Sign up"])

(defn main [{:keys [classes]}]
  [:> Container {:component "main" :max-width "xs"}
   [:> CssBaseline]
   [:> (with-sign-up-styles (reagent/reactify-component sign-up))]
   [:> Box {:mt 8}
    [copyright]]])
