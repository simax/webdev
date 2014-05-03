(ns webdev.item.handler
  (:require [webdev.item.model :refer [create-item
                                       read-items
                                       update-item
                                       delete-item]]
            [webdev.item.view :refer [items-page]]
            [ring.util.response :refer [redirect]]
            [ring.util.request :refer :all]))

(defn items-list [req]
  (str (:context req) "/items"))

(defn handle-index-items [req]
  (let [items (read-items)]
    {:status 200
     :headers {}
     :body (items-page items)}))

(defn handle-create-item [name description req]
  (create-item name description)
  (redirect (items-list req)))

(defn handle-delete-item [item-id req]
  (let [uuid-item-id (java.util.UUID/fromString item-id)
        exists? (delete-item uuid-item-id)]
    (if exists?
      (redirect (items-list req))
      {:status 404
       :body "Item not found."
       :headers {}})))

(defn handle-update-item [item-id checked req]
  (println (str "This is the context:" (:context req)))
  (let [uuid-item-id (java.util.UUID/fromString item-id)
        exists? (update-item uuid-item-id (= "true" checked))]
    (if exists?
      (redirect (items-list req))
      {:status 404
       :body "Item not found."
       :headers {}})))


