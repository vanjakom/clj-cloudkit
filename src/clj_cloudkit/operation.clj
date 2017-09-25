(ns clj-cloudkit.operation)

(use 'clj-cloudkit.model)

(defn create [record]
  {
    :operationType "create"
    :recordType (record-type record)
    :record record})

(defn update [record]
  {
    :operationType "update"
    :recordType (record-type record)
    :record record})

(defn force-update [record]
  {
    :operationType "forceUpdate"
    :recordType (record-type record)
    :record record})

(defn force-replace [record]
  {
    :operationType "forceReplace"
    :recordType (record-type record)
    :record record})

(defn delete [record]
  {
    :operationType "delete"
    :record record})

(defn force-delete [record]
  {
    :operationType "forceDelete"
    :record record})
