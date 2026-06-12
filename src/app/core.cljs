(ns app.core
  (:require [uix.core :refer [defui $ use-state]]
            [uix.dom]
            [app.utils :refer [parse-month-day days-until date-display due-text]]))

;; ── Data ────────────────────────────────────────────────────────────────────

(def category-order [:digital :household])

(def category-labels {:digital "Digital" :household "Household"})

(def tasks
  ;; :upcoming? true = due in the future, hidden by default
  [{:category :digital   :name "Annual report"          :freq "yearly"    :date "Jan 1"  :deadline "Jun 15"}
   {:category :digital   :name "Tax review"             :freq "quarterly" :date "Mar 15" :deadline "Jun 19"}
   {:category :digital   :name "Bookkeeping"            :freq "monthly"   :date "Jun 12"}
   {:category :digital   :name "Finance review"         :freq "6 months"  :date "Jun 11" :deadline "Jun 13"}
   {:category :digital   :name "Bookkeeping"            :freq "monthly"   :date "Jun 14" :upcoming? true}
   {:category :digital   :name "Tax review"             :freq "quarterly" :date "Sep 15" :upcoming? true}
   {:category :household :name "Wash shower curtain"     :freq "6 months"  :date "Jan 1"}
   {:category :household :name "Vacuum sofa"             :freq "6 months"  :date "Jan 1"}
   {:category :household :name "Clean range hood filter" :freq "quarterly" :date "Mar 6"}
   {:category :household :name "Wipe down fridge"        :freq "quarterly" :date "Mar 20"}
   {:category :household :name "Wash windows"            :freq "6 months"  :date "Mar 20"}
   {:category :household :name "Clean dishwasher"        :freq "bimonthly" :date "May 1"}
   {:category :household :name "Deep clean kitchen"      :freq "bimonthly" :date "May 1"}
   {:category :household :name "Clean washing machine"   :freq "monthly"   :date "Jun 1"}
   {:category :household :name "Clean washing machine"   :freq "monthly"   :date "Jun 17" :upcoming? true}
   {:category :household :name "Clean range hood filter" :freq "quarterly" :date "Sep 6"  :upcoming? true}])

;; ── Helpers ──────────────────────────────────────────────────────────────────

(defn partition-tasks [tasks done show-completed?]
  (let [year   (.getFullYear (js/Date.))
        sorted (sort-by #(parse-month-day (:date %) year) tasks)]
    {:current  (cond->> (remove :upcoming? sorted)
                 (not show-completed?) (remove #(contains? done (:name %))))
     :upcoming (filter :upcoming? sorted)}))

(defn deadline-label [date-str]
  (when-let [d (days-until date-str)]
    (when (<= d 7)
      (due-text d))))

;; ── Components ───────────────────────────────────────────────────────────────

(defui round-checkbox [{:keys [checked? class]}]
  ($ :button
     {:aria-pressed checked?
      :class (str "shrink-0 w-4 h-4 rounded-full border-2 cursor-pointer "
                  "flex items-center justify-center "
                  (if checked?
                    "bg-done border-done"
                    "bg-transparent border-edge")
                  (when class (str " " class)))}
     (when checked?
       ($ :svg {:viewBox "0 0 10 10" :class "w-2.5 h-2.5 text-white" :fill "none"
                :stroke "currentColor" :stroke-width 2 :stroke-linecap "round" :stroke-linejoin "round"}
          ($ :path {:d "M2 5l2.5 2.5 3.5-4"})))))

(defui task-row [{:keys [task done? on-toggle]}]
  (let [{:keys [name freq date deadline]} task
        due          (when-not done? (deadline-label deadline))
        {:keys [rel full]} (date-display date)]
    ($ :button
       {:on-click #(on-toggle name)
        :class (str "group flex w-full items-center gap-3 px-4 py-2.5 rounded-lg "
                    "cursor-pointer select-none touch-manipulation text-left "
                    (if due
                      "bg-red-500/8 hover:bg-red-500/14 "
                      "hover:bg-page ")
                    (when done? "opacity-35"))}
       ($ round-checkbox {:checked? done? :class "pointer-events-none hidden group-hover:flex"})
       ($ :span {:class (str "text-[15px] font-medium leading-snug text-label "
                             (when done? "line-through"))}
          name)
       (when-not done?
         ($ :span {:class "text-[13px] font-semibold text-muted"}
            rel))
       (when due
         ($ :span {:class "text-[12px] font-semibold uppercase text-red-500"}
            due))
       ($ :div {:class "flex-1"})
       (when (or done? (not (contains? #{"Today" "Yesterday"} rel)))
         ($ :span {:class "text-[13px] font-semibold tabular-nums text-muted"}
            full))
       ($ :span {:class "rounded px-2 py-0.5 text-[11px] font-semibold uppercase tracking-wider bg-edge/40 text-heading"}
          freq))))

(defui task-list [{:keys [tasks done on-toggle class]}]
  ($ :div {:class (str "flex flex-col" (when class (str " " class)))}
     (for [t tasks]
       ($ task-row {:key (str (:name t) (:date t))
                    :task t
                    :done? (contains? done (:name t))
                    :on-toggle on-toggle}))))

(defui upcoming-divider [{:keys [on-click]}]
  ($ :div {:class "group/upcoming mx-4 my-1 flex items-center gap-3 cursor-pointer"
           :on-click on-click}
     ($ :div {:class "flex-1 border-t border-dashed border-edge"})
     ($ :span {:class "text-[11px] font-semibold uppercase tracking-[0.15em] text-heading"}
        ($ :span {:class "group-hover/upcoming:hidden"} "Upcoming")
        ($ :span {:class "hidden group-hover/upcoming:inline"} "Hide upcoming"))
     ($ :div {:class "flex-1 border-t border-dashed border-edge"})))

(defui card-header [{:keys [label show-completed? on-toggle]}]
  ($ :div {:class "flex items-center gap-3 px-4 py-3"}
     ($ :span {:class "text-[20px] font-bold uppercase tracking-[0.2em] text-heading"}
        label)
     ($ :label {:class "flex items-center gap-1.5 cursor-pointer opacity-0 group-hover/card:opacity-100 select-none"}
        ($ :input {:type "checkbox" :checked show-completed?
                   :on-change #(on-toggle (.. % -target -checked))
                   :class "accent-done w-3.5 h-3.5"})
        ($ :span {:class "text-[11px] font-medium text-muted"}
           (if show-completed? "Hide completed" "Show completed")))))

(defui upcoming-tab [{:keys [on-click]}]
  ($ :div {:class "group/tab absolute inset-x-0 bottom-0 h-8 flex items-center justify-center"}
     ($ :button
        {:on-click on-click
         :class "opacity-0 group-hover/tab:opacity-100 flex items-center gap-1.5 rounded-full border-2 border-edge bg-surface px-3 py-1 text-[11px] font-semibold uppercase tracking-[0.1em] text-heading cursor-pointer select-none"}
        "Upcoming"
        ($ :svg {:viewBox "0 0 10 10" :fill "none" :stroke "currentColor" :stroke-width 2.5
                 :stroke-linecap "round" :stroke-linejoin "round"
                 :class "w-2.5 h-2.5"}
           ($ :path {:d "M1.5 3l3.5 4 3.5-4"})))))

(defui category-card [{:keys [label cat-tasks on-toggle done]}]
  (let [[show-completed? set-completed!] (use-state false)
        [show-upcoming?  set-upcoming!]  (use-state false)
        {:keys [current upcoming]} (partition-tasks cat-tasks done show-completed?)]
    ($ :div {:class "group/card relative pb-3"}
       ($ :div {:class "rounded-2xl border-2 border-edge bg-surface p-2"}
          ($ card-header {:label label
                          :show-completed? show-completed?
                          :on-toggle set-completed!})
          (if (empty? current)
            ($ :p {:class "py-4 text-center text-[15px] font-medium italic text-muted"}
               "All clear!")
            ($ task-list {:tasks current :done done :on-toggle on-toggle}))
          (when show-upcoming?
            ($ :<>
               ($ upcoming-divider {:on-click #(set-upcoming! not)})
               ($ task-list {:tasks upcoming :done done :on-toggle on-toggle
                             :class "pb-1 opacity-50"}))))
       (when (and (seq upcoming) (not show-upcoming?))
         ($ upcoming-tab {:on-click #(set-upcoming! not)})))))

;; ── Header ──────────────────────────────────────────────────────────────────

(defn today-parts []
  (let [d (js/Date.)]
    [(.toLocaleDateString d "en-US" #js {:weekday "long"})
     (.toLocaleDateString d "en-US" #js {:month "long" :day "numeric"})]))

(defui screen-header []
  (let [[wd md] (today-parts)]
    ($ :header {:class "mb-8 flex flex-col items-center gap-1.5 text-center"}
       ($ :div {:class "pwa:hidden text-[34px] font-extrabold uppercase leading-none tracking-[0.28em] pl-[0.28em] text-muted text-inset"}
          "Orbit")
       ($ :div {:class "text-[19px] font-medium tracking-wide text-muted"}
          (str wd " · " md)))))

;; ── App ─────────────────────────────────────────────────────────────────────

(defui app []
  (let [[done set-done!] (use-state #{})
        toggle (fn [name]
                 (set-done! #(if (contains? % name) (disj % name) (conj % name))))
        by-category (group-by :category tasks)]
    ($ :div {:class "px-7 pt-12 pb-16 mx-auto w-full max-w-2xl"}
       ($ screen-header)
       ($ :div {:class "flex flex-col gap-4"}
          (for [cat category-order
                :let [rows (by-category cat)]
                :when (seq rows)]
            ($ category-card {:key (str cat)
                              :label (category-labels cat)
                              :cat-tasks rows
                              :done done
                              :on-toggle toggle}))))))

(defonce root
  (uix.dom/create-root (js/document.getElementById "app")))

(defn ^:export init []
  (uix.dom/render-root ($ app) root))
