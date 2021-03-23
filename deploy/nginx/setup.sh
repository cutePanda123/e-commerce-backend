sudo yum -y install gcc zlib zlib-devel pcre-devel openssl openssl-devel

sudo tar -zxvf nginx-1.10.3.tar.gz
cd nginx-1.10.3
./configure
make
make install
whereis nginx
vim /usr/local/nginx/conf/nginx.conf
# added  include vhost/*.conf; to the conf file
mkdir /usr/local/nginx/conf/vhost

added config files to vhost folder

./usr/local/nginx/sbin/nginx
./usr/local/nginx/sbin/nginx -s restart
./usr/local/nginx/sbin/nginx -s stop