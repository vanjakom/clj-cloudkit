(ns clj-cloudkit.model)

(def creation-date "creationDate")
(def created-by "creatorUserRecordID")

(defn create-record-meta [record-name]
  {
    :recordName record-name})


(defn create-cl-location [longitude latitude]
  {
    :longitude longitude
    :latitude latitude})
