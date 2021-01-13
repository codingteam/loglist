FROM hseeberger/scala-sbt:11.0.8_1.3.13_2.13.3 as build

WORKDIR /loglist
COPY . .
RUN sbt dist
RUN mkdir /opt/loglist
RUN cp scalajvm/target/universal/scalajvm*zip /opt/loglist

WORKDIR /opt/loglist
RUN unzip *.zip
RUN mv scalajvm-*/* .
RUN rm -rf scalajvm-*

FROM openjdk:11.0.9-jre

WORKDIR /app
COPY --from=build /opt/loglist .

ENTRYPOINT /app/bin/scalajvm -Dhttp.port=9000 -Dpidfile.path=/dev/null

EXPOSE 9000/tcp
