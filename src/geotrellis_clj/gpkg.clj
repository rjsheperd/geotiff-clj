(ns geotrellis-clj.gpkg
  (:require [clojure.data.csv :as csv]
            [clojure.java.io :as io])
  (:import (org.geotools.data DataStoreFinder FileDataStoreFactorySpi)
           (org.geotools.data.geopackage GeoPackage)))

(defn create-geopackage
  [csv-file geopackage-file]
  (with-open [reader (io/reader csv-file)]
    (let [csv-data (csv/read-csv reader)
          headers (first csv-data)
          rows (rest csv-data)]
      
      ;; Create GeoPackage
      (let [params {"dbtype" "geopkg" "database" geopackage-file}
            geo-store (DataStoreFinder/getDataStore params)]
        (try
          ;; Define the schema based on the CSV headers (coordinate and attribute fields)
          ;; This is a simplified example, customize schema based on your CSV content
          (let [schema (-> (SimpleFeatureTypeBuilder.)
                           (.setName "CSVData")
                           (.add "location" Point.class)
                           (.add "name" String.class)
                           (.buildFeatureType))]
            ;; Create the feature source
            (let [feature-store (.createSchema geo-store schema)]
              
              ;; Create features and add to the feature store
              (let [builder (SimpleFeatureBuilder. schema)]
                (doseq [row rows]
                  (let [lon (Double/parseDouble (nth row 0))
                        lat (Double/parseDouble (nth row 1))
                        name (nth row 2)]
                    (do
                      (.add builder (Point. lon lat))
                      (.add builder name)
                      (.addFeatures feature-store (SimpleFeatureCollection. builder))))))
              (.dispose geo-store)))
          (finally
            (.dispose geo-store))))))))
```
