#!/usr/bin/env python

# This script will analyze SW1 and SW4
# Then calculate the average delay and jitter
# And the bandwidth usage
# And the total packet loss

from scapy.all import rdpcap
from scapy.layers.inet import IP

# Read the pcap files
packetsSW1 = rdpcap('outputSW1.pcap')
packetsSW4 = rdpcap('outputSW4.pcap')

# Initialize the dictionary to store times and differences
latency_dict = {} # source -> [packet_id -> [SW1 time, SW4 time, delay]]
devices_ips = ['192.136.10.'+str(i) for i in range(1, 255)]

# Process SW1 packets to fill the latency_dict
for packetSW1 in packetsSW1:
    if IP in packetSW1 and packetSW1[IP].src in devices_ips:
        lastSW1 = packetSW1.lastlayer()
        decoded_loadSW1 = lastSW1.load.decode()
        split_loadSW1 = decoded_loadSW1.split('-')
        
        if len(split_loadSW1) > 2 and split_loadSW1[1] == 'mMTC':
            packet_id = split_loadSW1[2]
            if packetSW1[IP].src not in latency_dict:
                latency_dict[packetSW1[IP].src] = {}
            if packet_id not in latency_dict[packetSW1[IP].src]:
                latency_dict[packetSW1[IP].src][packet_id] = [None, None, None]  # Add fields for size and timestamp
            latency_dict[packetSW1[IP].src][packet_id][0] = packetSW1.time

# Process SW4 packets
for packetSW4 in packetsSW4:
    if IP in packetSW4 and packetSW4[IP].src in devices_ips:
        lastSW4 = packetSW4.lastlayer()
        decoded_loadSW4 = lastSW4.load.decode()
        split_loadSW4 = decoded_loadSW4.split('-')

        if len(split_loadSW4) > 2 and split_loadSW4[1] == 'mMTC':
            packet_id = split_loadSW4[2]
            if packetSW4[IP].src not in latency_dict:
                latency_dict[packetSW4[IP].src] = {}
            if packet_id not in latency_dict[packetSW4[IP].src]:
                latency_dict[packetSW4[IP].src][packet_id] = [None, None, None]
            latency_dict[packetSW4[IP].src][packet_id][1] = packetSW4.time

# Calculate the differences for uplink (latency)
for device in latency_dict:
    for packet_id in latency_dict[device]:
        if latency_dict[device][packet_id][0] is not None and latency_dict[device][packet_id][1] is not None:
            latency_dict[device][packet_id][2] = latency_dict[device][packet_id][1] - latency_dict[device][packet_id][0] # SW4 - SW1

# Initialize variables for jitter calculation
previous_latency = None
jitter_dict = {}

for device in latency_dict:
    for packet_id in sorted(latency_dict[device].keys(), key=int):
        if latency_dict[device][packet_id][2] is not None:
            current_latency = latency_dict[device][packet_id][2]
            if previous_latency is not None:
                jitter = abs(current_latency - previous_latency)
                if device not in jitter_dict:
                    jitter_dict[device] = {}
                jitter_dict[device][packet_id] = jitter
            previous_latency = current_latency

total_latency_device = {}
latency_count_device = {}
          
# Calculate total latency per device
for device in latency_dict:
    # Calculate total latency
    total_latency_device[device] = sum([delay[2] for delay in latency_dict[device].values() if delay[2] is not None])
    latency_count_device[device] = len([delay[2] for delay in latency_dict[device].values() if delay[2] is not None])

total_jitter_device = {}
jitter_count_device = {}

# Calculate total jitter
for device in jitter_dict:
    total_jitter_device[device] = sum(jitter_dict[device].values())
    jitter_count_device[device] = len(jitter_dict[device])  
    
# Calculate averages
avg_latency_device = {}
avg_jitter_device = {}

for device in devices_ips:
    avg_latency_device[device] = total_latency_device[device] / latency_count_device[device] if latency_count_device[device] > 0 else None
    avg_jitter_device[device] = total_jitter_device[device] / jitter_count_device[device] if jitter_count_device[device] > 0 else None

avg_latency = sum([avg_latency_device[device] for device in avg_latency_device]) / len(avg_latency_device)
avg_jitter = sum([avg_jitter_device[device] for device in avg_jitter_device]) / len(avg_jitter_device)

# Calculate packet loss
total_packets_sent_device = {}
total_packets_received_device = {}
packet_loss_count_device = {}
packet_loss_percentage_device = {}

for device in latency_dict:
    total_packets_sent_device[device] = len([pkt for pkt in latency_dict[device].values() if pkt[0] is not None])
    total_packets_received_device[device] = len([pkt for pkt in latency_dict[device].values() if pkt[1] is not None])
    packet_loss_count_device[device] = total_packets_sent_device[device] - total_packets_received_device[device]
    packet_loss_percentage_device[device] = (packet_loss_count_device[device] / total_packets_sent_device[device]) * 100 if total_packets_sent_device[device] > 0 else None

total_packets_sent = sum([total_packets_sent_device[device] for device in total_packets_sent_device])
total_packets_received = sum([total_packets_received_device[device] for device in total_packets_received_device])
packet_loss_count = total_packets_sent - total_packets_received
packet_loss_percentage = abs(packet_loss_count / total_packets_sent) * 100 if total_packets_sent > 0 else None

print("Average latency: ", avg_latency)
print("Average jitter: ", avg_jitter)
print("Total packet sent: ", total_packets_sent)
print("Total packet received: ", total_packets_received)
print("Total packet loss: ", packet_loss_percentage)