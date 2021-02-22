FROM tomcat:alpine
RUN wget -O /usr/local/tomcat/webapps/nimitjohri.war http://192.168.1.7:8081/artifactory/nagp-devops-exam-2-dev/com/example/nagp-devops-exam-2-dev/0.0.1-SNAPSHOT/nagp-devops-exam-2-dev-0.0.1-SNAPSHOT.war
EXPOSE 8080
CMD ["catalina.sh", "run"]