(ns clj-cloudkit.recipe.exchange)

(require '[clojure.data.json :as json])

(require '[clj-cloudkit.client :as cloudkit])
(require '[clj-cloudkit.filter :as cloudkit-filter])
(require '[clj-cloudkit.sort :as cloudkit-sort])
(require '[clj-cloudkit.operation :as cloudkit-operation])

(def *exchange-record-type* "Exchange")

(defn pop [client type]
  (let [records (cloudkit/records-query
                  client
                  *exchange-record-type*
                  (list
                    (cloudkit-filter/equals :marked 0)
                    (cloudkit-filter/equals :type type))
                  (list
                    (cloudkit-sort/descending :timestamp)))]
    (if-let [record (first records)]
      (let [data-url (:downloadURL (:data record))]
        (if-let [data (cloudkit/assets-download
                        client
                        data-url)]
          (do
            (cloudkit/records-modify
              client
              (list
                (cloudkit-operation/update
                  (assoc (select-keys record [:marked]) :marked 1)
                  *exchange-record-type*)))
            data))))))

(defn pop-json [client type]
  (if-let [data (pop client type)]
    (json/read-str data :key-fn keyword)))
