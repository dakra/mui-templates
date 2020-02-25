(ns mui-templates.components
  (:require
   ["@material-ui/core/Typography" :default Typography]
   ["@material-ui/core/Link" :default Link]))

;;; General components that can be used in multiple views

(defn copyright []
  [:> Typography {:variant "body2" :color "textSecondary" :align "center"}
   "Copyright ©"
   [:> Link {:color "inherit" :href "https://material-ui.com"}
    "Your Website "]
   (.getFullYear (js/Date.))])
