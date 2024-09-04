# Boosting 5G with Network Coding-Aware Slicing

This work is done in order of a master degree that as the objective of enhance traffic routing decision-making in SDN-C based on QoS in the available network slices of the network infrastructure.


## Repository organization

```
B5G-with-network-coding-aware-slicing-framework
|   
+---mininetTopo
|       
+---onos-application
|   |       
|   \---ONOS-App
|                       
\---web_server
```

- mininetTopo: Multiple mininet topologies for testing porpuses
- ONOS-app: Developed ONOS application
- web_server: Flask web server with swagger API to receive the existing slices in the network

**Note: Run and deployment information available in the Readme of each forder**

## Work requirements

- Compile the ONOS-App with Java 11 and Apache Maven 3.9.6
- Linux VM
  - Run as docker containers
    - Latest PostgresSQL
    - ONOS 2.7.0-latest
  - Mininet

### Setting up the database
#### Create the container
```
docker run -d --name postgresSQL -p 5432:5432 -e POSTGRES_PASSWORD=postgres postgres
```
#### Enter the container and access the psql command line
```
docker exec -it postgresSQL bash
psql -h localhost -U postgres
```
#### Create the database
```
CREATE DATABASE db;
```
### Obtain the database IP
```
#In the VM bash
docker inspect postgresSQL
#Usually 172.17.0.# (2)
```

### Setting up ONOS
#### Create the ONOS container
```
docker run -t -d -p 80:8181 -p 6633:6633 -p 8101:8101 -p 5005:5005 -p 830:830 -p 443:8182 --name onos -e ONOS_APPS="drivers,fwd,gui2,openflow,ofagent" onosproject/onos:2.7-latest
```
#### Access container CLI and install openssh
```
docker exec -it onos /bin/bash
apt update
apt install openssh-server
```
#### Access the Karaf CLI
```
ssh -p 8101 -o StrictHostKeyChecking=no karaf@localhost
#passwor=karaf
# Access GUI - http://127.0.0.1:80/onos/ui/login.html
```
#### Install and initialize database connection dependencies
```
feature:install jdbc
feature:install pax-jdbc-postgresql
jdbc:ds-create -dc org.postgresql.Driver -url "jdbc:postgresql://IP_OF_DB:5432/DB_NAME" --username postgres --password postgres db
```

## License
