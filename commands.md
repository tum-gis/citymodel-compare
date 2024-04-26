# Command Line Interface

## Docker

```bash
# Build container
docker build --no-cache -t citymodel-compare .

# For experimenting with the docker image
docker run -it --rm -p7474:7474 -p7687:7687 citymodel-compare
# OR Run detached with name and tail logs
docker run -d --name citymodel-compare -p7474:7474 -p7687:7687 citymodel-compare ; docker logs --tail 50 -f citymodel-compare

# For production, it is recommended to build the JAR and include it in the ENTRYPOINT of the Dockerfile
# The JAR file is located in build/libs
# .\gradlew shadowJar

# Tag and push the image to Docker Hub
docker tag citymodel-compare sonnguyentum/citymodel-compare:1.0.0
docker push sonnguyentum/citymodel-compare:1.0.0
# OR For dev purposes
docker tag citymodel-compare sonnguyentum/citymodel-compare:1.0.0-dev
docker push sonnguyentum/citymodel-compare:1.0.0-dev
```

## Neo4j (Linux)

```bash
# Install Neo4j
# https://neo4j.com/docs/operations-manual/current/installation/linux/debian/

# The installation is in /var/lib/neo4j
# The conf file is in /etc/neo4j/neo4j.conf

# Change neo4j.conf
server.directories.data=</path/to/data>
server.bolt.enabled=true
server.bolt.listen_address=0.0.0.0:7687
server.http.enabled=true
server.http.listen_address=0.0.0.0:7474

# The </path/to/data> can be an existing database
# It must contain the directories databases and transactions

# Change password in Neo4j Browser
:server change-password

# Start Neo4j
sudo neo4j start

# Allow remote access
sudo iptables -A INPUT -p tcp --dport 7687 -j ACCEPT
sudo iptables -A INPUT -p tcp --dport 7474 -j ACCEPT
# To delete
# sudo iptables -D INPUT -p tcp --dport 7687 -j ACCEPT
# sudo iptables -D INPUT -p tcp --dport 7474 -j ACCEPT

# Stop Neo4j
sudo neo4j stop
```

## Publish Neo4j using SSL

The following code-snippets are based on Ubuntu OS.

### Install Apache

```bash
sudo apt update
sudo apt install apache2
```

### Configure Firewall

```bash
# Apache HTTP
sudo iptables -A INPUT -p tcp --dport 80 -j ACCEPT

# Apache HTTPS
sudo iptables -A INPUT -p tcp --dport 443 -j ACCEPT

# Neo4j Bolt
sudo iptables -A INPUT -p tcp --dport 7687 -j ACCEPT

# Neo4j Browser
sudo iptables -A INPUT -p tcp --dport 7474 -j ACCEPT

# Neo4j Browser SSL
sudo iptables -A INPUT -p tcp --dport 7473 -j ACCEPT

# Show all rules
sudo iptables -L -v -n | more
```

### Install Let's Encrypt

Source: https://certbot.eff.org/instructions?ws=apache&os=ubuntufocal

```bash
# Install certbot
sudo snap install --classic certbot

# Prepare certbot command
sudo ln -s /snap/bin/certbot /usr/bin/certbot

# When asked, enter email address and <custom_domain>.

# Configure apache for certbot
sudo certbot --apache

# Test
sudo certbot renew --dry-run

# The webpage should now be available via HTTPS
# https://<custom_domain>
```

### Transfer Certificate and Private Key

```bash
# Copy
cd /etc/neo4j
sudo mkdir certificates
cd certificates
sudo cp /etc/letsencrypt/live/<custom_domain>/* ./

# Investigate user neo4j
id neo4j

# Assign read/write rights to neo4j
cd ..
sudo chown -R neo4j:adm ./certificates

# Check rights
ls -la certificates
```

### Configure Neo4j

Source: https://neo4j.com/docs/operations-manual/current/security/ssl-framework/

```bash
# Open config file
sudo vim /etc/neo4j/neo4j.conf

# Bolt connector
server.bolt.enabled=true
server.bolt.tls_level=REQUIRED
server.bolt.listen_address=0.0.0.0:7687

# HTTP Connector. There can be zero or one HTTP connectors.
server.http.enabled=false
server.http.listen_address=0.0.0.0:7474

# HTTPS Connector. There can be zero or one HTTPS connectors.
server.https.enabled=true
server.https.listen_address=0.0.0.0:7473

# Bolt SSL configuration
dbms.ssl.policy.bolt.enabled=true
dbms.ssl.policy.bolt.base_directory=/etc/neo4j/certificates
dbms.ssl.policy.bolt.private_key=private_key.pem
dbms.ssl.policy.bolt.public_certificate=cert.pem

# HTTPS SSL configuration
dbms.ssl.policy.https.enabled=true
dbms.ssl.policy.https.base_directory=/etc/neo4j/certificates
dbms.ssl.policy.https.private_key=private_key.pem
dbms.ssl.policy.https.public_certificate=cert.pem

# The certificate and private key can be different between Bolt SSL and HTTPS SSL.
```

### Make the Neo4j DB Readonly

Only Neo4j Enterprise allows management of users, roles and privileges -> In free version: Make the DB readonly.

```bash
# Edit config file
sudo vim /etc/neo4j/neo4j.conf

# Make the DB readonly
dbms.databases.default_to_read_only=true

# Change password
sudo neo4j-admin set-initial-password <password>
```

### Restart Neo4j

```bash
# Change password in Neo4j Browser
:server change-password

# Automatic Startup
sudo systemctl enable neo4j

# Restart
sudo systemctl restart neo4j

# This webpage should now be available
# https://<custom_domain>:7473
# neo4j+s://<custom_domain>:7687
```