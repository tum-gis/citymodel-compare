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
#docker tag citymodel-compare sonnguyentum/citymodel-compare:1.0.0
#docker push sonnguyentum/citymodel-compare:1.0.0
docker tag citymodel-compare tumgis/citymodel-compare:1.0.0
docker push tumgis/citymodel-compare:1.0.0
# OR For dev purposes
docker tag citymodel-compare tumgis/citymodel-compare:1.0.0-dev
docker push tumgis/citymodel-compare:1.0.0-dev
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
# Install
sudo apt update
sudo apt install apache2
# sudo service apache2 start

# Automatically start apache on reboot
sudo systemctl enable apache2
```

### Configure ServerName

```bash
# Create a conf file
sudo vim /etc/apache2/conf-available/servername.conf

# Add this line to the file
ServerName your.domain.com

# Add config to apache2
sudo a2enconf servername
sudo systemctl reload apache2
sudo service apache2 restart

# Check logs
sudo vim /var/log/apache2/error.log
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
sudo iptables -L -n
# sudo apt install net-tools
sudo netstat -ntlp
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
sudo neo4j restart

# This webpage should now be available
# https://<custom_domain>:7473
# neo4j+s://<custom_domain>:7687
```

## Add Neo4j Tutorial

### Create a Guide

```bash
# Create directory
cd /home/user
mkdir neo4j-guides

# This directory can contain many tutorials
cd neo4j-guides
vim tutorial
```

The content of the tutorial is in HTML format. Example taken
from [Neo4j Guides](https://github.com/neo4j-contrib/neo4j-guides/blob/master/docs/html-guides.adoc#full-example):

```html

<article class="guide">
    <carousel class="deck container-fluid">
        <slide class="row-fluid">
            <div class="col-sm-3">
                <h3>Title one</h3>
                <p class="lead">First lead</p>
            </div>
            <div class="col-sm-9">
                <p>This is the content in the main column.</p>
                <ol class="big">
                    <li>Load: create data from external CSV files</li>
                    <li>Index: index nodes based on label</li>
                    <li>Relate: transform foreign key references into data relationships</li>
                    <li>Promote: transform join records into relationships</li>
                </ol>
            </div>
        </slide>
        <slide class="row-fluid">
            <div class="col-sm-3">
                <h3>Title two</h3>
                <p class="lead">Second lead</p>
            </div>
            <div class="col-sm-9">
                <figure>
                    <pre class="pre-scrollable code runnable">CREATE INDEX ON :Product(productID)</pre>
                    <figcaption>Find the produce suppliers.</figcaption>
                </figure>
                <figure>
                    <pre class="pre-scrollable code runnable">CREATE INDEX ON :Category(categoryID)</pre>
                </figure>
                <figure>
                    <pre class="pre-scrollable code runnable">CREATE INDEX ON :Supplier(supplierID)</pre>
                </figure>
                <h3>More code</h3>
                <ul class="undecorated">
                    <li><a play-topic="movie-graph">Movie Graph</a> - actors &amp; movies</li>
                    <li><a play-topic="query-template">Query Templates</a> - common ad-hoc queries</li>
                    <li><a play-topic="cypher">Cypher</a> - query language fundamentals</li>
                </ul>
            </div>
        </slide>
    </carousel>
</article>
```

### Publish the Guide

Insert the code:

```bash
# Create code for server
cd /home/user/neo4j-guides
touch http-server.py
touch https-server.py
```

The content of the file `http-server.py` (taken
from [this](https://github.com/neo4j-contrib/neo4j-guides/blob/master/http-server.py)):

```python
#! /usr/bin/env python
import os

try:
    from http.server import HTTPServer, SimpleHTTPRequestHandler
except ImportError:
    from BaseHTTPServer import HTTPServer
    from SimpleHTTPServer import SimpleHTTPRequestHandler


class CORSRequestHandler(SimpleHTTPRequestHandler):

    def end_headers(self):
        self.send_header('Access-Control-Allow-Origin', '*')
        self.send_header('Access-Control-Allow-Methods', 'GET')
        self.send_header('Access-Control-Allow-Headers', '*')
        self.send_header('Access-Control-Allow-Headers',
                         'Pragma,Cache-Control,If-Modified-Since,Content-Type,X-Requested-With,X-stream,X-Ajax-Browser-Auth')
        SimpleHTTPRequestHandler.end_headers(self)

    def do_OPTIONS(self):
        self.send_response(200)
        self.end_headers()


if __name__ == '__main__':
    HTTPServer(('0.0.0.0', 8001), CORSRequestHandler).serve_forever()
```

The content of the file `https-server.py` (based
on [this](https://gist.github.com/DannyHinshaw/a3ac5991d66a2fe6d97a569c6cdac534?permalink_comment_id=4565141#gistcomment-4565141)):

```python
import http.server
import ssl


class CORSRequestHandler(http.server.SimpleHTTPRequestHandler):
    def end_headers(self):
        self.send_header('Access-Control-Allow-Origin', '*')
        self.send_header('Access-Control-Allow-Methods', 'GET')
        self.send_header('Access-Control-Allow-Headers', '*')
        self.send_header('Access-Control-Allow-Headers',
                         'Pragma,Cache-Control,If-Modified-Since,Content-Type,X-Requested-With,X-stream,X-Ajax-Browser-Auth')
        super().end_headers()

    def do_OPTIONS(self):
        self.send_response(200)
        self.end_headers()


server_address = ('0.0.0.0', 4443)
httpd = http.server.HTTPServer(server_address, CORSRequestHandler)
ctx = ssl.SSLContext(protocol=ssl.PROTOCOL_TLS_SERVER)
ctx.load_cert_chain(certfile="/etc/neo4j/certificates/cert.pem", keyfile="/etc/neo4j/certificates/privkey.pem")
httpd.socket = ctx.wrap_socket(httpd.socket, server_side=True)
httpd.serve_forever()
```

### Convert images to base64 (load images directly)

```bash
# Save the base64 code in a text file (png)
echo "data:image/jpeg;base64,$(base64 -w 0 image.png)" > image.png.base64
# Save the base64 code in a text file (svg)
echo "data:image/svg+xml;base64,$(base64 -w 0 image.svg)" > image.svg.base64
```

```html
<!-- Copy the content of the exported text file and paste into img -->
<img src="data:image/jpeg;base64,..." .../>
<img src="data:image/svg+xml;base64,..." .../>
```

### Configure firewall

```bash
# HTTP Python Server
sudo iptables -A INPUT -p tcp --dport 8001 -j ACCEPT

# HTTPS Python Server
sudo iptables -A INPUT -p tcp --dport 4443 -j ACCEPT
```

### Configure Neo4j

```bash
# Open config file
sudo vim /etc/neo4j/neo4j.conf

# Whitelist host servers of these guides
browser.remote_content_hostname_whitelist=*

# Tutorial on startup (HTTP)
browser.post_connect_cmd=play http://<server_name>:8001/tutorial

# Tutorial on startup (HTTPS)
browser.post_connect_cmd=play https://<server_name>:4443/tutorial
```

### Run Python Server

The following code snippet runs the Python server for only the current session:

```bash
# Change directory
cd /home/user/neo4j-guides

# Run the server in the background (HTTP)
sudo nohup python3 ./http-server.py &

# Run the server in the background (HTTPS)
sudo nohup python3 ./https-server.py &

# List all running servers
ps -ef | grep http-server.py
ps ax | grep http-server.py
ps ax | grep https-server.py
```

The following code snippet allows for running the Python server even after the system has been rebooted:

```bash
# Open a crontab file
sudo crontab -e

# Add the following line at the end of the file
sudo nohup python3 /path/to/https-server.py &

# To check whether the crontab file has been updated
sudo vim /var/spool/cron/crontabs/root 
```

If the server is not listening despite already being executed:

```bash
# To find which process is listening on port 4443
sudo lsof -i :4443

# Stop that process
sudo kill 1234

# Rerun Python server again
sudo nohup python3 /path/to/https-server.py &
```

### Restart Neo4j

```bash
sudo neo4j restart
```

### Save and Restore Firewall Rules

After restart, firewall rules will be removed. To save and restore them:

```bash
# Install package
sudo apt install iptables-persistent

# The ipv4 and ipv6 rules are stored in /etc/iptables
# Simply adjust these files
# These files will be applied on the next reboot

# The created files are snapshot at the moment of installation
# To update these files with current firewall rules:
sudo iptables-save > /etc/iptables/rules.v4
sudo ip6tables-save > /etc/iptables/rules.v6
```