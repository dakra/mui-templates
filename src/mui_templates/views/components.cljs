(ns mui-templates.views.components
  (:require
   [reagent.core :as reagent]
   [re-frame.core :as rf]
   [mui-templates.components :refer [copyright]]
   ["@material-ui/core" :refer [Paper Box Grid Typography Container] :as mui]
   ["@material-ui/core/styles" :refer [withStyles]]
   ["@material-ui/icons/ArtTrack" :default ArtTrack]
   ["@material-ui/lab" :refer [Autocomplete]]))

;;; Styles

(defn showcase-styles [^js/Mui.Theme theme]
  (clj->js
   {:icon {:marginRight (.spacing theme 2)}
    :heroContent {:backgroundColor (.. theme -palette -background -paper)
                  :padding (.spacing theme 8 0 6)}
    :paper {:margin (.spacing theme 2 0)
            :padding (.spacing theme 4 4)
            :display "flex"
            :overflow "auto"
            :flexDirection "column"}
    :footer {:backgroundColor (.. theme -palette -background -paper)
             :padding (.spacing theme 6)}}))

(def with-showcase-styles (withStyles showcase-styles))


;; Components

(defn drawer-icon []
  [:> ArtTrack])


(defn showcase [{:keys [^js classes] :as props}]
  (let [orders (rf/subscribe [:dashboard/orders])]
    [:<>
     [:div {:class (.-heroContent classes)}
      [:> Container {:maxWidth "sm"}
       [:> Typography {:component "h1" :variant "h2" :align "center"
                       :color "textPrimary" :gutterBottom true}
        "Miscellaneous Components"]
       [:> Typography {:variant "h5" :align "center" :color "textSecondary" :paragraph true}
        "Short demo of different Material UI components."]]]
     [:> Container {:class (.-cardGrid classes) :maxWidth "md"}
      [:> Grid {:container true :spacing 4}
       [:> Grid {:item true :xs 12}
        [:> Paper {:class (.-paper classes)}
         [:> Autocomplete {:options (map :name @orders)
                           :fullWidth true
                           :on-change (fn [_ order reason]
                                        (if (= reason "clear")
                                          (js/console.log "clear")
                                          (js/console.log order)))
                           :render-input (fn [^js params]
                                           (set! (.-variant params) "outlined")
                                           (set! (.-label params) "Select an order")
                                           (reagent/create-element mui/TextField params))}]]]]]]))

(defn main [{:keys [^js classes]}]
  [:<>
   [:> (with-showcase-styles (reagent/reactify-component showcase))]
   [:> Box {:pt 4}
    [copyright]]])
