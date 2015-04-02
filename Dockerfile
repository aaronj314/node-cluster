FROM java:8

COPY target/node-1.0.jar /apps/node.jar
WORKDIR /apps


CMD java -jar /apps/node.jar