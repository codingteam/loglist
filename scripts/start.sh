set -e
cd /opt/loglist
unzip *.zip
mv scalajvm-*/* .
rm -r scalajvm-*
sudo /bin/systemctl start loglist
