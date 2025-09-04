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
