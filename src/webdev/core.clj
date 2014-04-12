(ns webdev.core
  (require [ring.adapter.jetty :as jetty]
               [ring.middleware.reload :refer [wrap-reload]]
               [compojure.core :refer [defroutes GET]]
               [compojure.route :refer [not-found]]))

(defn greet [req]
  {:status 200
   :body "Hello, World!!!!!!!!!!!!!"
   :headers {}})

(defn goodbye [req]
  {:status 200
   :body "Goodbye, cruel World!"
   :headers {}})


(defroutes app
  (GET "/" [] greet)
  (GET "/goodbye" [] goodbye)
  (not-found "Page not found."))

(defn -main [& arg]
  (jetty/run-jetty #'app                             {:port (Integer/parseInt (System/getenv "PORT"))}))

(defn -dev-main [port]
  (jetty/run-jetty (wrap-reload #'app)       {:port (Integer. port)}))
