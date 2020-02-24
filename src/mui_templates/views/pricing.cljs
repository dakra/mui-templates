(ns mui-templates.views.pricing
  (:require
   [reagent.core :as reagent]
   ["@material-ui/core/CssBaseline" :default CssBaseline]
   ["@material-ui/core" :refer [Link Box Typography Container]]
   ["@material-ui/core/styles" :refer [withStyles]]
   ["@material-ui/icons/EuroSymbol" :default EuroSymbolIcon]))

;;; Styles

(defn pricing-styles [^js/Mui.Theme theme]
  (clj->js
   {:appBar {:borderBottom (str "1px solid " (.. theme -palette -divider))}
    :toolbar {:flexWrap "wrap"}}))

(def with-pricing-styles (withStyles pricing-styles))


;; Components

(defn drawer-icon []
  [:> EuroSymbolIcon])

(defn copyright []
  [:> Typography {:variant "body2" :color "textSecondary" :align "center"}
   "Copyright ©"
   [:> Link {:color "inherit" :href "https://material-ui.com"}
    "Your Website"]
   (.getFullYear (js/Date.))])

(defn pricing [{:keys [classes] :as props}]
  [:> Typography {:component "h1" :variant "h5"}
   "FIXME: Pricing"])

(defn main [{:keys [classes]}]
  [:> Container {:component "main" :max-width "xs"}
   [:> CssBaseline]
   [:> (with-pricing-styles (reagent/reactify-component pricing))]
   [:> Box {:mt 8}
    [copyright]]])
