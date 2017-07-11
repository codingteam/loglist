set -e
sudo /bin/systemctl stop loglist
cd /opt/loglist
rm -rf bin
rm -rf conf
rm -rf lib
rm -rf share
rm -rf *.zip
