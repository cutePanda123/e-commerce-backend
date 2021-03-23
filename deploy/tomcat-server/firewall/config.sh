cd /etc/sysconfig

# init firewall
sudo iptables -P OUTPUT ACCEPT

sudo service iptables save

# back iptable config
mv iptables iptables.bak

# edit iptables to config firewall

sudo service iptables restart