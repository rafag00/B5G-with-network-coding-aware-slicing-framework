#!/usr/bin/env python

# This script will analyze SW1 and SW4
# Then calculate the average delay and jitter
# And the bandwidth usage
# And the total packet loss

from scapy.all import rdpcap

# Read the pcap files
packetsSW1 = rdpcap('outputSW1.pcap')
packetsSW4 = rdpcap('outputSW4.pcap')

# Initialize the dictionary to store times and differences
latency_dict = {} # packet_id -> [SW1 time, SW4 time, delay, size, jitter]

# Process SW1 packets to fill the latency_dict
for packetSW1 in packetsSW1:
    lastSW1 = packetSW1.lastlayer()
    decoded_loadSW1 = lastSW1.load.decode()
    split_loadSW1 = decoded_loadSW1.split('-')

    if len(split_loadSW1) > 2 and split_loadSW1[1] == 'eMBB':
        packet_id = split_loadSW1[2]
        if packet_id not in latency_dict:
            latency_dict[packet_id] = [None, None, None, None, None]  # Add fields for size and timestamp
        latency_dict[packet_id][0] = packetSW1.time
        latency_dict[packet_id][3] = len(packetSW1)  # Store packet size
        
# Process SW4 packets
for packetSW4 in packetsSW4:
    lastSW4 = packetSW4.lastlayer()
    decoded_loadSW4 = lastSW4.load.decode()
    split_loadSW4 = decoded_loadSW4.split('-')

    if len(split_loadSW4) > 2 and split_loadSW4[1] == 'eMBB':
        packet_id = split_loadSW4[2]
        if packet_id not in latency_dict:
            latency_dict[packet_id] = [None, None, None, None, None]
        latency_dict[packet_id][1] = packetSW4.time
        latency_dict[packet_id][4] = len(packetSW4)  # Store packet size
        
# Calculate the differences for downlink (latency)
for packet_id in latency_dict:
    if latency_dict[packet_id][0] is not None and latency_dict[packet_id][1] is not None:
        latency_dict[packet_id][2] = latency_dict[packet_id][0] - latency_dict[packet_id][1] # SW1 - SW4
        
# Initialize variables for jitter calculation
previous_latency = None
jitter_dict = {}

for packet_id in sorted(latency_dict.keys(), key=int):
    if latency_dict[packet_id][2] is not None:
        current_latency = latency_dict[packet_id][2]
        if previous_latency is not None:
            jitter = abs(current_latency - previous_latency)
            jitter_dict[packet_id] = jitter
        previous_latency = current_latency

# Calculate total latency
total_latency = sum([delay[2] for delay in latency_dict.values() if delay[2] is not None])
latency_count = len([delay[2] for delay in latency_dict.values() if delay[2] is not None])

# Calculate total jitter
total_jitter = sum(jitter_dict.values())
jitter_count = len(jitter_dict)    
        
# Calculate averages
avg_latency = total_latency / latency_count if latency_count > 0 else None
avg_jitter = total_jitter / jitter_count if jitter_count > 0 else None

# Calculate packet loss
total_packets_sent = len([pkt for pkt in latency_dict.values() if pkt[1] is not None])
total_packets_received = len([pkt for pkt in latency_dict.values() if pkt[0] is not None])
packet_loss_count = total_packets_sent - total_packets_received
packet_loss_percentage = (packet_loss_count / total_packets_sent) * 100 if total_packets_sent > 0 else None

# Print the results
for packet_id, times in latency_dict.items():
    jitter = jitter_dict.get(packet_id, None)
    print(f'ID: {packet_id} | SW1 Time: {times[0]} | SW4 Time: {times[1]} | Latency: {times[2]} | Jitter: {jitter}')
    
print(f'\nAverage Latency: {avg_latency}')
print(f'Average Jitter: {avg_jitter}')
print(f'Packet Loss Percentage: {packet_loss_percentage}%')