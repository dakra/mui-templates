(ns mui-templates.views.sign-up
  (:require
   [clojure.string :as str]
   [reagent.core :as reagent]
   [re-frame.core :as rf]
   [day8.re-frame.tracing :refer-macros [fn-traced]]
   [mui-templates.components :refer [copyright]]
   ["@material-ui/core/CssBaseline" :default CssBaseline]
   ["@material-ui/core" :refer [Link Box Typography Container Grid Avatar TextField
                                FormControlLabel Checkbox Button]]
   ["@material-ui/core/styles" :refer [withStyles]]
   ["@material-ui/icons/LockOutlined" :default LockOutlinedIcon]
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
 (fn [{:keys [db]} [_ form]]
   (if (str/includes? (:email form) "@")
     {:db (assoc db :user (dissoc form :password))
      :navigate! [:routes/home]}
     {:db (assoc-in db [:errors :sign-up]
                    {:email "Invalid Email address"})})))

(rf/reg-event-db
 :sign-up/clear-errors
 (fn-traced [db [_ field]]
   (update-in db [:errors :sign-up] dissoc field)))


;; Components

(defn drawer-icon []
  [:> VpnKeyIcon])


(defn sign-up [{:keys [^js classes] :as props}]
  (let [empty-form {:first-name "" :last-name "" :email "" :password "" :marketing-emails? false}
        form (reagent/atom empty-form)
        errors (rf/subscribe [:sign-up/errors])]
    (fn []
      [:div {:class (.-paper classes)}
       [:> Avatar {:class (.-avatar classes)}
        [:> LockOutlinedIcon]]
       [:> Typography {:component "h1" :variant "h5"}
        "Sign up"]
       [:form {:class (.-form classes)
               :on-submit (fn [e]
                            (js/console.log e)
                            (.preventDefault e)
                            (rf/dispatch [:sign-up @form])
                            (reset! form empty-form))
               :no-validate true}
        [:> Grid {:container true :spacing 2}
         [:> Grid {:item true :xs 12 :sm 6}
          [:> TextField {:autoComplete "fname"
                         :name         "firstName"
                         :variant      "outlined"
                         :required     true
                         :fullWidth    true
                         :id           "firstName"
                         :label        "First name"
                         :auto-focus true
                         :value        (:first-name @form)
                         :on-change    #(swap! form assoc :first-name (-> % .-target .-value))}]]
         [:> Grid {:item true :xs 12 :sm 6}
          [:> TextField {:autoComplete "lname"
                         :name         "lastName"
                         :variant      "outlined"
                         :required     true
                         :fullWidth    true
                         :id           "lastName"
                         :label        "Last name"
                         :auto-focus true
                         :value        (:last-name @form)
                         :on-change    #(swap! form assoc :last-name (-> % .-target .-value))}]]
         [:> Grid {:item true :xs 12}
          [:> TextField {:autoComplete "email"
                         :name         "email"
                         :variant      "outlined"
                         :required     true
                         :fullWidth    true
                         :id           "email"
                         :label        "Email Address"
                         :auto-focus true
                         :error        (boolean (:email @errors))
                         :helperText   (:email @errors)
                         :on-focus     #(rf/dispatch [:sign-up/clear-errors :email])
                         :value        (:email @form)
                         :on-change    #(swap! form assoc :email (-> % .-target .-value))}]]
         [:> Grid {:item true :xs 12}
          [:> TextField {:autoComplete "current-password"
                         :name         "password"
                         :variant      "outlined"
                         :required     true
                         :fullWidth    true
                         :id           "password"
                         :label        "Password"
                         :auto-focus true
                         :error        (boolean (:password @errors))
                         :helperText   (:password @errors)
                         :value        (:password @form)
                         :on-focus     #(rf/dispatch [:sign-up/clear-errors :password])
                         :on-change    #(swap! form assoc :password (-> % .-target .-value))}]]
         [:> FormControlLabel
          {:control (reagent/as-component
                     [:> Checkbox {:checked (:marketing-emails? @form)
                                   :on-change #(swap! form assoc :marketing-emails? (-> % .-target .-checked))
                                   :color "primary"}])
           :label "I want to receive inspiration, marketing promotions and updates via email."}]]
        [:> Button {:type "submit"
                    :full-width true
                    :variant "contained"
                    :color "primary"
                    :class (.-submit classes)}
         "Sign Up"]
        [:> Grid {:container true :justify "flex-end"}
         [:> Grid {:item true}
          [:> Link {:href "#" :variant "body2"}
           "Already have an account? Sign in"]]]]])))

(defn main [{:keys [^js classes]}]
  [:> Container {:component "main" :max-width "xs"}
   [:> CssBaseline]
   [:> (with-sign-up-styles (reagent/reactify-component sign-up))]
   [:> Box {:mt 5}
    [copyright]]])
