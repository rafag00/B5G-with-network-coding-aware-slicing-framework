#!/usr/bin/env python
from scapy.all import *
import numpy as np
        
if __name__ == '__main__':    
    conf.netcache.arp_cache["10.0.0.4"] = "00:00:00:00:00:04"
    
    hello_packet = IP(dst="10.0.0.4",tos=80)/Raw(load='Hello!!')
    send(hello_packet, verbose=0)
    time.sleep(3)
    
    try:
        print("Starting the test packets gen...")
        #pkt = IP(dst="10.0.0.4",tos=80)/TCP()/Raw(RandString(size=1500-40-14))
        #send(pkt, inter=0.001, count=1000)
        pkts=[]
        packet_count = 10000
        for i in range(packet_count):
            to_send = "ID-drone-"+str(i)+"-"
        
            # Calculate the size of the payload needed to fill the packet to the desired size
            fixed_payload_size = len(to_send)
            ip_header_size = 20  # IP header size (assuming no options)
            raw_data_size = 1500 - ip_header_size - fixed_payload_size - 14
            
            # Generate random data to fill the remaining size
            random_data = ''.join(np.random.choice(list('ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz'), size=raw_data_size))
                    
            # Construct the full payload
            full_payload = to_send + random_data
            packet = IP(dst="10.0.0.4",tos=80)/Raw(load=full_payload)
            pkts.append(packet)
        
        send(pkts, inter=0.000125) #1/8000
    except KeyboardInterrupt:
        print("Stopping the test gen")