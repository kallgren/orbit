(ns app.utils)

(defn parse-month-day [date-str year]
  (js/Date. (str date-str " " year)))

(defn days-until [date-str]
  (when date-str
    (let [now    (js/Date.)
          year   (.getFullYear now)
          target (parse-month-day date-str year)
          target (if (< target now) (doto target (.setFullYear (inc year))) target)
          diff   (- (.getTime target) (.getTime now))]
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
