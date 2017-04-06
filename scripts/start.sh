cd /opt/loglist
unzip *.zip
mv loglist-jvm-*/* .
rm -r loglist-jvm-*
sudo /bin/systemctl start loglist
