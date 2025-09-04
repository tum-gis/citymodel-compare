# Neo4j Guides

### How to Create a Guide

1. Create the guide in HTML format, such as [tutorial.html](html/tutorial.html)
2. Run the server:
   ```bash
   # Run the server in the background
   nohup python3 ./http-server.py &
   
   # List all running servers
    ps ax | grep http-server.py
   
   # Kill the server (if needed)
    kill -9 <PID>
   ```
3. Allow CORS in the host server (such as Apache):
   ```bash
   # Enable mod_headers
   sudo a2enmod headers
   
   # Update the configuration file
   <IfModule mod_headers.c>
      Header set Access-Control-Allow-Origin "*"
   </IfModule>
   ```
6. Whitelist host servers of these guides in `neo4j.conf`:
    ```bash
    # comma-separated list of base-urls, or * for everything
    browser.remote_content_hostname_whitelist=*
    ```
7. Access the guide in Neo4j Browser:
   ```
   :play http://localhost:8001/tutorial.html
   ```
   Or using this URL:
   ```
   http://localhost:7474/browser?cmd=play&arg=http://localhost:8001/tutorial.html
   ```
   Or change the file `neo4j.conf`:
   ```bash
   browser.post_connect_cmd=play http://localhost:8001/tutorial.html
   ```

### Source: 

https://neo4j.com/developer/guide-create-neo4j-browser-guide/

https://github.com/neo4j-contrib/neo4j-guides