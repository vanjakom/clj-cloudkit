(defproject
  com.mungolab/clj-cloudkit
  "0.1.0"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :deploy-repositories [
                        ["clojars" {
                                    :url "https://clojars.org/repo"
                                    :sign-releases false}]]  
  :dependencies [
                 [com.mungolab/clj-common "0.3.2"]
                 
                 [org.clojure/data.json "0.2.6"]
                 [clj-http "2.2.0"]])
