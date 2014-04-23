(ns webdev.core
  (:require [webdev.item.model :as items])
  (:require [webdev.item.handler :refer [handle-index-items
                                         handle-create-item
                                         handle-delete-item
                                         handle-update-item]])
  (:require
            [ring.adapter.jetty :as jetty]
            [ring.middleware.reload :refer [wrap-reload]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.resource :refer [wrap-resource]]
            [ring.middleware.file-info :refer [wrap-file-info]]
            [compojure.core :refer [defroutes ANY GET POST PUT DELETE]]
            [compojure.route :refer [not-found]]
            [ring.handler.dump :refer [handle-dump]]))

(def db (or
         (System/getenv "DATABASE_URL")
         "jdbc:postgresql://localhost/webdev"))

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
       :body "Unknown operator !!!"
       :headers {}})))

(defroutes routes
  (GET "/" [] greet)
  (GET "/yo/:name" [] say-my-name)
  (GET "/calc/:a/:op/:b" [] calc)
  (GET "/goodbye" [] goodbye)

  (GET "/about" [] about)
  (ANY "/request" [] handle-dump)

  (GET "/items" [] handle-index-items)
  (POST "/items" [] handle-create-item)
  (DELETE "/items/:item-id" [] handle-delete-item)
  (PUT "/items/:item-id" [] handle-update-item)

  (not-found "Page not found."))


(defn wrap-db [hndlr]
  (fn [req]
    (hndlr (assoc req :webdev/db db))))

(defn wrap-server-header [hndlr]
  (fn [req]
    (hndlr (assoc req :webdev/db db))))

(defn wrap-server-response [hndlr]
  (fn [req]
    (let [response (hndlr req)]
      (assoc-in response [:headers "Server:"] "my-server"))))


(def sim-methods {"PUT"     :put
                  "DELETE"  :delete})

(defn wrap-simulated-methods [hndlr]
  (fn [req]
    (if-let [method (and (= :post (:request-method req))
                         (sim-methods (get-in req [:params "_method"])))]
      (hndlr (assoc req :request-method method))
      (hndlr req))))

(def app
  (wrap-file-info
   (wrap-resource
    (wrap-server-response
     (wrap-db
      (wrap-params
       (wrap-simulated-methods routes)))) "static")))

(defn -main [& [port]]

  ;; passes request map when using: lein ring server (with or without port number)
  ;; passes port number when using: len run (with port number)

  (println (str "Port:" port))
  (items/create-table db)
  ;;(jetty/run-jetty #'app                     {:port (if port (Integer/parseInt port) (Integer/parseInt (System/getenv "PORT")))}))
  (jetty/run-jetty #'app                       {:port (Integer/parseInt port)}))
  ;;(jetty/run-jetty #'app                     {:port (Integer/parseInt (:server-port options))})



;;(defn -dev-main [& [req]]
;;  (println (str "Port:" (:server-port req)))
;;  (items/create-table db)
;;  (jetty/run-jetty (wrap-reload #'app)       {:port (Integer. (:server-port req))}))
