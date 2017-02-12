(ns clj-cloudkit.model)

(defn create-record-meta [record-name]
  {
    :recordName record-name})


(defn create-cl-location [longitude latitude]
  {
    :longitude longitude
    :latitude latitude})
