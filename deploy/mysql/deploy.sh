# check if mysql is installed
sudo rpm -qa | grep mysql-server

sudo yum -y install mysql-server

# update char set
#character-set-server=utf8
#default-character-set=utf8
sudo vim /etc/my.cnf

# setup auto start
sudo chkconfig mysqld on
sudo chkconfig --list mysqld
sudo service mysqld restart

# edit root password
mysql -u root
select user,host,password from mysql.user;
set password for root@'localhost' = password('password');
set password for root@'machine-name' = password('password');
set password for root@'127.0.0.1' = password('password');

# delete anonymous users
delete from mysql.user where user = '';
flush privileges;
insert into mysql.user(host, user, password) values ('localhost', 'mmall', password('mmallpassword'));

# enable remote access

# create a database
create database `mmall` default character set utf8 collate utf8_general_ci;

# grant database permission to new user
grant all privileges on mmall.* to mmall@localhost identified by 'mmallpassword';
flush privileges;

# dump database
mysqldump -u root -p mmall > mmall-dump.sql

# improt dump file
mysql -u root -p -h localhost mmall  < /mmall-dump.sq


systemctl start mysqld

