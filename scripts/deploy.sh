#!/usr/bin/env bash

mvn clean package

echo 'Copy files...'

scp -i ~/.ssh/id_rsa \
    target/SweaterBoot 0.0.1-SNAPSHOT.jar \
    kali@192.168.15.132:/home/kali/

echo 'Restart server...'

ssh -i ~/.ssh/id_rsa kali@192.168.15.132 << EOF
pgrep java | xargs kill -9
nohup java -jar SweaterBoot 0.0.1-SNAPSHOT.jar > log.txt &
EOF

echo 'Bye'