(ns clj-cloudkit.model)

; maybe useful for finding this proxy-mappings
; https://developer.apple.com/library/content/documentation/DataManagement/Conceptual/CloudKitWebServicesReference/QueryingRecords/QueryingRecords.html#//apple_ref/doc/uid/TP40015240-CH5-SW8

; in newest release of documentation systemFieldName is introduced, implemented for sort
; naming could be found here
; https://developer.apple.com/library/content/documentation/DataManagement/Conceptual/CloudKitWebServicesReference/QueryingRecords.html#//apple_ref/doc/uid/TP40015240-CH5-SW4

; recordName
; share
; parent
; createdUserRecordName
; createdTimestamp
; modifiedTimestamp
; modifiedUserRecordName

;(def ^:const creation-date "creationDate")
;(def ^:const created-by "creatorUserRecordID")

(def ^:const created-timestamp "createdTimestamp")

(def ^:const maximum-number-of-operations-request 200)
(def ^:const maximum-number-of-records-response 200)


(defn create-record-meta [record-name]
  {
    :recordName record-name})


; timestamp is in milliseconds

(defn create-cl-location [longitude latitude]
  {
    :longitude longitude
    :latitude latitude})
