(ns mui-templates.views.sign-in
  (:require
   [reagent.core :as reagent]
   [re-frame.core :as rf]
   [day8.re-frame.tracing :refer-macros [fn-traced]]
   ["@material-ui/core/CssBaseline" :default CssBaseline]
   ["@material-ui/core" :refer [Avatar Button TextField FormControlLabel
                                Checkbox Link Grid Box Typography Container]]
   ["@material-ui/core/styles" :refer [withStyles]]
   ["@material-ui/icons/LockOutlined" :default LockOutlinedIcon]))

;;; Styles

(defn sign-in-styles [^js/Mui.Theme theme]
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

(def with-sign-in-styles (withStyles sign-in-styles))

;;; Subs

(rf/reg-sub
 :dashboard/orders
 (fn [db]
   (:dashboard/orders db)))

(rf/reg-sub
 :dashboard/chart-data
 (fn [db]
   (:dashboard/chart-data db)))

(rf/reg-sub
 :login/errors
 :<- [:errors]
 (fn [errors _]
   (:login errors)))

;;; Events

(rf/reg-event-fx
 :login
 (fn-traced [{:keys [db]} [_ {:keys [userid password remember?]}]]
   (if (= password "top-secret")
     {:db (-> db
              (assoc-in [:auth :user-id] userid)
              (assoc-in [:auth :remember?] remember?))
      :navigate! [:routes/home]}
     {:db (assoc-in db [:errors :login] {:password "Wrong Password! (should be \"top-secret\")"})})))

(rf/reg-event-db
 :clear-errors
 (fn-traced [db [_ field]]
   (update-in db [:errors :login] dissoc field)))


;; Components

(defn drawer-icon []
  [:> LockOutlinedIcon])

(defn copyright []
  [:> Typography {:variant "body2" :color "textSecondary" :align "center"}
   "Copyright ©"
   [:> Link {:color "inherit" :href "https://material-ui.com"}
    "Your Website"]
   (.getFullYear (js/Date.))])

(defn sign-in [{:keys [classes] :as props}]
  (let [form (reagent/atom {:userid "" :password "" :remember? false})
        errors (rf/subscribe [:login/errors])]
    (fn []
      [:> CssBaseline]
      [:div {:class (.-paper classes)}
       [:> Avatar {:class (.-avatar classes)}
        [:> LockOutlinedIcon]]
       [:> Typography {:component "h1" :variant "h5"}
        "Sign in"
        (when (:need-login? @errors)
          " - Need to login first")]
       [:form {:class (.-form classes)
               :on-submit (fn [e]
                            (js/console.log e)
                            (.preventDefault e)
                            (rf/dispatch [:login @form])
                            (reset! form {:userid "" :password "" :remember? (:remember? @form)}))
               :no-validate true}
        [:> TextField {:variant      "outlined"
                       :margin       "normal"
                       :required     true
                       :fullWidth    true
                       :on-change    #(swap! form assoc :userid (-> % .-target .-value))
                       :value        (:userid @form)
                       :id           "email"
                       :label        "Email Address"
                       :name         "email"
                       :autoComplete "email"
                       :autoFocus    true}]
        [:> TextField {:variant      "outlined"
                       :margin       "normal"
                       :error        (boolean (:password @errors))
                       :helperText   (:password @errors)
                       :required     true
                       :fullWidth    true
                       :on-focus     #(rf/dispatch [:clear-errors :password])
                       :on-change    #(swap! form assoc :password (-> % .-target .-value))
                       :value        (:password @form)
                       :id           "password"
                       :label        "Password"
                       :name         "password"
                       :autoComplete "current-password"}]
        [:> FormControlLabel
         {:control (reagent/as-component
                    [:> Checkbox {:checked (:remember? @form)
                                  :on-change #(swap! form assoc :remember? (-> % .-target .-checked))
                                  :color "primary"}])
          :label "Remember me"}]
        [:> Button {:type "submit"
                    :full-width true
                    :variant "contained"
                    :color "primary"
                    :class (.-submit classes)}
         "Sign In"]
        [:> Grid {:container true}
         [:> Grid {:item true :xs true}
          [:> Link {:href "#" :variant "body2"}
           "Forgot password?"]]
         [:> Grid {:item true}
          [:> Link {:href "#" :variant "body2"}
           "Don't have an account?Sign Up"]]]]])))

(defn main [{:keys [classes]}]
  [:> Container {:component "main" :max-width "xs"}
   [:> CssBaseline]
   [:> (with-sign-in-styles (reagent/reactify-component sign-in))]
   [:> Box {:mt 8}
    [copyright]]])
