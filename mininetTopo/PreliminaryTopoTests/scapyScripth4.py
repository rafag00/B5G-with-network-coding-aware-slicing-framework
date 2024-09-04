from scapy.all import *
import numpy as np

conf.netcache.arp_cache["10.0.0.1"] = "00:00:00:00:00:01"
packet_count = 1000

send(IP(dst="10.0.0.1",tos=80)/Raw(load="First packet!!"), verbose=0)
time.sleep(3)

print("Sending response and stopping sniffing.")

packet_sizes = np.random.choice([44, 59, 447, 461, 1278], p=[0.007078, 0.018074, 0.016534, 0.032871, 0.925443], size=packet_count)
i = 0
for packet_size in packet_sizes:
    to_send = "ID-"+str(i)+"-"
    
    # Calculate the size of the payload needed to fill the packet to the desired size
    fixed_payload_size = len(to_send)
    ip_header_size = 20  # IP header size (assuming no options)
    raw_data_size = packet_size - ip_header_size - fixed_payload_size
    
    # Generate random data to fill the remaining size
    random_data = ''.join(np.random.choice(list('ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz'), size=raw_data_size))
            
    # Construct the full payload
    full_payload = to_send + random_data
    
    packet = IP(dst="10.0.0.1",tos=80)/Raw(load=full_payload)  # Use sequence number in payload
    send(packet, verbose=0)
    
    i += 1
    time.sleep(0.014004)