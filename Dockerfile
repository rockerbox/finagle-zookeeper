FROM ubuntu:14.04

MAINTAINER rickotoole

RUN apt-get update && \
apt-get install -y python-software-properties software-properties-common git-core 

RUN \
  echo oracle-java7-installer shared/accepted-oracle-license-v1-1 select true | debconf-set-selections && \
  add-apt-repository -y ppa:webupd8team/java && \
  apt-get update && \
  apt-get install -y oracle-java7-installer && \
  rm -rf /var/cache/oracle-jdk7-installer

RUN \
  apt-get -y install maven

ADD . /root/finagle-zookeeper

RUN \
  echo "deb http://dl.bintray.com/sbt/debian /" | tee -a /etc/apt/sources.list.d/sbt.list && \
  apt-get -y update && \
  apt-get -y --force-yes install sbt
  
