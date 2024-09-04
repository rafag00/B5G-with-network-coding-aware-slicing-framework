#!/usr/bin/env python

from scapy.all import *
import numpy as np
import time

packet_count = 37         # Number of packets to send - 847406

devices_ips = ['192.136.10.'+str(i) for i in range(1, 255)]

#print(len('ID-1000000000')) # 13

#change to first calculate the packets and only then start sending them
# (x-16)-16 ; x-16; x; x+32; (x+32)+32 [50, 66, 82, 114, 146]

def send_packets():
    start = time.time()
    for i in range(packet_count):
        conf.netcache.arp_cache["10.0.0.2"] = "00:00:00:00:00:02"
        pkts=[]
        packet_sizes = np.random.choice([50,58,66,82,114], p=[0.025, 0.135, 0.68, 0.135, 0.025], size=len(devices_ips))
        j = 0
        for device_ip in devices_ips:
            to_send = "ID-mMTC-"+str(i)+"-"
            
            # Calculate the size of the payload needed to fill the packet to the desired size
            fixed_payload_size = len(to_send)
            ip_header_size = 20  # IP header size (assuming no options)
            raw_data_size = packet_sizes[j] - ip_header_size - fixed_payload_size - 14
            
            # Generate random data to fill the remaining size
            random_data = ''.join(np.random.choice(list('ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz'), size=raw_data_size))
                    
            # Construct the full payload
            full_payload = to_send + random_data
            
            packet = IP(src=device_ip,dst="10.0.0.2",tos=48)/Raw(load=full_payload)  # Use sequence number in payload
            pkts.append(packet)
            
        send(pkts, verbose=0)
        pkts.clear()
        
        time.sleep(3)  # Adjust based on desired burst rate
    
    end = time.time()
    print(end - start)
        
if __name__ == '__main__':    
    conf.netcache.arp_cache["10.0.0.2"] = "00:00:00:00:00:02"
    
    hello_packet = IP(dst="10.0.0.2",tos=48)/Raw(load='Hello!!')
    send(hello_packet, verbose=0)
    time.sleep(3)
    
    try:
        print("Starting the mMTC packets gen...")
        send_packets()
    except KeyboardInterrupt:
        print("Stopping the server")
