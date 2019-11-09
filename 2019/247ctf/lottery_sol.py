import random
import socket
import time

secret = random.Random()

for i in range(0, 1000):
    secret.seed(int(time.time() + 1))
    sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    sock.connect(('a42087e844f6446e.247ctf.com', 50153))
    sock.recv(1024)
    guess = str(secret.random()).encode()
    sock.sendall(guess)
    res = str(sock.recv(1024))
    if 'Nope' not in res:
        print(res)
        break
    print(res)
    sock.close()
    
247CTF{3c435fe8a89cb0c65fdfcf0089669808}