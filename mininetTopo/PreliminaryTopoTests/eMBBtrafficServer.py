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

packet_count = 1000         # Number of packets to send - 847406

def send_packets():
    packet_sizes = np.random.choice([44, 59, 447, 461, 1278], p=[0.007078, 0.018074, 0.016534, 0.032871, 0.925443], size=packet_count)
    i = 0
    for packet_size in packet_sizes:
        to_send = "ID-eMBB-"+str(i)+"-"
        
        # Calculate the size of the payload needed to fill the packet to the desired size
        fixed_payload_size = len(to_send)
        ip_header_size = 20  # IP header size (assuming no options)
        raw_data_size = packet_size - ip_header_size - fixed_payload_size - 14  # 14 bytes for Ethernet header
        
        if(raw_data_size < 0):
            full_payload = to_send
        else:
            # Generate random data to fill the remaining size
            random_data = ''.join(np.random.choice(list('ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz'), size=raw_data_size))
            # Construct the full payload
            full_payload = to_send + random_data

        
        packet = IP(dst="10.0.0.1",tos=80)/Raw(load=full_payload)  # Use sequence number in payload
        send(packet, verbose=0)
        
        i += 1
        time.sleep(0.014004)  # Adjust based on desired packet rate

# Callback function to process each packet
def packet_callback(packet):
    if packet.haslayer(Raw):
        if b"Client Hello!!" in packet[Raw].load:
            print("Found 'Client Hello!!' in packet. Sending response and stopping sniffing.")
            
            send(IP(dst="10.0.0.1",tos=80)/Raw(load="First packet!!"), verbose=0)
            time.sleep(3)
            #send the server hello and start the streaming
            send(IP(dst="10.0.0.1",tos=80)/Raw(load="Server Hello!!"), verbose=0)
            
            # Stop sniffing by returning True
            return True
    return False

if __name__ == '__main__':
    print("Waiting for the client to send the first packet...")
    
    conf.netcache.arp_cache["10.0.0.1"] = "00:00:00:00:00:01"
    
    #snif for the client hello
    sniff(prn=packet_callback, stop_filter=packet_callback)
    
    try:
        print("Starting the eMBB packets gen...")
        send_packets()
    except KeyboardInterrupt:
        print("Stopping the server")