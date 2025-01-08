# as our packaging is war Use an official Tomcat image as a base
FROM tomcat:9.0-jdk17

# Remove default web apps provided by Tomcat (optional), 
RUN rm -rf /usr/local/tomcat/webapps/*

# Copy the WAR file from the Maven target directory to the Tomcat webapps directory
COPY target/AuroAutoLocker.war /usr/local/tomcat/webapps/ROOT.war

# Expose the port Tomcat runs on (default is 8080)
EXPOSE 8080

# Set the command to run Tomcat
CMD ["catalina.sh", "run"]
