FROM tomcat:alpine
RUN wget -O /usr/local/tomcat/webapps/launchstation04.war http://192.168.43.253:8081/artifactory/nagp-devops/com/example/nagp-devops-exec/0.0.1-SNAPSHOT/nagp-devops-exec-0.0.1-SNAPSHOT.war
EXPOSE 3000
EXPOSE 8080
CMD ["catalina.sh", "run"]