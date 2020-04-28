#!/usr/bin/env python3

import socket

HOST = '127.0.0.1'  # Standard loopback interface address (localhost)
PORT = 55553        # Port to listen on (non-privileged ports are > 1023)

with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
    s.bind((HOST, PORT))
    s.listen()
    conn, addr = s.accept()

    nxt="next"

    with conn:
        print('Connected by', addr)
        for i in range(100):
            data = "add state meh"+str(i).zfill(3)
           # data = "add transition Playing->Playing when pong on pingPort"
            conn.sendall(data.encode())

        conn.sendall(nxt.encode())

    s.close()
