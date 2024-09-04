#!/usr/bin/env python
from time import sleep, time
from scapy.all import *


# Callback function to process each packet
def packet_callback(packet):
    if packet.haslayer(Raw):
        if b"Server Hello!!" in packet[Raw].load:
            print("Found 'Server Hello!!' in packet. Going to sleep now.")      
            # Stop sniffing by returning True
            return True
    return False

if __name__ == '__main__':
    input("Press Enter to start the eMBB packets gen...")
    
    conf.netcache.arp_cache["10.0.0.4"] = "00:00:00:00:00:04"
    
    send(IP(dst="10.0.0.4",tos=80)/Raw(load="First packet!!"), verbose=0)
    time.sleep(3)
    send(IP(dst="10.0.0.4",tos=80)/Raw(load="Client Hello!!"), verbose=0)
    
    sniff(prn=packet_callback, stop_filter=packet_callback)