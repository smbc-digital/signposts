(defproject visualise "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.8.0"]
                 [ring-server "0.4.0"]
                 [reagent "0.7.0"]
                 [reagent-utils "0.2.1"]
                 [re-com "0.9.0"]
                 [ring "1.5.0"]
                 [ring/ring-defaults "0.2.1"]
                 [ring/ring-json "0.4.0"]
                 [bidi "2.0.16"]
                 [hiccup "1.0.5"]
                 [yogthos/config "0.8"]
                 [org.clojure/clojurescript "1.9.229"
                  :scope "provided"]
                 [cljsjs/flot "0.8.3-0"]
                 [com.cognitect/transit-cljs "0.8.239"]
                 [secretary "1.2.3"]
                 [venantius/accountant "0.1.7"
                  :exclusions [org.clojure/tools.reader]]
                 [cljs-ajax "0.5.8"]
                 [com.andrewmcveigh/cljs-time "0.4.0"]
                 [clj-http "2.3.0"]
                 [buddy "1.3.0"]
                 [com.cemerick/url "0.1.1"]
                 [org.slf4j/log4j-over-slf4j "1.7.14"]
                 [org.slf4j/jul-to-slf4j "1.7.14"]
                 [org.slf4j/jcl-over-slf4j "1.7.14"]
                 [com.fzakaria/slf4j-timbre "0.3.5"]
                 [com.taoensso/timbre "4.10.0"]
                 [camel-snake-kebab "0.4.0"]
                 [alandipert/storage-atom "1.2.4"]
                 [hodgepodge "0.1.3"]
                 [reagent-forms "0.5.32"]
                 [org.clojure/java.jdbc "0.7.7"]
                 [org.xerial/sqlite-jdbc "3.23.1"]
                 ]

  :plugins [[lein-ring "0.11.0"]
            [lein-environ "1.0.2"]
            [lein-cljsbuild "1.1.1"]
            [lein-asset-minifier "0.2.7"
             :exclusions [org.clojure/clojure]]
            [lein-doo "0.1.7"]]

  :ring {:handler      gov.stockport.sonar.visualise.handler/app
         :uberwar-name "visualise.war"}

  :min-lein-version "2.5.0"

  :uberjar-name "visualise.jar"

  :main gov.stockport.sonar.visualise.server

  :clean-targets ^{:protect false}
[:target-path
 [:cljsbuild :builds :app :compiler :output-dir]
 [:cljsbuild :builds :app :compiler :output-to]]

  :source-paths ["src/clj" "src/cljc"]

  :test-paths ["test/clj" "test/cljs"]

  :resource-paths ["resources" "target/cljsbuild"]

  :minify-assets
  {:assets
   {"resources/public/css/site.min.css" "resources/public/css/site.css"}}

  :cljsbuild
  {:builds {:min
            {:source-paths ["src/cljs" "src/cljc" "env/prod/cljs"]
             :compiler
                           {:output-to     "target/cljsbuild/public/js/app.js"
                            :output-dir    "target/uberjar"
                            :optimizations :advanced

                            :externs       ["externs/flot.plugins.navigate.ext.js"]
                            :pretty-print  false}}
            :app
            {:source-paths ["src/cljs" "src/cljc" "env/dev/cljs"]
             :compiler
                           {:main          "visualise.dev"
                            :asset-path    "/js/out"
                            :output-to     "target/cljsbuild/public/js/app.js"
                            :output-dir    "target/cljsbuild/public/js/out"
                            :source-map    true
                            :optimizations :none
                            :pretty-print  true}}
            :test
            {:source-paths ["src/cljs" "src/cljc" "env/dev/cljs" "test/cljs"]
             :compiler
                           {:main          "gov.stockport.sonar.visualise.runner"
                            :output-to     "target/cljsbuild/public/js/testable.js"
                            :source-map    true
                            :optimizations :none
                            :pretty-print  true}}

            :devcards
            {:source-paths ["src/cljs" "src/cljc" "env/dev/cljs"]
             :figwheel     {:devcards true}
             :compiler     {:main                 "gov.stockport.sonar.visualise.cards"
                            :asset-path           "js/devcards_out"
                            :output-to            "target/cljsbuild/public/js/app_devcards.js"
                            :output-dir           "target/cljsbuild/public/js/devcards_out"
                            :source-map-timestamp true
                            :optimizations        :none
                            :pretty-print         true}}
            }

   }

  :figwheel {:http-server-root "public"
             :server-port      3449
             :nrepl-port       7002
             :nrepl-middleware ["cemerick.piggieback/wrap-cljs-repl"]
             :css-dirs         ["resources/public/css"]
             :ring-handler     gov.stockport.sonar.visualise.handler/app}

  :sass {:src "src/sass"
         :dst "resources/public/css"}

  :profiles {:dev     {:repl-options {:init-ns          gov.stockport.sonar.visualise.repl
                                      :nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}

                       :dependencies [[ring/ring-mock "0.3.0"]
                                      [ring/ring-devel "1.5.0"]
                                      [prone "1.1.2"]
                                      [figwheel-sidecar "0.5.8"]
                                      [org.clojure/tools.nrepl "0.2.12"]
                                      [com.cemerick/piggieback "0.2.2-SNAPSHOT"]
                                      [devcards "0.2.2"]
                                      [pjstadig/humane-test-output "0.8.1"]
                                      [midje "1.8.3"]
                                      [cljs-react-test "0.1.4-SNAPSHOT"]
                                      [cljsjs/react-with-addons "15.2.0-0"]
                                      [cljsjs/react-dom "15.2.0-0" :exclusions [cljsjs/react]]
                                      [prismatic/dommy "1.1.0"]
                                      [stubadub "2.0.0"]]

                       :source-paths ["env/dev/clj"]
                       :plugins      [[lein-figwheel "0.5.8"]
                                      [lein-sassy "1.0.7"]
                                      [lein-midje "3.2.1"]]

                       :injections   [(require 'pjstadig.humane-test-output)
                                      (pjstadig.humane-test-output/activate!)]

                       :env          {:dev true}}

             :uberjar {:hooks        [minify-assets.plugin/hooks]
                       :source-paths ["env/prod/clj"]
                       :prep-tasks   ["compile" ["cljsbuild" "once" "min"]]
                       :env          {:production true}
                       :aot          :all
                       :omit-source  true}})
