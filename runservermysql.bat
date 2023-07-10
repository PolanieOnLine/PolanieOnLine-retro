set POLSKAONLINE_VERSION=0.44
set LOCALCLASSPATH=.;data\script;data\conf;polskaonline-server-%POLSKAONLINE_VERSION%.jar;libs/marauroa.jar;libs/commons-lang.jar;libs/mysql-connector.jar;libs/log4j.jar;libs/simple.jar;libs/libtiled.jar;libs/groovy.jar
java -Xmx400m -cp "%LOCALCLASSPATH%" games.stendhal.server.StendhalServer -c server.ini -l