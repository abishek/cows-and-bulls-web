(ns rohabini.cows-and-bulls-test
    (:require
     [cljs.test :refer-macros [deftest is testing]]
     [rohabini.cows-and-bulls :refer [digits num-matches num-presents]]))

(deftest test-digits-gen
  (is (= [1 2 3 4] (digits 1234))))

(deftest test-count-equals-01
  (is (= 4 (num-matches [1 2 3 4] [1 2 3 4]))))

(deftest test-count-equals-02
  (is (= 0 (num-matches [1 2 3 4] [5 6 7 8]))))

(deftest test-count-equals-03
  (is (= 2 (num-matches [1 2 5 6] [1 2 34]))))

(deftest test-count-equals-04
  (is (= 3 (num-matches [4 1 5 2] [1 2 3 4]))))
