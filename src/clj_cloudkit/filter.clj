(ns clj-cloudkit.filter)

(defn equals [field value]
  {
    "comparator" "EQUALS"
    "fieldName" field
    "fieldValue" {
                   "value" value }})

(defn near [field location distance]
  {
    "comparator" "NEAR"
    "fieldName" field
    "fieldValue" {
                   "value" location}
    "distance" distance})

(defn list-contains-all [field values]
  {
    "comparator" "LIST_CONTAINS_ALL"
    "fieldName" field
    "fieldValue" {
                   "value" values}})
