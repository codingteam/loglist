cd /opt/loglist_test
unzip *.zip
mv loglist-jvm-*/* .
rm -r loglist-jvm-*
sudo /sbin/start loglist_test
