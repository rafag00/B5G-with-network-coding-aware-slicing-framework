#!/usr/bin/env python

import requests
from mininet.net import Mininet
from mininet.node import RemoteController, OVSKernelSwitch
from mininet.cli import CLI
from mininet.log import setLogLevel, info
from mininet.link import TCLink

def basicTopo():

  #Just a simple topology with 4 hosts and 4 switches

  net = Mininet(switch=OVSKernelSwitch, link=TCLink, autoSetMacs=True)

  info('*** Adding controller ***\n')
  net.addController(RemoteController( 'c0', ip='172.17.0.3', port=6653))

  info('*** Adding hosts ***\n')
  h1 = net.addHost( 'h1')
  h2 = net.addHost( 'h2')
  #h3 = net.addHost( 'h3')
  #h4 = net.addHost( 'h4')

  info('*** Adding switch ***\n')
  s1 = net.addSwitch('s1', protocols='OpenFlow14')
  s2 = net.addSwitch('s2', protocols='OpenFlow14')
  s3 = net.addSwitch('s3', protocols='OpenFlow14')
  s4 = net.addSwitch('s4', protocols='OpenFlow14')
  

  info('*** Creating links ***\n')
  net.addLink(h1,s1,port1=0,port2=1)
  net.addLink(s1,s2,port1=3,port2=1, bw=50, loss=0.00001)
  net.addLink(s1,s3,port1=4,port2=1, bw=8, delay='25ms', loss=5, jitter='15ms')
  net.addLink(s2,s4,port1=2,port2=3, bw=50, loss=0.00001)
  net.addLink(s3,s4,port1=2,port2=4, bw=8, delay='25ms', loss=5, jitter='15ms')
  net.addLink(h2,s4,port1=0,port2=1)
  
  return net

if __name__ == '__main__':
  setLogLevel( 'info' )
  
  aux = input("Want to add network slices to the topology? (Y/N)")

  if aux == 'Y' or aux == 'y':
    try:
      data = [{
          "_5qi_levels": [
            7,
            8,
            70,
            79
          ],
          "active": True,
          "downlink_user_throughput_bps_ue": 20000000,
          "dscp_levels": [
            18,
            20,
            22
          ],
          "e2e_latency_ms": 10,
          "jitter": 13,
          "loss_probability": 0.1,
          "service_slice_type_sst": "eMBB",
          "uplink_user_throughput_bps_ue": 10000000,
          "use_case_type": "This network slice is used for a drone uplink video stream"
        },
          {
            "_5qi_levels": [
              67,
              69,
              2
            ],
            "active": True,
            "downlink_user_throughput_bps_ue": 3000000,
            "dscp_levels": [
              34,
              36,
              38
            ],
            "e2e_latency_ms": 1,
            "jitter": 3,
            "loss_probability": 0.00001,
            "service_slice_type_sst": "uRLLC",
            "uplink_user_throughput_bps_ue": 3000000,
            "use_case_type": "This network slice is for downlink remote control of the video streaming drone"
          }
      ]

      response = requests.post('http://172.17.0.4:8080/slice', json=data)
      
      # Check the response status code
      if response.status_code == 200:
          info('Slices posted successfully!\n')
      else:
          info(f'API request failed with status code: {response.status_code} and response: {response.text}')
    except Exception as e:
      info(f'API request failed with error: {e}')
      
  net = basicTopo()
  
  info('*** Starting network ***\n')
  net.start()
  
  info('*** Discovering hosts ***\n')
  net.pingAll()
  
  input("Press Enter to continue... Be sure you snapshoted the network!!")
    
  try:
    data = [{"sw_id_src": "of:0000000000000001","sw_id_dst": "of:0000000000000002","port_numb_src": 3,"port_numb_dst": 1,"bandwith": 50000000,
             "latency": 1,"jitter": 1,"loss_prob": 0.00001,"energy_consumption": 658}
            ,{"sw_id_src": "of:0000000000000002","sw_id_dst": "of:0000000000000001","port_numb_src": 1,"port_numb_dst": 3,"bandwith": 50000000,
              "latency": 1,"jitter": 1,"loss_prob": 0.00001,"energy_consumption": 658}
            ,{"sw_id_src": "of:0000000000000001","sw_id_dst": "of:0000000000000003","port_numb_src": 4,"port_numb_dst": 1,"bandwith": 8000000,
              "latency": 25,"jitter": 15,"loss_prob": 5,"energy_consumption": 138}
            ,{"sw_id_src": "of:0000000000000003","sw_id_dst": "of:0000000000000001","port_numb_src": 1,"port_numb_dst": 4,"bandwith": 8000000,
              "latency": 25,"jitter": 15,"loss_prob": 5,"energy_consumption": 138}
            ,{"sw_id_src": "of:0000000000000002","sw_id_dst": "of:0000000000000004","port_numb_src": 2,"port_numb_dst": 3,"bandwith": 50000000,
              "latency": 1,"jitter": 1,"loss_prob": 0.00001,"energy_consumption": 658}
            ,{"sw_id_src": "of:0000000000000004","sw_id_dst": "of:0000000000000002","port_numb_src": 3,"port_numb_dst": 2,"bandwith": 50000000,
              "latency": 1,"jitter": 1,"loss_prob": 0.00001,"energy_consumption": 658}
            ,{"sw_id_src": "of:0000000000000003","sw_id_dst": "of:0000000000000004","port_numb_src": 2,"port_numb_dst": 4,"bandwith": 8000000,
              "latency": 25,"jitter": 15,"loss_prob": 5,"energy_consumption": 138}
            ,{"sw_id_src": "of:0000000000000004","sw_id_dst": "of:0000000000000003","port_numb_src": 4,"port_numb_dst": 2,"bandwith": 8000000,
              "latency": 25,"jitter": 15,"loss_prob": 5,"energy_consumption": 138},]
    
    response = requests.post('http://172.17.0.4:8080/links', json=data)

    # Check the response status code
    if response.status_code == 200:
        info('API request successful!\n')
    else:
        info(f'API request failed with status code: {response.status_code} and response: {response.text}\n')
  except Exception as e:
    info(f'API request failed with error: {e}\n')

  #net.get('h1').cmd('python3 scapyScripth1.py')
  #net.get('h2').cmd('python3 scapyScripth2.py')

  info('*** Running CLI ***\n')
  CLI( net )

  info('*** Stopping network ***\n')
  net.stop()
