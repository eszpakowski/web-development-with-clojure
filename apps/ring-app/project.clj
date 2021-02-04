(defproject ring-app "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [ring "1.7.1"]
                 [metosin/ring-http-response "0.9.1"]
                 [metosin/muuntaja "0.6.4"]
                 [metosin/reitit "0.3.9"]]
  :repl-options {:init-ns ring-app.core}
  :main ring-app.core)
