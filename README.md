# clj-cloudkit

## Usage

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


## useful links

https://vanjakom.wordpress.com/2016/08/01/cloudkit-server-to-server-request-in-jvm-environment/


## License

Copyright © 2016 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
