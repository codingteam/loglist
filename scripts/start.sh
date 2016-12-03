cd /opt/loglist
unzip *.zip
mv loglist-jvm-*/* .
rm -r loglist-jvm-*
sudo /usr/bin/systemctl start loglist
