(ns mui-templates.views.album
  (:require
   [goog.object :as gobj]
   [reagent.core :as reagent]
   [re-frame.core :as rf]
   [day8.re-frame.tracing :refer-macros [fn-traced]]
   [mui-templates.components :refer [copyright]]
   ["@material-ui/core/CssBaseline" :default CssBaseline]
   ["@material-ui/core" :refer [Avatar Button TextField FormControlLabel
                                Checkbox Link Grid Box Typography Container]]
   ["@material-ui/core/styles" :refer [withStyles]]
   ["@material-ui/icons/PhotoAlbum" :default PhotoAlbumIcon]
   ["@material-ui/core" :as mui]))

;;; Styles

(defn album-styles [^js/Mui.Theme theme]
  (clj->js
   {:icon {:marginRight (.spacing theme 2)}
    :heroContent {:backgroundColor (.. theme -palette -background -paper)
                  :padding (.spacing theme 8 0 6)}
    :heroButtons {:marginTop (.spacing theme 4)}
    :cardGrid {:paddingTop (.spacing theme 8)
               :paddingBottom (.spacing theme 8)}
    :card {:height "100%"
           :display "flex"
           :flexDirection "column"}
    :cardMedia {:paddingTop "56.25%"}  ; 16:9
    :cardContent {:flexGrow 1}
    :footer {:backgroundColor (.. theme -palette -background -paper)
             :padding (.spacing theme 6)}}))

(def with-album-styles (withStyles album-styles))

;;; Subs

(rf/reg-sub
 :album/cards
 (fn [db _]
   (:album/cards db)))


;; Components

(defn drawer-icon []
  [:> PhotoAlbumIcon])


(defn card [^js classes c]
  [:> Grid {:item true :xs 12 :sm 6 :md 4}
   [:> mui/Card {:class (.-card classes)}
    [:> mui/CardMedia {:class (.-cardMedia classes)
                       :image "https://source.unsplash.com/random"
                       :title "Image title"}]
    [:> mui/CardContent {:class (.-cardContent classes)}
     [:> Typography {:gutterBottom true :variant "h5" :component "h2"}
      (str "Heading " c)]
     [:> Typography
      "This is a media card. You can use this section to describe the content."]]
    [:> mui/CardActions
     [:> Button {:size "small" :color "primary"}
      "View"]
     [:> Button {:size "small" :color "primary"}
      "Edit"]]]])


(defn album [{:keys [^js classes] :as props}]
  (let [cards (rf/subscribe [:album/cards])]
    [:<>
     [:div {:class (.-heroContent classes)}
      [:> Container {:maxWidth "sm"}
       [:> Typography {:component "h1" :variant "h2" :align "center"
                       :color "textPrimary" :gutterBottom true}
        "Album layout"]
       [:> Typography {:variant "h5" :align "center" :color "textSecondary" :paragraph true}
        "Something short and leading about the collection below—its contents, the creator, etc.
 Make it short and sweet, but not too short so folks don&apos;t simply skip over it entirely."]
       [:div {:class (.-heroButtons classes)}
        [:> Grid {:container true :spacing 2 :justify "center"}
         [:> Grid {:item true}
          [:> Button {:variant "contained" :color "primary"}
           "Main call to action"]]
         [:> Grid {:item true}
          [:> Button {:variant "outlined" :color "primary"}
           "Secondary action"]]]]]]
     [:> Container {:class (.-cardGrid classes) :maxWidth "md"}
      [:> Grid {:container true :spacing 4}
       (for [c @cards]
         ^{:key c} [card classes c])]]]))

(defn main [{:keys [^js classes]}]
  [:<>
   [:> (with-album-styles (reagent/reactify-component album))]
   [:> mui/Box {:pt 4}
    [copyright]]])
