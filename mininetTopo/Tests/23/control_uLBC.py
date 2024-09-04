#!/usr/bin/env python
from time import sleep, time
from scapy.all import *
import numpy as np

#The average time difference between rows is: 0.014004 seconds
#The average length of packets is: 1145.823029444058 bytes
#The max length of packets is: 1278 bytes
#The min length of packets is: 44 bytes
#The probabilities of the top 5 lengths are: Length
#1278    0.925443
#461     0.032871
#59      0.018074
#447     0.016534
#49      0.007078

packet_count = 12782         # Number of packets to send - 847406

def send_packets():
    pkts=[]
    packet_sizes = np.random.choice([205, 333, 461, 589, 845], p=[0.025, 0.135, 0.68, 0.135, 0.025], size=packet_count)
    i = 0
    for packet_size in packet_sizes:
        to_send = "ID-uRLLC-"+str(i)+"-"
        # Calculate the size of the payload needed to fill the packet to the desired size
        fixed_payload_size = len(to_send)
        ip_header_size = 20  # IP header size (assuming no options)
        raw_data_size = packet_size - ip_header_size - fixed_payload_size - 14
        
        # Generate random data to fill the remaining size
        random_data = ''.join(np.random.choice(list('ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz'), size=raw_data_size))
                
        # Construct the full payload
        full_payload = to_send + random_data
        
        packet = IP(src="10.10.10.4",dst="10.0.0.1",tos=112)/Raw(load=full_payload)  # Use sequence number in payload
        pkts.append(packet)
        i += 1
    
    input("Packets generated!\nPress Enter to start sending packets")
    start = time.time()
    conf.netcache.arp_cache["10.0.0.1"] = "00:00:00:00:00:01"
    hello_packet = IP(dst="10.0.0.1",tos=112)/Raw(load='Hello!!')
    send(hello_packet, verbose=0)
    time.sleep(3)
    
    # Need to split list to avoid cach timout
    for i in range(0, packet_count, 1500):
        conf.netcache.arp_cache["10.0.0.1"] = "00:00:00:00:00:01"
        send(pkts[i:i+1500], inter=0.007376, verbose=0)

    end = time.time()
    print(end - start)

if __name__ == '__main__':
    try:
        print("Starting the cmd uLBC packets gen...")
        send_packets()
    except KeyboardInterrupt:
        print("Stopping the controler gen")