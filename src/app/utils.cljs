(ns app.utils)

(defn parse-month-day [date-str year]
  (js/Date. (str date-str " " year)))

(defn date->month-day [date]
  (.toLocaleDateString date "en-US" #js {:month "short" :day "numeric"}))

(defn date->iso [date]
  (.toLocaleDateString date "en-CA"))

(defn days-until [iso-date-str]
  (when iso-date-str
    (let [diff (- (.getTime (js/Date. iso-date-str)) (.getTime (js/Date.)))]
      (Math/ceil (/ diff 86400000)))))

(defn date-display [date-str]
  (let [now       (js/Date.)
        year      (.getFullYear now)
        target    (parse-month-day date-str year)
        diff-ms   (- (.getTime now) (.getTime target))
        diff-days (Math/floor (/ diff-ms 86400000))
        weekday   (.toLocaleDateString target "en-US" #js {:weekday "short"})
        rel       (cond
                    (= diff-days 0)   "Today"
                    (= diff-days 1)   "Yesterday"
                    (> diff-days 0)   (str diff-days " days ago")
                    (>= diff-days -6) (.toLocaleDateString target "en-US" #js {:weekday "long"})
                    :else             (str "in " (- diff-days) " days"))]
    {:rel rel :full (str weekday " " date-str)}))

(defn due-text [days]
  (cond
    (= days 0) "Due today"
    (= days 1) "Due tomorrow"
    :else      (str "Due in " days " days")))
