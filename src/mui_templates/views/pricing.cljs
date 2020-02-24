(ns mui-templates.views.pricing
  (:require
   [goog.object :as gobj]
   [re-frame.core :as rf]
   [reagent.core :as reagent]
   ["@material-ui/core/CssBaseline" :default CssBaseline]
   ["@material-ui/core" :refer [Link Box Button Typography Container Grid] :as mui]
   ["@material-ui/core/styles" :refer [withStyles]]
   ["@material-ui/icons/StarBorder" :default StarIcon]
   ["@material-ui/icons/EuroSymbol" :default EuroSymbolIcon]))

;;; Styles

(defn pricing-styles [^js/Mui.Theme theme]
  (clj->js
   {"@global" {:ul {:margin 0 :padding 0 :listStyle "none"}}
    :appBar {:borderBottom (str "1px solid " (.. theme -palette -divider))}
    :toolbar {:flexWrap "wrap"}
    :toolbarTitle {:flexGrow 1}
    :link {:margin (.spacing theme 1 1.5)}
    :heroContent {:padding (.spacing theme 8 0 6)}
    :cardHeader {:backgroundColor (if (= (.. theme -palette -type) "dark")
                                    (gobj/get (.. theme -palette -grey) 700)
                                    (gobj/get (.. theme -palette -grey) 200))}
    :cardPricing {:display "flex"
                  :justifyContent "center"
                  :alignItems "baseline"
                  :marginBottom (.spacing theme 2)}
    :footer {:borderTop (str "1px solid " (.. theme -palette -divider))
             :marginTop (.spacing theme 8)
             :paddingTop (.spacing theme 3)
             :paddingBottom (.spacing theme 3)
             (.breakpoints.up theme "sm") {:paddingTop (.spacing theme 6)
                                           :paddingBottom (.spacing theme 6)}}}))

(def with-pricing-styles (withStyles pricing-styles))

;;; Subs

(rf/reg-sub
 :pricing/tiers
 (fn [db _]
   (:pricing/tiers db)))

(rf/reg-sub
 :pricing/footers
 (fn [db _]
   (:pricing/footers db)))


;; Components

(defn drawer-icon []
  [:> EuroSymbolIcon])

(defn copyright []
  [:> Typography {:variant "body2" :color "textSecondary" :align "center"}
   "Copyright ©"
   [:> Link {:color "inherit" :href "https://material-ui.com"}
    "Your Website"]
   (.getFullYear (js/Date.))])

(defn hero-unit [{:keys [^js classes] :as props}]
  [:> Container {:maxWidth "sm" :component "main" :class (.-heroContent classes)}
   [:> Typography {:component "h1" :variant "h2" :align "center"
                   :color "textPrimary" :gutterBottom true}
    "Pricing"]
   [:> Typography {:component "p" :variant "h5" :align "center"
                   :color "textSecondary"}
    "Quickly build an effective pricing table for your potential customers with this layout.
     It's built with default Material-UI components with little customization."]])

(defn card [^js classes {:keys [title price description button-variant button-text] :as tier}]
  [:> Grid {:item true :key title :xs 12 :sm (if (= title "Enterprise") 12 6) :md 4}
   [:> mui/Card
    [:> mui/CardHeader {:title title
                        :subheader (:subheader tier)
                        :titleTypographyProps {:align "center"}
                        :subheaderTypographyProps {:align "center"}
                        :action (when (= title "Pro") (reagent/as-component [:> StarIcon]))
                        :class (.-cardHeader classes)}]
    [:> mui/CardContent
     [:div {:class (.-cardPricing classes)}
      [:> Typography {:component "h2" :variant "h3" :color "textPrimary"}
       price]
      [:> Typography {:variant "h6" :color "textSecondary"}
       "/mo"]]
     [:ul
      (for [line description]
        (reagent/as-component
         [:> Typography {:component "li" :variant "subtitle1" :align "center" :key line}
          line]))]]
    [:> mui/CardActions
     [:> Button {:fullWidth true :variant button-variant :color "primary"}
      button-text]]]])

(defn pricing-cards [{:keys [^js classes] :as props}]
  (let [tiers (rf/subscribe [:pricing/tiers])]
    [:> Container {:component "main" :max-width "md"}
     [:> Grid {:container true :spacing 5 :alignItems "flex-end"}
      (for [{:keys [title] :as tier} @tiers]
        ^{:key title} [card classes tier])]]))

(defn footer [{:keys [^js classes] :as props}]
  (let [footers (rf/subscribe [:pricing/footers])]
    [:> Container {:maxWidth "md" :component "footer" :class (.-footer classes)}
     [:> Grid {:container true :spacing 4 :justify "space-evenly"}
      (for [{:keys [title description]} @footers]
        (reagent/as-component
         [:> Grid {:item true :xs 6 :sm 3 :key title}
          [:> Typography {:variant "h6" :color "textPrimary" :gutterBottom true}
           title]
          [:ul
           (for [i description]
             [:li {:key i}
              [:> Link {:href "#" :variant "subtitle1" :color "textSecondary"}
               i]])]]))]
     [:> Box {:mt 5}
      [copyright]]]))

(defn main [{:keys [^js classes]}]
  [:<>
   [:> CssBaseline]
   [:> (with-pricing-styles (reagent/reactify-component hero-unit))]
   [:> (with-pricing-styles (reagent/reactify-component pricing-cards))]
   [:> (with-pricing-styles (reagent/reactify-component footer))]])
