(ns webdev.item.model
  (:require [clojure.java.jdbc :as db]))



(defn create-table [db]
  (db/execute!
   db
   ["CREATE EXTENSION IF NOT EXISTS \"uuid-ossp\""])
  (db/execute!
   db
   ["CREATE TABLE IF NOT EXISTS items
    (id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name TEXT NOT NULL,
    description TEXT NOT NULL,
    checked BOOLEAN NOT NULL DEFAULT FALSE,
    date_created TIMESTAMPTZ NOT NULL DEFAULT now())"]))


 (defn create-item
   [db name description]
   (:id (first (db/query db ["INSERT INTO items (name, description) VALUES (?, ?) RETURNING id" name description]))))


 (defn update-item [db id checked]
   (= [1] (db/execute!
    db
    ["UPDATE items SET checked = ? WHERE id = ?" checked id])))


 (defn delete-item [db id]
   (= [1]
      (db/execute! db ["DELETE FROM items WHERE ID = ?" id])))

 (defn read-items [db]
   (db/query db "SELECT * FROM items ORDER BY date_created"))
