(defproject clj-cloudkit "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :repl-options {
                  :nrepl-middleware
                  [lighttable.nrepl.handler/lighttable-ops]}
  :dependencies [
                  [org.clojure/clojure "1.8.0"]
                  [org.clojure/data.json "0.2.6"]
                  [clj-http "2.2.0"]
                  [lein-light-nrepl "0.3.2"]])
