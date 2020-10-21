FROM hseeberger/scala-sbt:11.0.8_1.4.0_2.13.3

WORKDIR /loglist
COPY . .
RUN sbt dist
RUN mkdir /opt/loglist
RUN cp scalajvm/target/universal/scalajvm*zip /opt/loglist

WORKDIR /opt/loglist
# Steps below mirror our scripts/start.sh
RUN unzip *.zip
RUN mv scalajvm-*/* .
RUN rm -rf scalajvm-*
# Steps below mirror our docs/loglist.service
# We don't set `-Dhttp.address=127.0.0.1` because we want the service to be
# accessible from outside.
# We pass an additional option to set a secret because Play! won't start
# without it.
ENTRYPOINT /opt/loglist/bin/scalajvm -Dhttp.port=9000 -Dpidfile.path=/dev/null -Dplay.http.secret.key="this key is long and secure to make Play! happy"

EXPOSE 9000/tcp
