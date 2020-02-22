(ns mui-templates.views.dashboard
  (:require
   [reagent.core :as reagent]
   [re-frame.core :as rf]
   ["recharts" :as recharts]
   ["@material-ui/core" :refer [Container Grid Paper] :as mui]))

;;; Subs
(rf/reg-sub
 :dashboard/orders
 (fn [db]
   (:dashboard/orders db)))

(rf/reg-sub
 :dashboard/chart-data
 (fn [db]
   (:dashboard/chart-data db)))

(defn copyright []
  [:> mui/Typography {:variant "body2" :color "textSecondary" :align "center"}
   "Copyright ©"
   [:> mui/Link {:color "inherit" :href "https://material-ui.com"}
    "Your Website"]
   (.getFullYear (js/Date.))])


(defn chart [{:keys [classes]}]
  (let [chart-data (rf/subscribe [:dashboard/chart-data])]
    [:<>
     [:> mui/Typography {:component "h2" :variant "h6" :color "primary" :gutter-bottom true}
      "Today"]
     [:> recharts/ResponsiveContainer
      [:> recharts/LineChart {:data @chart-data :margin {:top 16 :right 16 :bottom 0 :left 24}}
       [:> recharts/XAxis {:dataKey :time}]  ; :stroke (.. classes -palette -text -secondary)
       [:> recharts/YAxis]
       [:> recharts/Line {:type "monotone" :dataKey :amount :fill "#8884d8" :dot false}]
       [:> recharts/CartesianGrid {:stroke "#ccc" :strokeDasharray "5 5"}]
       [:> recharts/Tooltip]]]]))


(defn deposits [{:keys [classes]}]
  [:<>
   [:> mui/Typography {:component "h2" :variant "h6" :color "primary" :gutter-bottom true}
    "Recent Deposits"]
   [:> mui/Typography {:component "p" :variant "h4"}
    "$3,024.00"]
   [:> mui/Typography {:color "textSecondary" :style {:flex 1} } ; :class (.-depositContext classes)
    "on 15 March, 2019"]
   [:div
    [:> mui/Link {:color "primary" :href "#" :on-click #(.preventDefault %)} "View balance"]]])

(defn table-row [{:keys [date name ship-to payment-method amount]}]
  [:> mui/TableRow
   [:> mui/TableCell date]
   [:> mui/TableCell name]
   [:> mui/TableCell ship-to]
   [:> mui/TableCell payment-method]
   [:> mui/TableCell {:align "right"} amount]])


(defn orders [{:keys [classes]}]
  (let [orders (rf/subscribe [:dashboard/orders])]
    [:<>
     [:> mui/Typography {:component "h2" :variant "h6" :color "primary" :gutter-bottom true}
      "Your Products"]
     [:> mui/Table {:size "small"}
      [:> mui/TableHead
       [:> mui/TableRow
        [:> mui/TableCell "Date"]
        [:> mui/TableCell "Name"]
        [:> mui/TableCell "Ship To"]
        [:> mui/TableCell "Payment Method"]
        [:> mui/TableCell {:align "right"} "Sale Amount"]]]
      [:> mui/TableBody
       (for [order @orders]
         ^{:key (:id order)} [table-row order]
         #_(reagent/as-component [:> mui/ListItem {:button true}
                                  [:> ListItemIcon [:> InboxIcon]]
                                  [:> ListItemText {:primary text}]])
         #_[list-item {:text text
                       :icon icon
                       :route-name route-name
                       :selected (= route-name (-> current-route :data :name))}])]]]))

(defn dashboard [{:keys [classes]}]
  [:> Container {:max-width "lg" :class (.-container classes)}

   [:> Grid {:container true :spacing 3}
    [:> Grid {:item true :xs 12 :md 8 :lg 9}
     [:> Paper {:class [(.-fixedHeight classes)]}
      [chart {:classes classes}]]]
    [:> Grid {:item true :xs 12 :md 4 :lg 3}
     [:> Paper {:class [(.-paper classes) (.-fixedHeight classes)]}
      [deposits {:classes classes}]]]
    [:> Grid {:item true :xs 12}
     [:> Paper {:class (.-paper classes)}
      [orders {:classes classes}]]]]
   [:> mui/Grid {:container true :spacing 3}]

   [:> mui/Box {:pt 4}
    [copyright]]])
