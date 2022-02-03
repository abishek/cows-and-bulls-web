(ns ^:figwheel-hooks rohabini.cows-and-bulls
  (:require
   [clojure.set]
   [goog.dom :as gdom]
   [goog.dom.classlist :as gc]
   [goog.events :as gevents]))

(defn zeropadded [n]
  (cond
    (< n 10) (str "000" n)
    (< n 100) (str "00" n)
    (< n 1000) (str "0" n)
    :else (str n)))

(def number (atom '()))

(defn digits [n]
  (map int (zeropadded n)))

(defn start []
  (take 4 (shuffle '(1 2 3 4 5 6 7 8 9))))

(defn count-bulls [source target]
  (reduce + (map
             #(get {false 0 nil 0 true 1} %)
             (map = source target))))

(defn count-cows [source target]
  (count (clojure.set/intersection (set source) (set target))))

(defn cows-and-bulls [pred]
  (let [cows (count-cows @number pred)
        bulls (count-bulls @number pred)]
    {:cows (- cows bulls) :bulls bulls}))

(defn toggle-hidden [id]
  (gc/toggle (gdom/getElement id) "hidden"))

(defn toggle-hidden-multi [ids]
  (doall (map toggle-hidden ids)))

(defn clear-table
  "Clear all entries in the table."
  []
  (let [tbody-el (gdom/getElement "score-board-rows")]
    (gdom/removeChildren tbody-el)
    true))

(defn start-game [success]
  (swap! number start)
  (if success
    (and (clear-table)
         (toggle-hidden-multi ["start" "play" "success-gif"]))
    (toggle-hidden-multi ["start" "play"])))

(defn create-column
  "creates a td element with the value to be displayed."
  [num]
  (let [td-el (gdom/createElement "td")]
    (gdom/setProperties td-el #js {"class" "px-6 py-4 whitespace-nowrap text-sm text-gray-500"})
    (gdom/setTextContent td-el num)
    td-el))

(defn create-table-entry
  "creates a tr element with the values required for display."
  [entry cows bulls]
  (let [tr-el (gdom/createElement "tr")]
    (gdom/setProperties tr-el #js {"class" "bg-white"})
    (gdom/appendChild tr-el (create-column entry))
    (gdom/appendChild tr-el (create-column cows))
    (gdom/appendChild tr-el (create-column bulls))
    tr-el))

(defn append-score-row
  "create a row from the scores and append to the table."
  [entry cows bulls]
  (let [tbody-el (gdom/getElement "score-board-rows")]
    (gdom/appendChild tbody-el (create-table-entry entry cows bulls))))

(defn fetch-guess-vector
  "collect the inputs from the user and construct a guess vector"
  []
  (map int (map #(.-value %) (map gdom/getElement ["d01" "d02" "d03" "d04"]))))

(defn clear-input
  "clear the text input and set its value to nil."
  [id]
  (set! (.-value (gdom/getElement id)) nil))

(defn clear-user-inputs
  "clear all the inputs from the textboxes."
  []
  (doall (map clear-input ["d01" "d02" "d03" "d04"])))

(defn try-number
  "the hook for the button click."
  []
  (let [guess (fetch-guess-vector)
        cab (cows-and-bulls guess)]
    ;; (println @number)
    (clear-user-inputs)
    (if (= 4 (:bulls cab))
      (start-game true)
      (append-score-row (reduce str guess) (:cows cab) (:bulls cab)))))

(defn setup
  "sets up on-click functions for the elements."
  []
  (let [start-btn (gdom/getElement "start-btn")
        guess-btn (gdom/getElement "guess-btn")]
    (gevents/listen start-btn "click" (partial start-game false))
    (gevents/listen guess-btn "click" try-number)))

(setup)

;; specify reload hook with ^:after-load metadata
(defn ^:after-load on-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
)
