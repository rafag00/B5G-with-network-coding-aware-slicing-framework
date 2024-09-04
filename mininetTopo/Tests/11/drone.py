#!/usr/bin/env python
from scapy.all import *
import numpy as np
import time

# Number of packets to send - 847406

#1500    0.650744
#40      0.339742
#1402    0.004554
#1304    0.002615
#1206    0.002344

def send_packets():
    pkts=[]
    packet_count = 261969
    packet_sizes = np.random.choice([1500, 40, 1402, 1304, 1206], p=[0.650744, 0.339743, 0.004554, 0.002615, 0.002344], size=packet_count)
    i = 0
    for packet_size in packet_sizes:
        to_send = str(i)+"-"

        # Calculate the size of the payload needed to fill the packet to the desired size
        fixed_payload_size = len(to_send)
        ip_header_size = 20  # IP header size (assuming no options)
        raw_data_size = packet_size - ip_header_size - fixed_payload_size - 14

        if raw_data_size <= 0:
            full_payload = to_send
        else: 
            # Generate random data to fill the remaining size
            random_data = ''.join(np.random.choice(list('ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz'), size=raw_data_size))
            # Construct the full payload
            full_payload = to_send + random_data
            
        packet = IP(src="10.10.10.1",dst="10.0.0.2",tos=80)/Raw(load=full_payload)
        pkts.append(packet)
        i += 1
    
    half_packet = int(packet_count/12)
    input("Packets generated!\nPress Enter to start sending packets")
    #start = time.time()
    conf.netcache.arp_cache["10.0.0.2"] = "00:00:00:00:00:02"
    hello_packet = IP(dst="10.0.0.2",tos=80)/Raw(load='Hello!!')
    send(hello_packet, verbose=0)
    time.sleep(3)
    start = time.time()

    # Need to split list to avoid cach timout
    for i in range(0, packet_count, half_packet):
        if (time.time() - start) >= 120:
            break
        conf.netcache.arp_cache["10.0.0.2"] = "00:00:00:00:00:02"
        send(pkts[i:i+half_packet], verbose=0) #1/8000
    

    end = time.time()
    print(end - start)

if __name__ == '__main__':    
    try:
        print("Starting the drone packets gen...")
        send_packets()
    except KeyboardInterrupt:
        print("Stopping the drone gen")
