(defproject organizer "0.1.0-SNAPSHOT"

  :description "Todo Organizer App. Organize with LOVE!"

  :min-lein-version "2.0.0"

  :dependencies [[org.clojure/clojure "1.9.0-alpha14"]
                 [org.clojure/clojurescript "1.9.494"]

                 [com.stuartsierra/component "0.3.1"]
                 [duct "0.8.2"]

                 [org.postgresql/postgresql "9.4.1211"]
                 [funcool/clojure.jdbc "0.9.0"]
                 [hikari-cp "1.7.5"]
                 [honeysql "0.8.2"]
                 [duct/ragtime-component "0.1.4"]

                 [compojure "1.5.1"]
                 [ring "1.5.0"]
                 [ring/ring-defaults "0.2.1"]
                 [ring-jetty-component "0.3.1"]

                 [ring-webjars "0.1.1"]
                 [org.webjars/normalize.css "3.0.2"]

                 [reagent "0.6.0"]
                 [re-frame "0.9.2"]
                 [re-com "2.0.0"]

                 [camel-snake-kebab "0.4.0"]
                 [environ "1.1.0"]
                 [org.slf4j/slf4j-nop "1.7.21"]]

  :plugins [[lein-environ "1.0.3"]
            [lein-cljsbuild "1.1.2"]]

  :main ^:skip-aot organizer.main

  :target-path "target/%s/"

  :resource-paths ["resources" "target/cljsbuild"]

  :prep-tasks [["javac"] ["cljsbuild" "once"] ["compile"]]

  :cljsbuild {:builds
              {:main {:jar true
                      :source-paths ["src"]
                      :compiler
                      {:output-to "target/cljsbuild/organizer/public/js/main.js"
                       :optimizations :advanced}}}}

  :aliases {"run-task" ["with-profile" "+repl" "run" "-m"]
            "setup"    ["run-task" "dev.tasks/setup"]}

  :profiles {:dev  [:project/dev  :profiles/dev]

             :test [:project/test :profiles/test]

             :repl {:resource-paths ^:replace ["resources" "target/figwheel"]

                    :prep-tasks     ^:replace [["javac"] ["compile"]]}

             :uberjar {:aot :all}

             :profiles/dev  {}

             :profiles/test {}

             :project/dev
             {:dependencies [[duct/generate "0.7.0"]
                             [reloaded.repl "0.2.2"]
                             [org.clojure/tools.namespace "0.2.11"]
                             [org.clojure/tools.nrepl "0.2.12"]
                             [eftest "0.1.1"]
                             [com.gearswithingears/shrubbery "0.3.1"]
                             [kerodon "0.7.0"]
                             [binaryage/devtools "0.6.1"]
                             [com.cemerick/piggieback "0.2.1"]
                             [duct/figwheel-component "0.3.2"]
                             [figwheel "0.5.0-6"]]

              :source-paths ["dev"]

              :repl-options {:init-ns user

                             :nrepl-middleware
                             [cemerick.piggieback/wrap-cljs-repl]}

              :env {:port "3000"}}

             :project/test  {}})
