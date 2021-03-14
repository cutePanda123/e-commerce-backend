iptables -A INPUT -p TCP --dport 61001:62000 -j ACCEPT
iptables -A OUTPUT -p TCP --sport 61001:62000 -j ACCEPT
iptables -A INPUT -p TCP --dport 20:21 -j ACCEPT
iptables -A OUTPUT -p TCP --sport 20:21 -j ACCEPT
service iptables save
iptables -L -n -v