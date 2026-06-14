"""Laboratorio SSRF local para a demonstracao do seminario.

O endpoint vulneravel aceita apenas HTTP para 127.0.0.1. Essa restricao
mantem a demonstracao isolada e impede requisicoes a sistemas externos.
"""

from ipaddress import ip_address
from threading import Thread
from urllib.error import HTTPError, URLError
from urllib.parse import urlparse
from urllib.request import urlopen

from flask import Flask, Response, request
from werkzeug.serving import make_server


vulnerable_app = Flask("ssrf_demo")
internal_app = Flask("internal_demo")
ALLOWED_PORTS = {5000, 6379, 8000, 8001}


@vulnerable_app.get("/")
def index():
    return "SSRF demo server"


@vulnerable_app.get("/ssrf")
def ssrf():
    target = request.args.get("url", "")
    parsed = urlparse(target)

    try:
        host = ip_address(parsed.hostname or "")
        port = parsed.port or 80
    except ValueError:
        return Response("Blocked: invalid target", status=400)

    if parsed.scheme != "http" or not host.is_loopback or port not in ALLOWED_PORTS:
        return Response("Blocked: local demo targets only", status=403)

    try:
        with urlopen(target, timeout=1) as response:
            return Response(response.read(), status=200, content_type="text/plain")
    except HTTPError as error:
        return Response(error.read(), status=200, content_type="text/plain")
    except (URLError, TimeoutError, OSError):
        return Response("Connection refused", status=200, content_type="text/plain")


@internal_app.get("/")
def internal_service():
    return "INTERNAL SERVICE: accessible only through the SSRF demo"


if __name__ == "__main__":
    print("[LAB] Vulnerable application: http://127.0.0.1:5000/ssrf?url=...")
    print("[LAB] Internal service:      http://127.0.0.1:8000/")
    print("[LAB] Allowed targets:       loopback ports 5000, 6379, 8000, 8001")
    print("[LAB] Keep this terminal open. Press Ctrl+C only when finished.")

    internal_server = make_server("127.0.0.1", 8000, internal_app, threaded=True)
    vulnerable_server = make_server("127.0.0.1", 5000, vulnerable_app, threaded=True)
    internal_thread = Thread(target=internal_server.serve_forever, daemon=True)
    internal_thread.start()

    try:
        vulnerable_server.serve_forever()
    except KeyboardInterrupt:
        print("\n[LAB] Stopping local services...")
    finally:
        vulnerable_server.shutdown()
        internal_server.shutdown()
        internal_thread.join(timeout=2)
