(ns webdev.core
  (:require [webdev.item.model :as items])
  (:require [webdev.item.handler :refer [handle-index-items
                                         handle-create-item
                                         handle-delete-item
                                         handle-update-item]])
  (:require
            [hiccup.middleware :refer [wrap-base-url]]
            [ring.middleware.reload :refer [wrap-reload]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.resource :refer [wrap-resource]]
            [ring.middleware.file-info :refer [wrap-file-info]]
            [compojure.core :refer [defroutes ANY GET POST PUT DELETE]]
            [compojure.route :refer [not-found]]
            [compojure.handler :as handler]
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
  (POST "/items" [& params] (handle-create-item params))
  (DELETE "/items/:item-id" [item-id] (handle-delete-item item-id))
  (PUT "/items/:item-id" [& params] (handle-update-item params))

  (not-found "Page not found."))


;;(defn wrap-servlet-path-info [handler]
;;  (fn [request]
;;    (if-let [servlet-req (:servlet-request request))]
;;      (handler (assoc request :path-info (.getPathInfo servlet-req)))
;;      (handler request))))


(defn wrap-check-db-exists [hndlr]
   (items/create-table)
   (fn [req]
    (hndlr req)))

;;(defn wrap-server-header [hndlr]
;;  (fn [req]
;;    (hndlr (assoc req :webdev/db db))))

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

(def app-routes
  (wrap-base-url
   (wrap-check-db-exists
    (wrap-file-info
     (wrap-resource
      (wrap-server-response
       (wrap-params
        (wrap-simulated-methods routes))) "static")))))

(def app
  ;; passes request map when using: lein ring server (with or without port number)
  ;; passes port number when using: len run (with port number)

  ;;(println (str "Port:" port))
  ;;(items/create-table db)
  (handler/site app-routes))
  ;;(jetty/run-jetty #'app     {:port (if port (Integer/parseInt port) (Integer/parseInt (System/getenv "PORT")))}))
  ;;(jetty/run-jetty #'app                       {:port (Integer/parseInt port)}))
  ;;(jetty/run-jetty #'app                     {:port (Integer/parseInt (:server-port options))})



;;(defn -dev-main [& [req]]
;;  (println (str "Port:" (:server-port req)))
;;  (items/create-table db)
;;  (jetty/run-jetty (wrap-reload #'app)       {:port (Integer. (:server-port req))}))
