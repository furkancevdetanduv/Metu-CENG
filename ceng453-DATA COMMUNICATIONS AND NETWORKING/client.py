import socket             
import os
import time 
import sys
import struct


fragmentation_size = 1000
# Create a socket object  
TCP_socket = socket.socket(socket.AF_INET,socket.SOCK_STREAM)       
  
# Read server address and ports 
server_address = sys.argv[1]
UDP_listen_port = sys.argv[2]
TCP_listen_port = sys.argv[3]
UDP_client_port = sys.argv[4]
TCP_listen_port = sys.argv[5]

#TCP_port = 12345    

file_name = "transfer_file_TCP.txt"
#file_size = os.path.getsize(file_name)            
  
# connect to the server 
#TCP_socket.connect(('127.0.0.1', TCP_port))  
TCP_socket.connect((server_address, int(TCP_listen_port)))
t  = time.time()
#open file and read chunks of size 1000 byte
with open(file_name, "rb") as f:
    bytes_read = f.read(fragmentation_size)
    while bytes_read:
    	#add time before send
    	message = struct.pack("d",time.time()) + bytes_read
    	TCP_socket.sendall(message)
    	#read new chunk
    	bytes_read = f.read(fragmentation_size)

TCP_socket.close()     