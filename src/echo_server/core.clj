(ns echo-server.core
  (:gen-class)
  (:require [org.httpkit.server :as server]
            [clojure.data.json :as json]
            [clojure.string :as cstr]
            [clojure.java.io :as io])
  (:import java.time.LocalDateTime
           java.time.format.DateTimeFormatter))


(defn format-message [m]
  (let [parsed-json (try (json/read-str m)
                         (catch Exception e))
        data (if parsed-json
               (format "<tr><td class='timestamp'><pre>%s</pre></td><td class='json'><pre>%s</pre></td></tr>"
                       (.format (LocalDateTime/now) (DateTimeFormatter/ISO_DATE_TIME))
                       (json/write-str parsed-json
                                       :escape-unicode nil))
               m)]
    data))


(defn format-received-data [payload]
  (let [messages (cstr/join "\n" (map format-message payload))
        template (slurp (clojure.java.io/resource "index.html"))]
    (format template messages)))


(def received (atom nil))


(defn app [{:keys [uri body]}]
  (case uri
    "/echo"
    {:status 200
     :headers {"Content-Type" "text/html"}
     :body (format-received-data
            (deref received))}
    (do (when body (swap! received (fn [r] (cons (slurp body) r))))
        {:status 200
         :headers {"Content-Type" "text/html"}})))


(defn reset-server [] (reset! received nil))


(defn start-server
  ([settings]
   (printf "Echo server running at %s\n"
           settings)
   (def stop-server (server/run-server app settings)))
  ([] (start-server {:ip "0.0.0.0" :port 8889})))
