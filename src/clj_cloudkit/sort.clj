(ns clj-cloudkit.sort)

(defn ascending [field-name]
  {
    "fieldName" field-name
    "ascending" true})

(defn ascending-system [system-field-name]
  {
    "systemFieldName" system-field-name
    "ascending" true})

(defn descending [field-name]
  {
    "fieldName" field-name
    "ascending" false})

(defn descending-system [system-field-name]
  {
    "systemFieldName" system-field-name
    "ascending" false})

(defn ascending-location [field-name location]
  {
    "fieldName" field-name
    "ascending" true
    "realtiveLocation" {
                         "value" location
                         "type" "Location"}})

(defn descending-location [field-name location]
  {
    "fieldName" field-name
    "ascending" false
    "realtiveLocation" {
                         "value" location
                         "type" "Location"}})
