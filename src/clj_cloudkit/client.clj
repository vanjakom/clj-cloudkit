(ns clj-cloudkit.client)

(require '[clojure.data.json :as json])
(require '[clj-http.client :as http])

(declare create-default-configuration)
(declare execute-request)
(declare create-signature-fn)
(declare serialize-body)
(declare extract-record)
(declare extract-records)
(declare serialize-record)

; ### problems:
; join / when path params, auth-api-token
; repetition of body-str calculation
; execute-request uses insecure-true

; ### notes :
; # auth function is responsable for adding of :body-str, after that point body should
;   not be changed
;

(defn create-client
  "Creates client which should be used as arg to all api calls"
  [container]
  (let [configuration (create-default-configuration)]
    (assoc
      configuration
      :container
      container)))

(defn auth-server-to-server
  "Adds layer of server-to-server auth on top of client, returning new client"
  [client private-key-hex key-id]
  (let [simple-date-formatter (new java.text.SimpleDateFormat "yyyy-MM-dd'T'HH:mm:ss'Z'")
        timezone (java.util.TimeZone/getTimeZone "UTC")
        base64-encoder (java.util.Base64/getEncoder)
        sha256-digest (java.security.MessageDigest/getInstance "SHA-256")
        signature-fn (create-signature-fn private-key-hex)]
    (.setTimeZone simple-date-formatter timezone)
    (assoc
      client
      :auth-fn
      (fn [request]
        (let [headers (:headers request)
              path (:path-params request)
              body (:body request)
              body-str (serialize-body body)
              body-sha256 (.digest sha256-digest (.getBytes body-str))
              body-sha256-base64 (.encodeToString base64-encoder body-sha256)
              request-date-iso (.format simple-date-formatter (new java.util.Date))
              subpath (str "/" (clojure.string/join
                                 "/"
                                 (map
                                   (fn [param]
                                     (if (keyword? param)
                                       (param request)
                                       param))
                                   (rest path))))
              sign-request (str request-date-iso ":" body-sha256-base64 ":" subpath)
              signature (signature-fn sign-request)]
          (println "subpath: " subpath)
          (assoc
            request
            :headers
            (assoc
              headers
              "X-Apple-CloudKit-Request-KeyID"
              key-id
              "X-Apple-CloudKit-Request-ISO8601Date"
              request-date-iso
              "X-Apple-CloudKit-Request-SignatureV1"
              signature)
            :body-str
            body-str))))))

(defn auth-api-token
  "Adds layer of api token auth on top of client, returning new client"
  [client api-token]
  (assoc
    client
    :auth-fn
    (fn [request]
      (let [query-params (get request :query-params {})
            body (:body request)
            body-str (serialize-body body)]
      (assoc
        request
        :query-params
        (assoc
          query-params
          "ckAPIToken"
          api-token)
        :body-str
        body-str)))))

(defn records-query
  "Fetching Records Using a Query (records/query)"
  [client record-type filter-seq sort-seq]
  (let [response (execute-request
                   (assoc
                     client
                     :path-params
                     (list :server-uri "database" :version :container
                           :environment :database "records" "query")
                     :body
                     {
                       "zoneWide" false
                       "query" {
                                 "recordType" record-type
                                 "filterBy" (if (nil? filter-seq) '() filter-seq)
                                 "sortBy" (if (nil? sort-seq) '() sort-seq)}}))]
    (extract-records (:records response))))

; it seems that records-lookup will return entry with recordName for records that don't
; exist on server
(defn records-lookup
  "Fetching Records by Record Name (records/lookup)"
  [client record-name-seq]
  (let [response (execute-request
                   (assoc
                     client
                     :path-params
                     (list :server-uri "database" :version :container
                           :environment :database "records" "lookup")
                     :body
                     {
                       "records" (map
                                   (fn [record-name] {"recordName" record-name})
                                   record-name-seq)}))]
    (extract-records (:records response))))

(defn records-modify
  "Apply multiple types of operations—such as creating, updating, replacing,
  and deleting records— to different records in a single request."
  [client operations-seq]
  (let [response (execute-request
                   (assoc
                     client
                     :path-params
                     (list :server-uri "database" :version :container
                           :environment :database "records" "modify")
                     :body
                     {
                       "operations" (map
                                      (fn [operation]
                                        (assoc
                                          operation
                                          :record
                                          (assoc
                                            (serialize-record (:record operation))
                                            "recordType"
                                            (:recordType operation))))
                                      operations-seq)}))]
    (extract-records (:records response))))

(defn assets-upload
  "Request tokens for asset fields in new or existing records used in another request to upload asset data.
  Note: currently supports only single asset-upload"
  [client record-name record-type record-field asset-input-stream]
  (let [response-token (first
                         (execute-request
                           (assoc
                             client
                             :path-params
                             (list :server-uri "database" :version :container
                                   :environment :database "assets" "upload")
                             :body
                             {
                               "tokens" (list {
                                                "recordName" record-name
                                                "recordType" record-type
                                                "fieldName" record-field})})))
        upload-url (:url response-token)]
    (let [upload-response (clj-http.client/post
                            upload-url
                            {
                              :content asset-input-stream})]
      (if (not (= (:status upload-response 200)))
        (throw (ex-info "Unable to upload asset" upload-response))
        (:body upload-response)))))


(defn assets-downlaod
  "Retrieves asset from CloudKit url given in record"
  [client asset-url]
  (let [response (http/get asset-url)]
    (if (= (:status response) 200)
      (:body response)
      (throw (ex-info "Status not 200" response)))))

(defn users-caller
  "Fetching Current User Identity (users/caller)"
  [client]
  (let [response (execute-request
                   (assoc
                     client
                     :path-params
                     (list :server-uri "database" :version :container
                           :environment "public" "users" "caller")))]
    response))

(defn- create-default-configuration []
  {
    :server-uri "https://api.apple-cloudkit.com"
    :version "1"
    :environment "development"
    :database "public"
    :auth-fn (fn [request]
               (let [body (:body request)]
                 (assoc
                   request
                   :body-str
                   (if (empty? body)
                     ""
                     (json/write-str body)))))})

(defn- execute-request [request]
  (let [processor-chain (filter
                          #(not (nil? %))
                          (list (:auth-fn request)))
        processor-chain-fn (apply comp processor-chain)
        final-request (processor-chain-fn request)
        headers (:headers final-request)
        path (:path-params final-request)
        query-params (:query-params final-request)
        path-str (clojure.string/join
                   "/"
                   (map
                     (fn [param]
                       (if (keyword? param)
                         (param final-request)
                         param))
                     path))
        body (:body final-request)
        body-str (:body-str final-request)]
    (println "Path: " path-str)
    (println "headers:" headers)
    (println "Body:" body-str)
    (let [response (if
                     (not (nil? body))
                     (http/post
                       path-str
                       {
                         :insecure? true
                         :headers headers
                         :body body-str
                         :query-params query-params})
                     (http/get
                       path-str
                       {
                         :insecure? true
                         :headers headers
                         :query-params query-params}))]
      (if (= (:status response) 200)
        (json/read-str (:body response) :key-fn keyword)
        (throw (ex-info "Status not 200" response))))))

(defn- create-signature-fn [private-key-hex]
  (let [keypair-generator (java.security.KeyPairGenerator/getInstance "EC")
        prime256v1-gen-param-spec (new java.security.spec.ECGenParameterSpec "secp256r1")]
    (.initialize keypair-generator prime256v1-gen-param-spec)
    (let [ec-params (.getParams (.getPrivate (.generateKeyPair keypair-generator)))
          private-key-bigint (new java.math.BigInteger private-key-hex 16)
          private-key-specs (new java.security.spec.ECPrivateKeySpec private-key-bigint ec-params)
          key-factory (java.security.KeyFactory/getInstance "EC")
          private-key (.generatePrivate key-factory private-key-specs)
          signature (java.security.Signature/getInstance "SHA256withECDSA")
          base64-encoder (java.util.Base64/getEncoder)]
      (.initSign signature private-key)
      (fn [body]
        (.update signature (.getBytes body))
        (.encodeToString base64-encoder (.sign signature))))))

(defn- serialize-body [body]
  (if
    (empty? body)
    ""
    (json/write-str body)))

(defn- extract-record [original-record meta-map]
  (reduce
    (fn [state [key value]]
      (with-meta
        (assoc
          state
          key
          (:value value))
        (assoc
          (meta state)
          :fields
          (assoc
            (get (meta state) :fields {})
            key
            (:type value)))))
    (with-meta {} meta-map)
    original-record))

(defn- extract-records [original-records-seq]
  (map
    (fn [original-record]
      (assoc
        (extract-record
          (:fields original-record)
          (select-keys
            original-record
            [:recordType :recordChangeTag :created :modified]))
        :_id
        (:recordName original-record)))
    original-records-seq))

(defn- serialize-record [record]
  (let [cloudkit-record {
                          :fields (reduce
                                    (fn [state [key value]]
                                      (assoc
                                        state
                                        key
                                        {:value value}))
                                    {}
                                    (dissoc record :_id))}]
    (if-let [record-name (:_id record)]
      (assoc cloudkit-record "recordName" record-name)
      cloudkit-record)))
