yum -y install vsftpd
sudo mkdir /product
sudo mkdir ftpfile
sudo useradd ftpuser -d /product/ftpfile -s /sbin/nologin
sudo chown -R ftpuser.ftpuser /product/ftpfile/
sudo passwd ftpuser

# edit /etc/vsftpd/chroot_list to add ftpuser
# edit /etc/selinux/config to disable selinux
# or run sudo setsebool -P ftp_home_dir 1
# edit /etc/vsftpd/vsftpd.config

# config firewall
