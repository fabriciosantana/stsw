"""Varredura curta e restrita para a demonstracao local do seminario."""

from concurrent.futures import ThreadPoolExecutor, as_completed
from datetime import datetime
import logging

from core.utils import wrapper_http


name = "portscan_demo"
description = "Scan a small, fixed set of local demo ports"
author = "Local seminar demo"
documentation = []

DEMO_HOST = "127.0.0.1"
DEMO_PORTS = (5000, 6379, 8000, 8001)


class exploit:
    def __init__(self, requester, args):
        logging.info("Module '%s' launched (local demo only)", name)
        baseline = requester.do_request(args.param, "")
        if baseline is None:
            logging.error(
                "Lab server is unavailable on 127.0.0.1:5000. "
                "Keep lab_server.py running in the first terminal."
            )
            return

        with ThreadPoolExecutor(max_workers=len(DEMO_PORTS)) as executor:
            futures = {
                executor.submit(self.check_port, requester, args.param, port, baseline): port
                for port in DEMO_PORTS
            }
            for future in as_completed(futures):
                future.result()

    @staticmethod
    def check_port(requester, param, port, baseline):
        payload = wrapper_http("", DEMO_HOST, port)
        response = requester.do_request(param, payload)
        timestamp = datetime.today().time().replace(microsecond=0)

        if response is None or "Connection refused" in response.text:
            state = "closed"
        elif baseline is not None and response.text == baseline.text:
            state = "filtered"
        else:
            state = "open"

        print(f"\t[{timestamp}] {DEMO_HOST}:{port:<5} {state}")
