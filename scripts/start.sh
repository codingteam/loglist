cd /opt/loglist || exit 1
unzip *.zip
mv loglist-jvm-*/* .
rm -r loglist-jvm-*
sudo /bin/systemctl start loglist
