(ns webdev.item.handler
  (:require [webdev.item.model :refer [create-item
                                       read-items
                                       update-item
                                       delete-item]]
            [webdev.item.view :refer [items-page]]))


(defn handle-index-items [req]
  (let [items (read-items)]
    {:status 200
     :headers {}
     :body (items-page items)}))

(defn handle-create-item [req]
  (let [name (:name req)
        description (:description req)
        item-id (create-item name description)]
    {:status 302
     :headers {"Location" "/items"}
     :body ""}))

(defn handle-delete-item [item-id]
  (let [uuid-item-id (java.util.UUID/fromString item-id)
        exists? (delete-item uuid-item-id)]
    (if exists?
      {:status 302
       :headers {"Location" "/items"}
       :body ""}
      {:status 404
       :body "Item not found."
       :headers {}})))

(defn handle-update-item [req]
  (let [uuid-item-id (java.util.UUID/fromString (:item-id req))
        checked (:checked req)
        exists? (update-item uuid-item-id (= "true" checked))]
    (if exists?
      {:status 302
       :headers {"Location" "/items"}
       :body ""}
      {:status 404
       :body "Item not found."
       :headers {}})))


