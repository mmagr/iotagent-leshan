FROM zenika/alpine-maven:3-jdk8


RUN mkdir -p /usr/src/app/
WORKDIR /usr/src/app
ADD . /usr/src/app

RUN ["mvn", "install"]

CMD ["java", "-jar", "target/iotagent-leshan-1.0-SNAPSHOT-jar-with-dependencies.jar"]
