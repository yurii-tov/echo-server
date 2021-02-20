(ns echo-server.core
  (:gen-class)
  (:require [org.httpkit.server :as server]
            [clojure.data.json :as json]
            [clojure.string :as cstr]
            [clojure.java.io :as io])
  (:import java.time.LocalDateTime
           java.time.format.DateTimeFormatter))


(defn format-message [[timestamp message-raw]]
  (let [message-parsed (or (try (json/write-str (json/read-str message-raw)
                                                :escape-unicode nil)
                                (catch Exception e))
                           message-raw)]
    (format "<tr><td class='timestamp'><pre>%s</pre></td><td><pre class='json'>%s</pre></td></tr>"
            timestamp
            message-parsed)))


(defn format-received-data [payload]
  (let [messages (if payload
                   (format "<table>
                            <tr><th>Timestamp</th><th>Message</th></tr>
                            %s
                            </table>"
                           (cstr/join "\n" (map format-message payload)))
                   "Nothing received yet...")
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
    (do (when body (swap! received
                          (fn [r] (cons (list (.format (LocalDateTime/now) (DateTimeFormatter/ISO_DATE_TIME))
                                              (slurp body))
                                        r))))
        {:status 200
         :headers {"Content-Type" "text/html"}})))


(defn reset-server [] (reset! received nil))


(defn start-server
  ([settings]
   (printf "Echo server running at %s\n"
           settings)
   (def stop-server (server/run-server app settings)))
  ([] (start-server {:ip "0.0.0.0" :port 8889})))
