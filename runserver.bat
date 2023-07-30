set POL_VERSION=0.44
set LOCALCLASSPATH=.;data\script;data\conf;polanieonline-server-%POL_VERSION%.jar;libs\marauroa.jar;libs\mysql-connector-java-8.0.13.jar;libs\log4j.jar;libs\commons-lang.jar;libs\h2.jar
java -Xmx400m -cp "%LOCALCLASSPATH%" games.stendhal.server.StendhalServer -c server.ini -l
@pause