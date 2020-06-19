(ns echo-server.core
  (:gen-class)
  (:require [org.httpkit.server :as server]
            [clojure.data.json :as json]
            [clojure.java.io :as io]))


(defn format-received-data [payload]
  (let [parsed-json (try (json/read-str payload)
                         (catch Exception e))
        data (if parsed-json
               (format "<pre id='json'>%s</pre>"
                       (json/write-str parsed-json
                                       :escape-unicode nil))
               payload)
        template (slurp (clojure.java.io/resource "index.html"))]
    (format template data)))


(let [received (atom "nothing received yet")]
  (defn app [{:keys [uri body]}]
    (case uri
      "/echo"
      {:status 200
       :headers {"Content-Type" "text/html"}
       :body (format-received-data
              (deref received))}
      (do (when body (reset! received (slurp body)))
          {:status 200
           :headers {"Content-Type" "text/html"}}))))


(defn start-server
  ([settings]
   (printf "Echo server running at %s\n"
           settings)
   (def stop-server (server/run-server app settings)))
  ([] (start-server {:ip "0.0.0.0" :port 8889})))
