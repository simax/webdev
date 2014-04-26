(ns webdev.item.model
  (:require [clojure.java.jdbc :as db]))


(def conn (or
         (System/getenv "DATABASE_URL")
         "jdbc:postgresql://localhost/webdev"))

(defn create-table []
  (db/execute!
   conn
   ["CREATE EXTENSION IF NOT EXISTS \"uuid-ossp\""])
  (db/execute!
   conn
   ["CREATE TABLE IF NOT EXISTS items
    (id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name TEXT NOT NULL,
    description TEXT NOT NULL,
    checked BOOLEAN NOT NULL DEFAULT FALSE,
    date_created TIMESTAMPTZ NOT NULL DEFAULT now())"]))


 (defn create-item [name description]
   (:id (first (db/query conn ["INSERT INTO items (name, description) VALUES (?, ?) RETURNING id" name description]))))


 (defn update-item [id checked]
   (= [1] (db/execute!
    conn
    ["UPDATE items SET checked = ? WHERE id = ?" checked id])))


 (defn delete-item [id]
   (= [1]
      (db/execute! conn ["DELETE FROM items WHERE ID = ?" id])))

 (defn read-items []
   (db/query conn "SELECT * FROM items ORDER BY date_created"))
