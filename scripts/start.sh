cd /opt/loglist
unzip *.zip
mv loglist-jvm-*/* .
rm -r loglist-jvm-*
sudo /sbin/start loglist
