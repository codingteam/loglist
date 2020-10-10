FROM postgres:9.3

COPY loglist-db-enable-pgcrypto.sql /docker-entrypoint-initdb.d/
RUN ["chmod", "a+r", "/docker-entrypoint-initdb.d/loglist-db-enable-pgcrypto.sql"]
