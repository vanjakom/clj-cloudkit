# clj-cloudkit

## Setup

### create public key:
```
openssl ecparam -name prime256v1 -genkey -noout -out eckey.pem
```

### output key in format needed by client and used in apple documentation:
```
openssl ec -in key.pem -noout -text
```
( Concatenate private key by removing colons )

key should be reported to Server-to-Server Keys in CloudKit dashboard.

## Usage

```
(require '[clj-cloudkit.client :as cloudkit])
(require '[clj-cloudkit.filter :as cloudkit-filter])
(require '[clj-cloudkit.sort :as cloudkit-sort])

(def cloudkit-client (cloudkit/auth-server-to-server
                       (cloudkit/create-client "<container>")
                       "<private-key-hex>"
                       "<key-id>"))

(cloudkit/records-query
  cloudkit-client
  "<data type>"
  nil
  (list (cloudkit-sort/descending "<field>")))

```

### to setup production client:
```
(def cloudkit-client-prod
  (cloudkit/auth-server-to-server
    (assoc
      (cloudkit/create-client "<container>")
      :environment
      "production")
    "<private-key-hex>"
    "<key-id>"))
```
Note: each env ( development / production ) requires different key

## useful links

https://vanjakom.wordpress.com/2016/08/01/cloudkit-server-to-server-request-in-jvm-environment/


## License

Copyright © 2016 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
