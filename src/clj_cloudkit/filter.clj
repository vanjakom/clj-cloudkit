(ns clj-cloudkit.filter)

(defn equals [field value]
  {
    "comparator" "EQUALS"
    "fieldName" field
    "fieldValue" {
                   "value" value }})

; tried with generic solution but problem is with complex
; value that is set as map, but I don't know key
; https://developer.apple.com/library/content/documentation/DataManagement/Conceptual/CloudKitWebServicesReference/QueryingRecords.html#//apple_ref/doc/uid/TP40015240-CH5-SW4

(defn record-name-equals [value]
  {
    "comparator" "EQUALS"
    "systemFieldName" "recordName"
    "fieldValue" {
                   "value" {
                             "recordName" value }}})

(defn greater-or-equals [field value]
  {
    "comparator" "GREATER_THAN_OR_EQUALS"
    "fieldName" field
    "fieldValue" {
                   "value" value}})

(defn greater [field value]
  {
    "comparator" "GREATER_THAN"
    "fieldName" field
    "fieldValue" {
                   "value" value}})

(defn near [field location distance]
  {
    "comparator" "NEAR"
    "fieldName" field
    "fieldValue" {
                   "value" location}
    "distance" distance})

(defn list-contains [field value]
  {
    "comparator" "LIST_CONTAINS"
    "fieldName" field
    "fieldValue" {
                   "value" value}})

(defn list-contains-all [field values]
  {
    "comparator" "LIST_CONTAINS_ALL"
    "fieldName" field
    "fieldValue" {
                   "value" values}})

; found this one on
; https://developer.apple.com/reference/cloudkitjs/cloudkit.queryfiltercomparator
(defn list-contains-any [field values]
  {
    "comparator" "LIST_CONTAINS_ANY"
    "fieldName" field
    "fieldValue" {
                   "value" values}})
