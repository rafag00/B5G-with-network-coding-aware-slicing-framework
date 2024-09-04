#!/usr/bin/env python

# This script will analyze SW1 and SW4
# Then calculate the average delay, jitter and the total packet loss

from scapy.all import rdpcap
from scapy.layers.inet import IP

# Read the pcap files
packetsSW1 = rdpcap('outputSW1.pcap')
packetsSW4 = rdpcap('outputSW4.pcap')

# Initialize the dictionary to store times and differences
latency_dict_drone = {} # packet_id -> [SW1 time, SW4 time, delay]
latency_dict_control = {} # packet_id -> [SW1 time, SW4 time, delay]

# Process SW1 packets to fill the latency_dict_drone
for packetSW1 in packetsSW1:
    if IP in packetSW1 and packetSW1[IP].src == '10.10.10.3':
        lastSW1 = packetSW1.lastlayer()
        decoded_loadSW1 = lastSW1.load.decode()
        split_loadSW1 = decoded_loadSW1.split('-')

        packet_id = split_loadSW1[0]
        if packet_id not in latency_dict_drone:
            latency_dict_drone[packet_id] = [None, None, None]  # Add fields for size and timestamp
        latency_dict_drone[packet_id][0] = packetSW1.time
        
# Process SW4 packets to fill the latency_dict_drone
for packetSW4 in packetsSW4:
    if IP in packetSW4 and packetSW4[IP].src == '10.10.10.3':
        lastSW4 = packetSW4.lastlayer()
        decoded_loadSW4 = lastSW4.load.decode()
        split_loadSW4 = decoded_loadSW4.split('-')

        packet_id = split_loadSW4[0]
        if packet_id not in latency_dict_drone:
            latency_dict_drone[packet_id] = [None, None, None]
        latency_dict_drone[packet_id][1] = packetSW4.time

# Calculate the differences for downlink (latency)
for packet_id in latency_dict_drone:
    if latency_dict_drone[packet_id][0] is not None and latency_dict_drone[packet_id][1] is not None:
        latency_dict_drone[packet_id][2] = latency_dict_drone[packet_id][1] - latency_dict_drone[packet_id][0] # SW4 - SW1

# Initialize variables for jitter calculation
previous_latency = None
jitter_dict_drone = {}

for packet_id in sorted(latency_dict_drone.keys(), key=int):
    if latency_dict_drone[packet_id][2] is not None:
        current_latency = latency_dict_drone[packet_id][2]
        if previous_latency is not None:
            jitter = abs(current_latency - previous_latency)
            jitter_dict_drone[packet_id] = jitter
        previous_latency = current_latency
        
# Calculate total latency
total_latency_drone = sum([delay[2] for delay in latency_dict_drone.values() if delay[2] is not None])
latency_count_drone = len([delay[2] for delay in latency_dict_drone.values() if delay[2] is not None])

# Calculate total jitter
total_jitter_drone = sum(jitter_dict_drone.values())
jitter_count_drone = len(jitter_dict_drone) 

# Calculate averages
avg_latency_drone = total_latency_drone / latency_count_drone if latency_count_drone > 0 else None
avg_jitter_drone = total_jitter_drone / jitter_count_drone if jitter_count_drone > 0 else None

# Calculate packet loss
total_packets_sent_drone = len([pkt for pkt in latency_dict_drone.values() if pkt[0] is not None])
total_packets_received_drone = len([pkt for pkt in latency_dict_drone.values() if pkt[1] is not None])
packet_loss_count_drone = total_packets_sent_drone - total_packets_received_drone
packet_loss_percentage_drone = (packet_loss_count_drone / total_packets_sent_drone) * 100 if total_packets_sent_drone > 0 else None


# Process SW1 packets to fill the latency_dict_control
for packetSW1 in packetsSW1:
    if IP in packetSW1 and packetSW1[IP].src == '10.10.10.4':
        lastSW1 = packetSW1.lastlayer()
        decoded_loadSW1 = lastSW1.load.decode()
        split_loadSW1 = decoded_loadSW1.split('-')

        if len(split_loadSW1) > 2 and split_loadSW1[1] == 'uRLLC':
            packet_id = split_loadSW1[2]
            if packet_id not in latency_dict_control:
                latency_dict_control[packet_id] = [None, None, None]  # Add fields for size and timestamp
            latency_dict_control[packet_id][0] = packetSW1.time
            
# Process SW4 packets to fill the latency_dict_control
for packetSW4 in packetsSW4:
    if IP in packetSW4 and packetSW4[IP].src == '10.10.10.4':
        lastSW4 = packetSW4.lastlayer()
        decoded_loadSW4 = lastSW4.load.decode()
        split_loadSW4 = decoded_loadSW4.split('-')

        if len(split_loadSW4) > 2 and split_loadSW4[1] == 'uRLLC':
            packet_id = split_loadSW4[2]
            if packet_id not in latency_dict_control:
                latency_dict_control[packet_id] = [None, None, None]
            latency_dict_control[packet_id][1] = packetSW4.time 
            
# Calculate the differences for downlink (latency)
for packet_id in latency_dict_control:
    if latency_dict_control[packet_id][0] is not None and latency_dict_control[packet_id][1] is not None:
        latency_dict_control[packet_id][2] = latency_dict_control[packet_id][0] - latency_dict_control[packet_id][1] # SW1 - SW4
               
# Initialize variables for jitter calculation
previous_latency = None
jitter_dict_control = {}

for packet_id in sorted(latency_dict_control.keys(), key=int):
    if latency_dict_control[packet_id][2] is not None:
        current_latency = latency_dict_control[packet_id][2]
        if previous_latency is not None:
            jitter = abs(current_latency - previous_latency)
            jitter_dict_control[packet_id] = jitter
        previous_latency = current_latency
        
# Calculate total latency
total_latency_control = sum([delay[2] for delay in latency_dict_control.values() if delay[2] is not None])
latency_count_control = len([delay[2] for delay in latency_dict_control.values() if delay[2] is not None])

# Calculate total jitter
total_jitter_control = sum(jitter_dict_control.values())
jitter_count_control = len(jitter_dict_control)  

# Calculate averages
avg_latency_control = total_latency_control / latency_count_control if latency_count_control > 0 else None
avg_jitter_control = total_jitter_control / jitter_count_control if jitter_count_control > 0 else None

# Calculate packet loss
total_packets_sent_control = len([pkt for pkt in latency_dict_control.values() if pkt[1] is not None])
total_packets_received_control = len([pkt for pkt in latency_dict_control.values() if pkt[0] is not None])
packet_loss_count_control = total_packets_sent_control - total_packets_received_control
packet_loss_percentage_control = abs(packet_loss_count_control / total_packets_sent_control) * 100 if total_packets_sent_control > 0 else None

print(f'\nAverage Latency Drone: {avg_latency_drone}')
print(f'Average Latency Control: {avg_latency_control}')
print(f'Average Jitter Drone: {avg_jitter_drone}')
print(f'Average Jitter Control: {avg_jitter_control}')
print(f'Total packets sent Drone: {total_packets_sent_drone}')
print(f'Total packets received Drone: {total_packets_received_drone}')
print(f'Total packets sent Control: {total_packets_sent_control}')
print(f'Total packets received Control: {total_packets_received_control}')
print(f'Packet Loss Percentage Drone: {packet_loss_percentage_drone}%')
print(f'Packet Loss Percentage Control: {packet_loss_percentage_control}%')