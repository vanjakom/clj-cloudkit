(ns clj-cloudkit.utils)

(require '[clj-cloudkit.client :as cloudkit])
(require '[clj-cloudkit.operation :as operation])

(defn drop-table
  "Removes all entries from given record type"
  [client record-type]
  (let [records-seq (cloudkit/records-query-all
                      client
                      record-type
                      nil
                      nil)]
    (cloudkit/records-modify
      client
      (map
        operation/force-delete
        records-seq))))

(defn count-table
  "Counts all entries for given record-type"
  [client record-type]
  (let [records-seq (cloudkit/records-query-all
                      client
                      record-type
                      nil
                      nil)]
    (count records-seq)))
