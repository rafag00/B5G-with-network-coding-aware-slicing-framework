from scapy.all import *

conf.netcache.arp_cache["10.0.0.4"] = "00:00:00:00:00:04"

# Create an IP packet with the destination IP address set to 10.0.0.4
packet = IP(dst="10.0.0.4")

# Send the packet
send(packet)