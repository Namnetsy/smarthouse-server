./gradlew build
unzip build/distributions/server.zip
cp create_tables.sql server/bin/
cp log4j2.xml server/bin/
cp parse-logs.py server/bin
cp config.cfg server/bin
cp -r html server/bin
zip -r server server
rm -rf server
scp server.zip <username>@<server ip address>:<path>
rm server.zip
