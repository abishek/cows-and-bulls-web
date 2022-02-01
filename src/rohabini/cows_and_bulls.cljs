(ns ^:figwheel-hooks rohabini.cows-and-bulls
  (:require
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

(defn has-digit? [coll digit]
  (some #(= digit %) coll))

(defn num-matches [numvec predvec]
  (reduce + (map
             #(get {false 0 true 1} %)
             (map = numvec predvec))))

(defn num-presents [numvec predvec]
  (let [numvec-has-digit (partial has-digit? numvec)]
    (reduce + (map
               #(get {nil 0 true 1} %)
               (map numvec-has-digit predvec)))))

(defn cows-and-bulls [pred]
  (let [cows (num-presents @number pred)
        bulls (num-matches @number pred)]
    {:cows (- cows bulls) :bulls bulls}))

(defn toggle-hidden [id]
  (gc/toggle (gdom/getElement id) "hidden"))

(defn toggle-hidden-multi [ids]
  (println (map toggle-hidden ids)))

(defn start-game [success]
  (swap! number start)
  (if success
    (toggle-hidden-multi ["start" "play" "success-gif"])
    (toggle-hidden-multi ["start" "play"])))

(defn try-number []
  (let [d01 (int (.-value (gdom/getElement "d01")))
        d02 (int (.-value (gdom/getElement "d02")))
        d03 (int (.-value (gdom/getElement "d03")))
        d04 (int (.-value (gdom/getElement "d04")))
        tbody-el (gdom/getElement "score-board-rows")
        tr-el (gdom/createElement "tr")
        td-guess-el (gdom/createElement "td")
        td-cows-el (gdom/createElement "td")
        td-bulls-el (gdom/createElement "td")
        guess [d01 d02 d03 d04]
        cab (cows-and-bulls guess)]
    (println @number)
    (when (= 4 (:bulls cab))
      (start-game true))

    (gdom/setProperties tr-el #js {"class" "bg-white"})
    (gdom/setTextContent td-guess-el (reduce str guess))
    (gdom/setProperties td-guess-el #js {"class" "px-6 py-4 whitespace-nowrap text-sm text-gray-500"})
    (gdom/setTextContent td-cows-el (:cows cab))
    (gdom/setProperties td-cows-el #js {"class" "px-6 py-4 whitespace-nowrap text-sm text-gray-500"})
    (gdom/setTextContent td-bulls-el (:bulls cab))
    (gdom/setProperties td-bulls-el #js {"class" "px-6 py-4 whitespace-nowrap text-sm text-gray-500"})
    (gdom/appendChild tr-el td-guess-el)
    (gdom/appendChild tr-el td-cows-el)
    (gdom/appendChild tr-el td-bulls-el)
    (gdom/appendChild tbody-el tr-el)
    (set! (.-value (gdom/getElement "d01")) nil)
    (set! (.-value (gdom/getElement "d02")) nil)
    (set! (.-value (gdom/getElement "d03")) nil)
    (set! (.-value (gdom/getElement "d04")) nil)
    ))

(defn setup []
  (let [start-btn (gdom/getElement "start-btn")
        guess-btn (gdom/getElement "guess-btn")]
    (gevents/listen start-btn "click" (partial start-game false))
    (gevents/listen guess-btn "click" try-number)))

(setup)
;;(display-number (start) (get-start-element))

;; specify reload hook with ^:after-load metadata
(defn ^:after-load on-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
)
