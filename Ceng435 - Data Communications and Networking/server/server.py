import socket  
import os 
import time     
import struct  
import sys    
  
fragmentation_size = 1000
#create socket
TCP_socket = socket.socket(socket.AF_INET,socket.SOCK_STREAM)    
TCP_socket.settimeout(1000)   

#read ports
UDP_port = sys.argv[1]
TCP_port = sys.argv[2]
  
#port = 12345                

#bind to corresponding port  
TCP_socket.bind(('', int(TCP_port)))        
#print ("socket binded to %s" %(int(TCP_port)))  
  
TCP_socket.listen(5)            
 
#conect with client 
client, addr = TCP_socket.accept()      
#print ('Got connection from', addr ) 
filename = "transfer_file_TCP.txt"
#list of time difference between send and recieved times of chunks
time_diff =[]
with open(filename, "wb") as f:
    # receive 1008 bytes 1000 byte for chunk of file and 8 byte for time info
    bytes_read = client.recv(fragmentation_size+8)
    time_received = time.time()
    time_sent_first = struct.unpack("d",bytes_read[:8])[0]
    time_diff.append(time_received-time_sent_first)
    #print("time received - sent")
    #print(time_received - time_sent_first)
    while bytes_read:
        time_received = time.time()
        # get send time from message
        time_sent = struct.unpack("d",bytes_read[:8])[0]
        if (time_received > time_sent) and (time_received-1 < time_sent):
            time_diff.append(time_received-time_sent)
        message = bytes_read[8:]
        f.write(message)
        bytes_read = client.recv(fragmentation_size+8)

del time_diff[1]
print("TCP Packets Average Transmission Time: " + str((sum(time_diff)/len(time_diff))*1000)+" ms")
print("TCP Communication Total Transmission Time: " + str((time_received - time_sent_first)*1000) +" ms")
client.close()  
TCP_socket.close()