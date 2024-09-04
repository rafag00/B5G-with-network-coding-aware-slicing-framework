#!/usr/bin/env python

from time import sleep
import requests
import random


if __name__ == '__main__':
    input("Press Enter to start the random generation of the link properties...")
    
    try:
        print("Starting the random generation of the link properties")
        while True:
            try:
                data = [{"sw_id_src": "of:0000000000000001","sw_id_dst": "of:0000000000000002","port_numb_src": 3,"port_numb_dst": 1,"bandwith": 8000000,
                    "latency": random.randint(15, 39),"jitter": random.randint(10, 31),"loss_prob": random.randint(5, 19),"energy_consumption": 138}
                    ,{"sw_id_src": "of:0000000000000002","sw_id_dst": "of:0000000000000001","port_numb_src": 1,"port_numb_dst": 3,"bandwith": 8000000,
                    "latency": random.randint(15, 39),"jitter": random.randint(10, 31),"loss_prob": random.randint(5, 19),"energy_consumption": 138}
                    ,{"sw_id_src": "of:0000000000000001","sw_id_dst": "of:0000000000000003","port_numb_src": 2,"port_numb_dst": 1,"bandwith": 50000000,
                    "latency": random.randint(1, 4),"jitter": random.randint(1, 3),"loss_prob": random.choice([0.00001,0.0001,0.001,0.01,0.1,1]),"energy_consumption": 658}
                    ,{"sw_id_src": "of:0000000000000003","sw_id_dst": "of:0000000000000001","port_numb_src": 1,"port_numb_dst": 2,"bandwith": 50000000,
                    "latency": random.randint(1, 4),"jitter": random.randint(1, 3),"loss_prob": random.choice([0.00001,0.0001,0.001,0.01,0.1,1]),"energy_consumption": 658}
                    ,{"sw_id_src": "of:0000000000000002","sw_id_dst": "of:0000000000000003","port_numb_src": 2,"port_numb_dst": 3,"bandwith": 8000000,
                    "latency": random.randint(15, 39),"jitter": random.randint(10, 31),"loss_prob": random.randint(5, 19),"energy_consumption": 138}
                    ,{"sw_id_src": "of:0000000000000003","sw_id_dst": "of:0000000000000002","port_numb_src": 3,"port_numb_dst": 2,"bandwith": 8000000,
                    "latency": random.randint(15, 39),"jitter": random.randint(10, 31),"loss_prob": random.randint(5, 19),"energy_consumption": 138},]
                
                response = requests.post('http://172.17.0.4:8080/links', json=data)

                # Check the response status code
                if response.status_code != 200:
                    print(f'API request failed with status code: {response.status_code} and response: {response.text}')
                    break
            except Exception as e:
                print(f'API request failed with error: {e}')
                break
            
            sleep(10)
    except KeyboardInterrupt:
        print("Stopping the random generation of the link properties")