(ns webdev.core
  (require [ring.adapter.jetty :as jetty]
               [ring.middleware.reload :refer [wrap-reload]]
               [compojure.core :refer [defroutes GET]]
               [compojure.route :refer [not-found]]
               [ring.handler.dump :refer [handle-dump]]))

(defn greet [req]
  {:status 200
   :body "Hello, World!!!!!!!!!!!!!"
   :headers {}})

(defn about [req]
  {:status 200
   :body "This is the about page"
   :headers {}})

(defn goodbye [req]
  {:status 200
   :body "Goodbye, cruel World!"
   :headers {}})

(defn say-my-name [req]
  {:status 200
   :body (str "Yo, " (get-in req [:route-params :name]))
   :headers {}})

(def ops
  {"+" +
   "-" -
   "*" *
   ":" /})

(defn calc [req]
  (let [a  (Integer. (get-in req [:route-params :a]))
        b  (Integer. (get-in req [:route-params :b]))
        op (get-in req [:route-params :op])
        f  (get ops op)]
    (if f
      {:status 200
       :body (str (f a b))
       :headers {}}
      {:status 404
       :body "Unkown operator !!!"
       :headers {}})))


(defroutes app
  (GET "/" [] greet)
  (GET "/yo/:name" [] say-my-name)
  (GET "/calc/:a/:op/:b" [] calc)
  (GET "/about" [] about)
  (GET "/request" [] handle-dump)
  (GET "/goodbye" [] goodbye)
  (not-found "Page not found."))

(defn -main [& arg]
  (jetty/run-jetty #'app                     {:port (Integer/parseInt (System/getenv "PORT"))}))

(defn -dev-main [port]
  (jetty/run-jetty (wrap-reload #'app)       {:port (Integer. port)}))
