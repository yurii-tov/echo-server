(ns echo-server.core
  (:gen-class)
  (:require [org.httpkit.server :as server]
            [clojure.data.json :as json]))


(defn format-received-data [payload]
  (let [data (if-let [parsed-json (try (json/read-str payload)
                                       (catch Exception e))]
               (with-out-str (json/pprint parsed-json
                                          :escape-unicode nil))
               payload)]
    (format "<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"></head><body><pre>%s</pre></body></html>\n"
            data)))


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


(defn start-server []
  (def stop-server (server/run-server app {:port 8889})))
