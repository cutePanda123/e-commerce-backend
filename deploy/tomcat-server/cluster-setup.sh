# multi-server in my machine
# include the environment variables in the /etc/profile based on how many servers you want to start

export CATALINA_BASE=/usr/local/tomcat
export CATALINA_HOME=/usr/local/tomcat
export TOMCAT_HOME=/usr/local/tomcat

export CATALINA_2_BASE=/usr/local/tomcat1
export CATALINA_2_HOME=/usr/local/tomcat1
export TOMCAT_2_HOME=/usr/local/tomcat1

# set the server port, connector port, connector AJP port in server.xml to different values

