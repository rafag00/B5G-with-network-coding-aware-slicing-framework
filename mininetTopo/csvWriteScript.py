#!/usr/bin/env python

import csv
import json
from scapy.all import rdpcap
from scapy.layers.inet import IP
import numpy as np

testComb = [['eMBB', 'mMTC'],['eMBB', 'mMTC'],
            ['uRLLC','eMBB'],['uRLLC','eMBB'],
            ['uRLLC','mMTC'],['uRLLC','mMTC'],
            ['uLBC','mMTC'],['uLBC','mMTC'],
            ['eMBB','mMTC'],['eMBB','mMTC'],
            ['uRLLC','eMBB'],['uRLLC','eMBB'],
            ['uRLLC','mMTC'],['uRLLC','mMTC'],
            ['uLBC','mMTC'],['uLBC','mMTC'],
            ['eMBB','mMTC'],['eMBB','mMTC'],
            ['uRLLC','eMBB'],['uRLLC','eMBB'],
            ['uRLLC','mMTC'],['uRLLC','mMTC'],
            ['uLBC','mMTC'],['uLBC','mMTC']]

class analyzeReturn:
    def __init__(self, latency, jitter, loss_prob, latency_prog, jitter_prog):
        self.latency = latency
        self.jitter = jitter
        self.loss_prob = loss_prob
        self.latency_prog = latency_prog
        self.jitter_prog = jitter_prog

class ioStatsReturn:
    def __init__(self, avg_bw, bw_prog):
        self.avg_bw = avg_bw
        self.bw_prog = bw_prog

def analyzeDrone(path1, path2):
    # Read the pcap files
    packetsSW1 = rdpcap(path1)
    packetsSW4 = rdpcap(path2)
    
    # Initialize the dictionary to store times and differences
    latency_dict = {} # packet_id -> [SW1 time, SW4 time, delay]

    # Process SW1 packets to fill the latency_dict
    for packetSW1 in packetsSW1:
        if IP in packetSW1 and packetSW1[IP].src == '10.10.10.1':
            lastSW1 = packetSW1.lastlayer()
            decoded_loadSW1 = lastSW1.load.decode()
            split_loadSW1 = decoded_loadSW1.split('-')

            packet_id = split_loadSW1[0]
            if packet_id not in latency_dict:
                latency_dict[packet_id] = [None, None, None]  # Add fields for size and timestamp
            latency_dict[packet_id][0] = packetSW1.time

    # Process SW4 packets
    for packetSW4 in packetsSW4:
        if IP in packetSW4 and packetSW4[IP].src == '10.10.10.1':
            lastSW4 = packetSW4.lastlayer()
            decoded_loadSW4 = lastSW4.load.decode()
            split_loadSW4 = decoded_loadSW4.split('-')

            packet_id = split_loadSW4[0]
            if packet_id not in latency_dict:
                latency_dict[packet_id] = [None, None, None]
            latency_dict[packet_id][1] = packetSW4.time
        

    # Calculate the differences for uplink (latency)
    for packet_id in latency_dict:
        if latency_dict[packet_id][0] is not None and latency_dict[packet_id][1] is not None:
            latency_dict[packet_id][2] = latency_dict[packet_id][1] - latency_dict[packet_id][0] # SW4 - SW1

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
    total_packets_sent = len([pkt for pkt in latency_dict.values() if pkt[0] is not None])
    total_packets_received = len([pkt for pkt in latency_dict.values() if pkt[1] is not None])
    packet_loss_count = total_packets_sent - total_packets_received
    packet_loss_percentage = abs(packet_loss_count / total_packets_sent) * 100 if total_packets_sent > 0 else None

    return analyzeReturn(avg_latency, avg_jitter, packet_loss_percentage, latency_dict, jitter_dict)

def analyzeDevices(path1, path2):
    packetsSW1 = rdpcap(path1)
    packetsSW4 = rdpcap(path2)

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

    return analyzeReturn(avg_latency, avg_jitter, packet_loss_percentage, latency_dict, jitter_dict)

def analyzeControl(path1, path2):
    # Read the pcap files
    packetsSW1 = rdpcap(path1)
    packetsSW4 = rdpcap(path2)

    # Initialize the dictionary to store times and differences
    latency_dict = {} # packet_id -> [SW1 time, SW4 time, delay]

    # Process SW1 packets to fill the latency_dict
    for packetSW1 in packetsSW1:
        if IP in packetSW1 and packetSW1[IP].src == '10.10.10.2':
            lastSW1 = packetSW1.lastlayer()
            decoded_loadSW1 = lastSW1.load.decode()
            split_loadSW1 = decoded_loadSW1.split('-')

            if len(split_loadSW1) > 2 and split_loadSW1[1] == 'uRLLC':
                packet_id = split_loadSW1[2]
                if packet_id not in latency_dict:
                    latency_dict[packet_id] = [None, None, None]  # Add fields for size and timestamp
                latency_dict[packet_id][0] = packetSW1.time

    # Process SW4 packets
    for packetSW4 in packetsSW4:
        if IP in packetSW4 and packetSW4[IP].src == '10.10.10.2':
            lastSW4 = packetSW4.lastlayer()
            decoded_loadSW4 = lastSW4.load.decode()
            split_loadSW4 = decoded_loadSW4.split('-')

            if len(split_loadSW4) > 2 and split_loadSW4[1] == 'uRLLC':
                packet_id = split_loadSW4[2]
                if packet_id not in latency_dict:
                    latency_dict[packet_id] = [None, None, None]
                latency_dict[packet_id][1] = packetSW4.time      

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
    packet_loss_percentage = abs(packet_loss_count / total_packets_sent) * 100 if total_packets_sent > 0 else None

    return analyzeReturn(avg_latency, avg_jitter, packet_loss_percentage, latency_dict, jitter_dict)

def analyzeCombined(path1, path2):
    packetsSW1 = rdpcap(path1)
    packetsSW4 = rdpcap(path2)

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

    dronReturn = analyzeReturn(avg_latency_drone, avg_jitter_drone, packet_loss_percentage_drone, latency_dict_drone, jitter_dict_drone)
    controlReturn = analyzeReturn(avg_latency_control, avg_jitter_control, packet_loss_percentage_control, latency_dict_control, jitter_dict_control)
    
    return dronReturn, controlReturn

def analyzeIoStats(path):
    io_dict = {}
    
    with open(path, 'r') as file:
        lines = file.readlines()
    
    # Find the starting point of the interval data
    start_index = None
    for i, line in enumerate(lines):
        if line.strip().startswith("| Interval | Frames |  Bytes  |") or line.strip().startswith("| Interval   | Frames |  Bytes  |") or line.strip().startswith("| Interval | Frames | Bytes | |") or line.strip().startswith("| Interval   | Frames | Bytes |") or line.strip().startswith("| Interval   | Frames |  Bytes |") or line.strip().startswith("| Interval | Frames |  Bytes |"):
            start_index = i + 1
            break
    
    if start_index is None:
        raise ValueError("Interval data not found in the file")
    
    # Process the interval data
    for line in lines[start_index:]:
        if line.strip().startswith("|===========================|") or line.strip().startswith("================================="):
            break
        
        parts = line.split('|')
        if len(parts) < 4:
            continue
        
        interval = parts[1].strip()
        bytes_str = parts[3].strip()
        
        if '<>' in interval:
            start_sec = interval.split('<>')[0].strip()
            if start_sec.isdigit():
                start_sec = int(start_sec)
            else:
                continue
        
        if bytes_str.isdigit():
            bytes_val = int(bytes_str)
        else:
            continue
        
        io_dict[start_sec] = (bytes_val*8) # Convert bytes to bits
    
    values_bits = np.fromiter(io_dict.values(), dtype=np.double)
    average_bits = np.mean(values_bits)
    
    return ioStatsReturn(average_bits, io_dict)

def writeCSV(analyze, ioStatsSW1, ioStatsSW4, testId, runNum, trafficType, csvFileTest):
    if trafficType == 'mMTC':
        with open('trafficData.csv', 'a', newline='') as csvfile:
            csvwriter = csv.writer(csvfile)
            row = [testId, runNum, trafficType, None, None, analyze.latency, analyze.jitter, analyze.loss_prob, None, None, json.dumps(analyze.latency_prog), json.dumps(analyze.jitter_prog)]
            csvwriter.writerow(row)
        with open(csvFileTest, 'a', newline='') as csvfile:
            csvwriter = csv.writer(csvfile)
            row = [testId, runNum, trafficType, None, None, analyze.latency, analyze.jitter, analyze.loss_prob, None, None, json.dumps(analyze.latency_prog), json.dumps(analyze.jitter_prog)]
            csvwriter.writerow(row)
    elif trafficType == 'uRLLC':
        with open('trafficData.csv', 'a', newline='') as csvfile:
            csvwriter = csv.writer(csvfile)
            row = [testId, runNum, trafficType, ioStatsSW4.avg_bw, ioStatsSW1.avg_bw, analyze.latency, analyze.jitter, analyze.loss_prob, json.dumps(ioStatsSW4.bw_prog), json.dumps(ioStatsSW.bw_prog), json.dumps(analyze.latency_prog), json.dumps(analyze.jitter_prog)]
            csvwriter.writerow(row)
        with open(csvFileTest, 'a', newline='') as csvfile:
            csvwriter = csv.writer(csvfile)
            row = [testId, runNum, trafficType, ioStatsSW4.avg_bw, ioStatsSW1.avg_bw, analyze.latency, analyze.jitter, analyze.loss_prob, json.dumps(ioStatsSW4.bw_prog), json.dumps(ioStatsSW.bw_prog), json.dumps(analyze.latency_prog), json.dumps(analyze.jitter_prog)]
            csvwriter.writerow(row)
    else:
        with open('trafficData.csv', 'a', newline='') as csvfile:
            csvwriter = csv.writer(csvfile)
            row = [testId, runNum, trafficType, ioStatsSW1.avg_bw, ioStatsSW4.avg_bw, analyze.latency, analyze.jitter, analyze.loss_prob, json.dumps(ioStatsSW1.bw_prog), json.dumps(ioStatsSW4.bw_prog), json.dumps(analyze.latency_prog), json.dumps(analyze.jitter_prog)]
            csvwriter.writerow(row)
        with open(csvFileTest, 'a', newline='') as csvfile:
            csvwriter = csv.writer(csvfile)
            row = [testId, runNum, trafficType, ioStatsSW1.avg_bw, ioStatsSW4.avg_bw, analyze.latency, analyze.jitter, analyze.loss_prob, json.dumps(ioStatsSW1.bw_prog), json.dumps(ioStatsSW4.bw_prog), json.dumps(analyze.latency_prog), json.dumps(analyze.jitter_prog)]
            csvwriter.writerow(row)
        
def writeCSVCombined(analyzeDrone, analyzeControl, ioStatsSW1Drone, ioStatsSW4Drone, ioStatsSW1Combined, ioStatsSW4Combined, testId, runNum, csvFileTest):
    with open('trafficData.csv', 'a', newline='') as csvfile:
        csvwriter = csv.writer(csvfile)
        row = [testId, runNum, 'uLBC-Drone', ioStatsSW1Drone.avg_bw, ioStatsSW4Drone.avg_bw, analyzeDrone.latency, analyzeDrone.jitter, analyzeDrone.loss_prob, json.dumps(ioStatsSW1Drone.bw_prog), json.dumps(ioStatsSW4Drone.bw_prog), json.dumps(analyzeDrone.latency_prog), json.dumps(analyzeDrone.jitter_prog)]
        csvwriter.writerow(row)
        row = [testId, runNum, 'uLBC-Control', ioStatsSW4Combined.avg_bw, ioStatsSW1Combined.avg_bw, analyzeControl.latency, analyzeControl.jitter, analyzeControl.loss_prob, json.dumps(ioStatsSW4Combined.bw_prog), json.dumps(ioStatsSW1Combined.bw_prog), json.dumps(analyzeControl.latency_prog), json.dumps(analyzeControl.jitter_prog)]
        csvwriter.writerow(row)
    with open(csvFileTest, 'a', newline='') as csvfile:
        csvwriter = csv.writer(csvfile)
        row = [testId, runNum, 'uLBC-Drone', ioStatsSW1Drone.avg_bw, ioStatsSW4Drone.avg_bw, analyzeDrone.latency, analyzeDrone.jitter, analyzeDrone.loss_prob, json.dumps(ioStatsSW1Drone.bw_prog), json.dumps(ioStatsSW4Drone.bw_prog), json.dumps(analyzeDrone.latency_prog), json.dumps(analyzeDrone.jitter_prog)]
        csvwriter.writerow(row)
        row = [testId, runNum, 'uLBC-Control', ioStatsSW4Combined.avg_bw, ioStatsSW1Combined.avg_bw, analyzeControl.latency, analyzeControl.jitter, analyzeControl.loss_prob, json.dumps(ioStatsSW4Combined.bw_prog), json.dumps(ioStatsSW1Combined.bw_prog), json.dumps(analyzeControl.latency_prog), json.dumps(analyzeControl.jitter_prog)]
        csvwriter.writerow(row)
    
def percTest():
    for i in range(1,25):
        directory = '/home/Tests/'+str(i)+'/'
        csvFileTest = directory+'trafficData_'+str(i)+'.csv'
        
        with open(csvFileTest, 'w', newline='') as csvfile:
            csvwriter = csv.writer(csvfile)
            header = ['run_type', 'run_num', 'traffic_type', 'avg_bw_src', 'avg_bw_dst', 'latency', 'jitter', 'loss_prob', 'bw_prog_src', 'bw_prog_dst', 'latency_prog', 'jitter_prog']
            csvwriter.writerow(header)
            
        for j in range(1,31):
            fileOutput1 = directory+'outputSW1_'+str(j)+'.pcap'
            fileOutput2 = directory+'outputSW4_'+str(j)+'.pcap'

            for k in testComb[i-1]:
                if k == 'eMBB':
                    resultAnalyze = analyzeDrone(fileOutput1, fileOutput2)
                    print('Data for test '+str(i)+' run '+str(j)+' traffic type '+k+' SW1')
                    resultIoStatsSW1 = analyzeIoStats(directory+'io_stats_SW1_D_'+str(j)+'.txt')
                    print('Data for test '+str(i)+' run '+str(j)+' traffic type '+k+' SW4')
                    resultIoStatsSW4 = analyzeIoStats(directory+'io_stats_SW4_D_'+str(j)+'.txt')
                    writeCSV(resultAnalyze, resultIoStatsSW1, resultIoStatsSW4, i, j, k, csvFileTest)
                elif k == 'mMTC':
                    resultAnalyze = analyzeDevices(fileOutput1, fileOutput2)
                    resultIoStatsSW1 = None
                    resultIoStatsSW4 = None
                    writeCSV(resultAnalyze, resultIoStatsSW1, resultIoStatsSW4, i, j, k, csvFileTest)
                elif k == 'uRLLC':
                    resultAnalyze = analyzeControl(fileOutput1, fileOutput2)
                    print('Data for test '+str(i)+' run '+str(j)+' traffic type '+k+' SW1')
                    resultIoStatsSW1 = analyzeIoStats(directory+'io_stats_SW1_C_'+str(j)+'.txt')
                    print('Data for test '+str(i)+' run '+str(j)+' traffic type '+k+' SW4')
                    resultIoStatsSW4 = analyzeIoStats(directory+'io_stats_SW4_C_'+str(j)+'.txt')
                    writeCSV(resultAnalyze, resultIoStatsSW1, resultIoStatsSW4, i, j, k, csvFileTest)
                elif k == 'uLBC':
                    resultAnalyzeDrone, resultAnalyzeControl = analyzeCombined(fileOutput1, fileOutput2)
                    print('Data for test '+str(i)+' run '+str(j)+' traffic type '+k+' SW1 Drone')
                    resultIoStatsSW1Drone = analyzeIoStats(directory+'io_stats_SW1_D_'+str(j)+'.txt')
                    print('Data for test '+str(i)+' run '+str(j)+' traffic type '+k+' SW4 Drone')
                    resultIoStatsSW4Drone = analyzeIoStats(directory+'io_stats_SW4_D_'+str(j)+'.txt')
                    print('Data for test '+str(i)+' run '+str(j)+' traffic type '+k+' SW1 Control')
                    resultIoStatsSW1Combined = analyzeIoStats(directory+'io_stats_SW1_C_'+str(j)+'.txt')
                    print('Data for test '+str(i)+' run '+str(j)+' traffic type '+k+' SW4 Control')
                    resultIoStatsSW4Combined = analyzeIoStats(directory+'io_stats_SW4_C_'+str(j)+'.txt')
                    writeCSVCombined(resultAnalyzeDrone, resultAnalyzeControl, resultIoStatsSW1Drone, resultIoStatsSW4Drone, resultIoStatsSW1Combined, resultIoStatsSW4Combined, i, j, csvFileTest)
            
if __name__ == '__main__': 
    with open('trafficData.csv', 'w', newline='') as csvfile:
        csvwriter = csv.writer(csvfile)
        header = ['run_type', 'run_num', 'traffic_type', 'avg_bw_src', 'avg_bw_dst', 'latency', 'jitter', 'loss_prob', 'bw_prog_src', 'bw_prog_dst', 'latency_prog', 'jitter_prog']
        csvwriter.writerow(header)
    
    percTest()