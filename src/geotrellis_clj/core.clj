(ns geotrellis-clj.core
  (:import [geotrellis.raster CellType MultibandTile]
           [geotrellis.raster.io.geotiff.reader GeoTiffReader]
           [geotrellis.raster MultibandTile]
           [geotrellis.layer SpatialKey]
           [geotrellis.util HttpRangeReader RangeReader]
           [geotrellis.store.cog COGValueReader]))

(defn ->url [s]
  (cond
    (instance? java.net.URL s)
    s

    (string? s)
    (java.net.URL. s)))

(defn get-cog-info
  "Fetches the value of a specific cell from a Cloud Optimized GeoTIFF."
  [{:keys [url]}]
  (let [cog-url          (->url url)
        range-reader     (HttpRangeReader. cog-url true)
        streaming-reader (RangeReader/rangeReaderToStreamingByteReader range-reader)
        geotiff          (GeoTiffReader/readSingleband streaming-reader)]
    geotiff))

(defn get-cog-cell-value
  "Fetches the value of a specific cell from a Cloud Optimized GeoTIFF."
  [{:keys [url col row]}]
  (let [cog-url          (->url url)
        range-reader     (HttpRangeReader. cog-url true)
        streaming-reader (RangeReader/rangeReaderToStreamingByteReader range-reader)
        geotiff          (GeoTiffReader/readSingleband streaming-reader)
        tile             (.tile geotiff)]
    (.getDouble tile col row)))

(comment

  (def cog-url "https://data.pyrecast.org/fuels_and_topography/cfo-2020/cbd.tif")

  (type (->url cog-url))

  (get-cog-info {:url cog-url})
  (get-cog-cell-value {:url cog-url :row 1000 :col 1000})

  )
