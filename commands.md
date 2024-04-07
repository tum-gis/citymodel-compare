# Command Line Interface

## Docker

```bash
# Build container
docker build --no-cache -t citymodel-compare .

# For experimenting with the docker image
docker run -it --rm -p7474:7474 -p7687:7687 citymodel-compare
# Run detached with name and tail logs
docker run -d --name citymodel-compare -p7474:7474 -p7687:7687 citymodel-compare ; docker logs --tail 50 -f citymodel-compare

# For production, it is recommended to build the JAR and include it in the ENTRYPOINT of the Dockerfile
# The JAR file is located in build/libs
# .\gradlew shadowJar

# Tag and push the image to Docker Hub
docker tag citymodel-compare sonnguyentum/citymodel-compare:1.0.0
docker push sonnguyentum/citymodel-compare:1.0.0
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

# Change password
sudo neo4j-admin set-initial-password <password>

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
