FROM openjdk:8-jre-alpine
LABEL maintainer="danbai@88.com"

RUN echo "Asia/Shanghai" > /etc/timezone
ADD u2ps-web.jar /bin/app/u2ps-web.jar
ADD /lib /bin/app/lib
CMD exec java -Dloader.path="/bin/app/lib" -jar /bin/app/u2ps-web.jar