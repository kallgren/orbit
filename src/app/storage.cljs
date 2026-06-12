(ns app.storage)

(def ^:private schedule-key "orbit/schedule")
(def ^:private done-key "orbit/done")

(defn- normalize-schedule [raw]
  ;; js->clj produces all-string keys; we want keyword category keys,
  ;; string interval keys (they're display labels), and keyword task keys.
  (into {}
        (for [[cat intervals] raw]
          [(keyword cat)
           (into {}
                 (for [[freq tasks] intervals]
                   [freq (mapv #(into {} (map (fn [[k v]] [(keyword k) v]) %)) tasks)]))])))

(defn read-schedule []
  (try
    (when-let [raw (.getItem js/localStorage schedule-key)]
      (normalize-schedule (js->clj (js/JSON.parse raw))))
    (catch :default _ nil)))

(defn read-done []
  (try
    (when-let [raw (.getItem js/localStorage done-key)]
      (set (js->clj (js/JSON.parse raw))))
    (catch :default _ nil)))

(defn write-done! [done]
  (try
    (.setItem js/localStorage done-key
              (js/JSON.stringify (clj->js (vec done))))
    (catch :default _ nil)))
