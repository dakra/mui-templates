(ns mui-templates.dashboard
  (:require
   [reagent.core :as reagent]
   [reitit.frontend.easy :as rfe]
   [reitit.core :as reitit]
   [reitit.frontend.easy :refer [href]]
   ["@material-ui/core" :as mui]
   ["@material-ui/core/AppBar" :default AppBar]
   ["@material-ui/core/Badge" :default Badge]
   ["@material-ui/core/Box" :default Box]
   ["@material-ui/core/Container" :default Container]
   ["@material-ui/core/CssBaseline" :default CssBaseline]
   ["@material-ui/core/Divider" :default Divider]
   ["@material-ui/core/Drawer" :default Drawer]
   ["@material-ui/core/Grid" :default Grid]
   ["@material-ui/core/IconButton" :default IconButton]
   ["@material-ui/core/Link" :default Link]
   ["@material-ui/core/List" :default List]
   ;; ["@material-ui/core/ListItem" :default ListItem]
   ["@material-ui/core/ListItemIcon" :default ListItemIcon]
   ["@material-ui/core/ListItemText" :default ListItemText]
   ["@material-ui/core/styles" :refer [withStyles]]
   ["@material-ui/icons" :as icons]
   ["@material-ui/icons/Menu" :default MenuIcon]
   ["@material-ui/icons/ChevronLeft" :default ChevronLeftIcon]
   ["@material-ui/icons/Notifications" :default NotificationsIcon]
   ["@material-ui/core/Paper" :default Paper]
   ["@material-ui/core/Toolbar" :default Toolbar]
   ["@material-ui/core/Typography" :default Typography]))

(def icon-map
  {:watch-later icons/WatchLater
   :settings icons/Settings
   :bar-chart icons/BarChart
   :people icons/People
   :shopping-cart icons/ShoppingCart
   :dashboard icons/Dashboard
   :home icons/Home})

;;; Styles

(def drawer-width 240)

(defn dashboard-styles [^js/Mui.Theme theme]
  (clj->js
   {:root {:display "flex"}
    :toolbar {:paddingRight 24}  ; keep right padding when drawer closed
    :toolbarIcon (merge {:display "flex"
                         :alignItems "center"
                         :justifyContent "flex-end"
                         :padding "0 8px"}
                        (js->clj (.. theme -mixins -toolbar)))
    :appBar {:zIndex (+ (.. theme -zIndex -drawer) 1)
             :transition (.. theme -transitions
                             (create #js ["width" "margin"]
                                     #js {:easing (.. theme -transitions -easing -sharp)
                                          :duration (.. theme -transitions -duration -leavingScreen)}))}
    :appBarShift {:marginLeft drawer-width
                  :width (str "calc(100% - " drawer-width "px)")
                  :transition (.. theme -transitions
                                  (create #js ["width" "margin"]
                                          #js {:easing (.. theme -transitions -easing -sharp)
                                               :duration (.. theme -transitions -duration -enteringScreen)}))}
    :menuButton {:marginRight 36}
    :menuButtonHidden {:display "none"}
    :title {:flexGrow 1}
    :drawerPaper {:position "relative"
                  :whiteSpace "nowrap"
                  :width drawer-width
                  :transition (.. theme -transitions
                                  (create "width"
                                          #js {:easing (.. theme -transitions -easing -sharp)
                                               :duration (.. theme -transitions -duration -enteringScreen)}))}
    :drawerPaperClose {:overflowX "hidden"
                       :transition (.. theme -transitions
                                       (create "width"
                                               #js {:easing (.. theme -transitions -easing -sharp)
                                                    :duration (.. theme -transitions -duration -leavingScreen)}))
                       :width (.spacing theme 7)
                       (.breakpoints.up theme "sm") {:width (.spacing theme 9)}}
    :appBarSpacer (.. theme -mixins -toolbar)
    :content {:flexGrow 1
              :height "100vh"
              :overflow "auto"}
    :container {:paddingTop (.spacing theme 4)
                :paddingBottom (.spacing theme 4)}
    :paper {:padding (.spacing theme 4)
            :display "flex"
            :overflow "auto"
            :flexDirection "column"}
    :fixedHeight {:height 240}}))

(def with-dashboard-styles (withStyles dashboard-styles))

;;; Components

(defn list-item [{:keys [selected route-name text icon]}]
  [:> mui/ListItem {:button true
                    :selected selected
                    :on-click #(rfe/push-state route-name)}
   [:> ListItemIcon [:> (get icon-map icon)]]
   [:> ListItemText {:primary text}]])


(defn dashboard [{:keys [router current-route]}]
  (let [state (reagent/atom {:open true})]
    (fn [{:keys [classes] :as props}]
      (let [open? (:open @state)]
        [:div {:class (.-root classes)}
         [:> CssBaseline]

         [:> AppBar {:position "absolute"
                     :class [(.-appBar classes)
                             (when open? (.-appBarShift classes))]}
          [:> Toolbar {:class (.-toolbar classes)}
           [:> IconButton {:edge "start"
                           :color "inherit"
                           :aria-label "open drawer"
                           :on-click (fn [e] (swap! state assoc :open true))  ; Open drawer
                           :class [(.-menuButton classes) (when open? (.-menuButtonHidden classes))]}
            [:> MenuIcon]]
           [:> Typography {:component "h1"
                           :variant "h6"
                           :color "inherit"
                           :no-wrap true
                           :class (.-title classes)}
            "Dashboard"]
           [:> IconButton {:color "inherit"}
            [:> Badge {:badgeContent 4 :color "secondary"}
             [:> NotificationsIcon]]]]]

         [:> Drawer {:variant "permanent"
                     :classes {:paper (str (.-drawerPaper classes) " "
                                           (if open? "" (.-drawerPaperClose classes)))}
                     :open open?}
          [:div {:class (.-toolbarIcon classes)}
           [:> IconButton {:on-click #(swap! state assoc :open false)}  ; Close drawer
            [:> ChevronLeftIcon]]]
          [:> Divider]
          [:> List
           (for [route-name (reitit/route-names router)
                 :let [route (reitit/match-by-name router route-name)
                       text (-> route :data :link-text)
                       icon (-> route :data :icon)]]
             #_(reagent/as-component [:> mui/ListItem {:button true}
                                      [:> ListItemIcon [:> InboxIcon]]
                                      [:> ListItemText {:primary text}]])
             ^{:key route-name}
             [list-item {:text text
                         :icon icon
                         :route-name route-name
                         :selected (= route-name (-> current-route :data :name))}])]
          [:> Divider]
          ;; [:> List (secondary-list-items)]
          ]
         [:main {:class (.-content classes)}
          [:div {:class (.-appBarSpacer classes)}]
          (when current-route
            ;; [:> (with-dashboard-styles (reagent/reactify-component (-> current-route :data :view)))]
            [(-> current-route :data :view) {:classes classes}])
          ]]))))

(defn page [{:keys [router current-route]}]
  [:> (with-dashboard-styles
        (reagent/reactify-component
         (dashboard {:router router :current-route current-route})))])
