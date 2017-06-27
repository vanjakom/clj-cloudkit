(ns clj-cloudkit.operation)

(defn create [record record-type]
  {
    :operationType "create"
    :recordType record-type
    :record record})

(defn update [record record-type]
  {
    :operationType "update"
    :recordType record-type
    :record record})

(defn force-update [record record-type]
  {
    :operationType "forceUpdate"
    :recordType record-type
    :record record})

(defn force-replace [record record-type]
  {
    :operationType "forceReplace"
    :recordType record-type
    :record record})

(defn delete [record]
  {
    :operationType "delete"
    :record record})

(defn force-delete [record]
  {
    :operationType "forceDelete"
    :record record})
