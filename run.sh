# For experimenting with the docker image
docker build --no-cache -t citymodel-compare .
docker run -it --rm -p7474:7474 -p7687:7687 citymodel-compare

# Run detached with name and tail logs
docker run -d --name citymodel-compare -p7474:7474 -p7687:7687 citymodel-compare ; docker logs --tail 50 -f citymodel-compare

# For production, it is recommended to build the JAR and include it in the ENTRYPOINT of the Dockerfile
# The JAR file is located in build/libs
# .\gradlew shadowJar

# Tag and push the image to Docker Hub
docker tag citymodel-compare sonnguyentum/citymodel-compare:1.0.0
docker push sonnguyentum/citymodel-compare:1.0.0
