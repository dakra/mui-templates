(ns mui-templates.views.sign-in-site
  (:require
   [goog.object :as gobj]
   [reagent.core :as reagent]
   [re-frame.core :as rf]
   [day8.re-frame.tracing :refer-macros [fn-traced]]
   ["@material-ui/core/CssBaseline" :default CssBaseline]
   ["@material-ui/core" :refer [Avatar Button TextField FormControlLabel
                                Checkbox Link Grid Box Typography Container]]
   ["@material-ui/core/styles" :refer [withStyles]]
   ["@material-ui/icons/Lock" :default LockIcon]
   ["@material-ui/icons/LockOutlined" :default LockOutlinedIcon]
   ["@material-ui/core" :as mui]))

;;; Styles

(defn sign-in-site-styles [^js/Mui.Theme theme]
  (clj->js
   {:root {:height "100vh"}
    :image {:backgroundImage "url(https://source.unsplash.com/random)"
            :backgroundRepeat "no-repeat"
            :backgroundColor (if (= (.. theme -palette -type) "dark")
                               (gobj/get (.. theme -palette -grey) 900)
                               (gobj/get (.. theme -palette -grey) 50))
            :backgroundSize "cover"
            :backgroundPosition "center"}
    :paper {:margin (.spacing theme 8 4)
            :display "flex"
            :flexDirection "column"
            :alignItems "center"}
    :avatar {:margin (.spacing theme 1)
             :backgroundColor (.. theme -palette -secondary -main)}
    :form {:width "100%"  ; Fix IE 11 issue
           :marginTop (.spacing theme 1)}
    :submit {:margin (.spacing theme 3 0 2)}}))

(def with-sign-in-site-styles (withStyles sign-in-site-styles))

;;; Subs

(rf/reg-sub
 :sign-in-site/errors
 :<- [:errors]
 (fn [errors _]
   (:sign-in-site errors)))

;;; Events

(rf/reg-event-fx
 :sign-in-site
 (fn-traced [{:keys [db]} [_ {:keys [userid password remember?]}]]
   (if (= password "top-secret")
     {:db (-> db
              (assoc-in [:auth :user-id] userid)
              (assoc-in [:auth :remember?] remember?))
      :navigate! [:routes/home]}
     {:db (assoc-in db [:errors :sign-in-site]
                    {:password "Wrong Password! (should be \"top-secret\")"})})))

(rf/reg-event-db
 :sign-in-site/clear-errors
 (fn-traced [db [_ field]]
   (update-in db [:errors :sign-in-site] dissoc field)))


;; Components

(defn drawer-icon []
  [:> LockIcon])

(defn copyright []
  [:> Typography {:variant "body2" :color "textSecondary" :align "center"}
   "Copyright ©"
   [:> Link {:color "inherit" :href "https://material-ui.com"}
    "Your Website"]
   (.getFullYear (js/Date.))])

(defn sign-in [{:keys [classes] :as props}]
  (let [form (reagent/atom {:userid "" :password "" :remember? false})
        errors (rf/subscribe [:sign-in-site/errors])]
    (fn []
      [:> Grid {:container true :component "main" :class (.-root classes)}
       [:> CssBaseline]
       [:> Grid {:item true :xs false :sm 4 :md 7 :class (.-image classes)}]
       [:> Grid {:item true :xs 12    :sm 8 :md 5
                 :component mui/Paper :elevation 6 :square true}
        [:div {:class (.-paper classes)}
         [:> Avatar {:class (.-avatar classes)}
          [:> LockOutlinedIcon]]
         [:> Typography {:component "h1" :variant "h5"}
          "Sign in"]
         [:form {:class (.-form classes)
                 :on-submit (fn [e]
                              (js/console.log e)
                              (.preventDefault e)
                              (rf/dispatch [:sign-in-site @form])
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
                         :on-focus     #(rf/dispatch [:sign-in-site/clear-errors :password])
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
             "Don't have an account? Sign Up"]]]
          [:> Box {:mt 5}
           [copyright]]]]]])))

(defn main [{:keys [classes]}]
  [:> (with-sign-in-site-styles (reagent/reactify-component sign-in))])
