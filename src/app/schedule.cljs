(ns app.schedule
  (:require [app.utils :refer [parse-month-day date->month-day date->iso]]))

(defn- interval->months [interval]
  (case interval
    "monthly"  1
    "2 months" 2
    "quarterly" 3
    "6 months" 6
    "yearly"   12
    1))

(defn- add-months [date n]
  (let [d (js/Date. (.getTime date))]
    (.setMonth d (+ (.getMonth d) n))
    d))

(defn- expand-task [category freq task year]
  (let [{:keys [name anchor before]} task
        before     (or before 0)
        months     (interval->months freq)
        anchor-d   (parse-month-day anchor year)
        year-start (js/Date. year 0 1)
        year-end   (js/Date. year 11 31)
        today      (js/Date.)
        first-d    (loop [d anchor-d]
                     (let [prev (add-months d (- months))]
                       (if (< prev year-start)
                         d
                         (recur prev))))]
    (loop [d first-d results []]
      (if (> d year-end)
        results
        (let [display-d (js/Date. (- (.getTime d) (* before 86400000)))]
          (recur
           (add-months d months)
           (conj results
                 {:category  category
                  :name      name
                  :freq      freq
                  :date      (date->month-day display-d)
                  :deadline  (when (pos? before) (date->iso d))
                  :upcoming? (> display-d today)
                  :key       (str name ":" (date->iso d))})))))))

(defn expand-schedule [schedule year]
  (for [[category intervals] schedule
        [freq tasks] intervals
        task tasks
        occurrence (expand-task category freq task year)]
    occurrence))
