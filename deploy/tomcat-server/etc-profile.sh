# add the environment variables to the /etc/profile file
export JAVA_HOME=/usr/java/jdk1.8.0_281-amd64
export CLASSPATH=.:$JAVA_HOME/jre/lib/rt.jar:$JAVA_HOME/lib/dt.jar:$JAVA_HOME/lib/tools.jar
export CATALINA_HOME=/developer/apache-tomcat-7.0.108
export MAVEN_HOME=/developer/apache-maven-3.6.3

# run this command to link mvn
ln -s /developer/apache-maven-3.6.3/bin/mvn /usr/bin/mvn