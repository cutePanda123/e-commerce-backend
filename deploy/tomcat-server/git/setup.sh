sudo yum -y install zlib-devel openssl-devel cpio expat-devel gettext-devel curl-devel perl-ExtUtils-CBuilder perl-ExtUtils- MakeMaker

sudo tar -zxvf git-2.8.0.tar.gz

cd git-2.8.0

sudo make prefix=/usr/local/git all

sudo make prefix=/usr/local/git install

# edit /ect/profile path export PATH=$PATH:$JAVA_HOME/bin:/usr/local/git/bin:$CATALINA_HOME/bin:@MAVEN_HOME/bin

git config --global user.name "name"
git config --global user.email "email"
git config --global core.autocrlf false
git config --global core.quotepath off
git config --global gui.encoding utf-8

ssh-keygen -t rsa -C "email"
eval `ssh-agent`
ssh-add ~/.ssh/id_rsa

# add the public key to GitHub