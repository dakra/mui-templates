(ns mui-templates.views.dashboard
  (:require
   [re-frame.core :as rf]
   [mui-templates.components :refer [copyright]]
   ["recharts" :as recharts]
   ["@material-ui/icons" :as icons]
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

;;; Events

(rf/reg-event-db
 :dashboard/randomize-chart
 ;; XXX: `fn-traced` doesn't support `for` or `map` yet. See https://github.com/day8/re-frame-debux#status
 ;; With fn-traced I have to define 2 :let blocks in `for` or it doesn't work
 (fn [db _]
   (update db :dashboard/chart-data (fn [data]
                                      (for [d data
                                            :let [time (:time d)
                                                  amount (rand-int 3000)]]
                                        {:time time :amount amount})))))

;; Components

(defn drawer-icon []
  [:> icons/Home])


(defn chart [{:keys [^js classes]}]
  (let [chart-data (rf/subscribe [:dashboard/chart-data])]
    [:<>
     [:> Grid {:justify "space-between" :container true}
      [:> Grid {:item true}
       [:> mui/Typography {:component "h2" :variant "h6" :color "primary" :gutter-bottom true}
        "Today"]]
      [:> Grid {:item true}
       [:> mui/Button {:color "primary" :style {:float "right"}
                       :on-click #(rf/dispatch [:dashboard/randomize-chart])}
        "Randomize data"]]]
     [:> recharts/ResponsiveContainer
      [:> recharts/LineChart {:data @chart-data :margin {:top 16 :right 16 :bottom 0 :left 24}}
       [:> recharts/XAxis {:dataKey :time}]  ; :stroke (.. classes -palette -text -secondary)
       [:> recharts/YAxis
        [:> recharts/Label {:angle 270 :position "left" :style {:text-anchor "middle"}}  ; :fill (.. classes -palette -text -primary)
         "Sales ($)"]]
       [:> recharts/Line {:type "monotone" :dataKey :amount :dot false}] ; :stroke (.. classes -palette -text -main)
       [:> recharts/CartesianGrid {:stroke "#ccc" :strokeDasharray "5 5"}]
       [:> recharts/Tooltip]]]]))


(defn deposits [{:keys [^js classes]}]
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


(defn orders [{:keys [^js classes]}]
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

(defn main [{:keys [^js classes]}]
  [:> Container {:max-width "lg" :class (.-container classes)}
   [:> Grid {:container true :spacing 3}
    [:> Grid {:item true :xs 12 :md 8 :lg 9}
     [:> Paper {:class [(.-paper classes) (.-fixedHeight classes)]}
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
