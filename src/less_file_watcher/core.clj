(ns less-file-watcher.core
  (:use [clojure.java.shell :only [sh]])
  (:require [hawk.core :as hawk]))

(defn compile-less [src-file map? min?]
  (let [file-path (.getPath src-file)
        file-name-with-extension (.getName src-file)
        file-name (let [vec-s (clojure.string/split file-name-with-extension #"\.")
                        count-vec-s (count vec-s)]
                    (clojure.string/join (subvec vec-s 0 (dec count-vec-s))))
        file-parent-dir (.getParent src-file)
        map (if map? "--source-map" "")
        min (if min? "--clean-css" "")
        opt [min map]
        dst (str file-parent-dir "/" file-name ".css" )
        ;cmd (clojure.string/join " " ["lessc" file-path dst (clojure.string/join " " opt)])
        ]
    (println (str "Compiling " file-name-with-extension))
    (sh "lessc" file-path dst map min)
    (println (str "Compiled.")))
  )

(defn -main [& args]
  (hawk/watch! [{:paths ["resources/less"]
                 :filter hawk/file?
                 :handler (fn [ctx {:keys [file kind]}]
                            (let [vec-s (clojure.string/split (.getName file) #"\.")
                                  count-vec-s (count vec-s)
                                  extension (first (subvec vec-s (dec count-vec-s)))
                                  file-name-without-extension (clojure.string/join (subvec vec-s 0 (dec count-vec-s)))]
                              (if (= "less" extension)
                                ;; if it's a LESS file
                                (do
                                  (compile-less file true true)
                                  (println ctx)
                                  (assoc ctx (keyword file-name-without-extension) []))
                                ;; else
                                (do
                                  (println ctx)
                                  ctx))))}]))
