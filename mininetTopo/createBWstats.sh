#!/bin/bash

#don't forget to make executable chmod +x createBWstats.sh

i=1

# Navigate to the folder
cd "$i"
  
for j in {1..30}
do
case "$i" in
    1)
        sudo tshark -r outputSW1_${j}.pcap -q -z io,stat,1,ip.addr==10.10.10.1 > io_stats_SW1_D_${j}.txt
        sudo tshark -r outputSW4_${j}.pcap -q -z io,stat,1,ip.addr==10.10.10.1 > io_stats_SW4_D_${j}.txt
        ;;
    2)
        sudo tshark -r outputSW1_${j}.pcap -q -z io,stat,1,ip.addr==10.10.10.1 > io_stats_SW1_D_${j}.txt
        sudo tshark -r outputSW4_${j}.pcap -q -z io,stat,1,ip.addr==10.10.10.1 > io_stats_SW4_D_${j}.txt        
        ;;
    3)
        sudo tshark -r outputSW1_${j}.pcap -q -z io,stat,1,ip.addr==10.10.10.1 > io_stats_SW1_D_${j}.txt
        sudo tshark -r outputSW4_${j}.pcap -q -z io,stat,1,ip.addr==10.10.10.1 > io_stats_SW4_D_${j}.txt
        sudo tshark -r outputSW1_${j}.pcap -q -z io,stat,1,ip.addr==10.10.10.2 > io_stats_SW1_C_${j}.txt
        sudo tshark -r outputSW4_${j}.pcap -q -z io,stat,1,ip.addr==10.10.10.2 > io_stats_SW4_C_${j}.txt        
        ;;
    4)
        sudo tshark -r outputSW1_${j}.pcap -q -z io,stat,1,ip.addr==10.10.10.1 > io_stats_SW1_D_${j}.txt
        sudo tshark -r outputSW4_${j}.pcap -q -z io,stat,1,ip.addr==10.10.10.1 > io_stats_SW4_D_${j}.txt
        sudo tshark -r outputSW1_${j}.pcap -q -z io,stat,1,ip.addr==10.10.10.2 > io_stats_SW1_C_${j}.txt
        sudo tshark -r outputSW4_${j}.pcap -q -z io,stat,1,ip.addr==10.10.10.2 > io_stats_SW4_C_${j}.txt         
        ;;
    5)
        sudo tshark -r outputSW1_${j}.pcap -q -z io,stat,1,ip.addr==10.10.10.2 > io_stats_SW1_C_${j}.txt
        sudo tshark -r outputSW4_${j}.pcap -q -z io,stat,1,ip.addr==10.10.10.2 > io_stats_SW4_C_${j}.txt
        ;;
    6)
        sudo tshark -r outputSW1_${j}.pcap -q -z io,stat,1,ip.addr==10.10.10.2 > io_stats_SW1_C_${j}.txt
        sudo tshark -r outputSW4_${j}.pcap -q -z io,stat,1,ip.addr==10.10.10.2 > io_stats_SW4_C_${j}.txt
        ;;
    7)
        sudo tshark -r outputSW1_${j}.pcap -q -z io,stat,1,ip.addr==10.10.10.3 > io_stats_SW1_D_${j}.txt
        sudo tshark -r outputSW4_${j}.pcap -q -z io,stat,1,ip.addr==10.10.10.3 > io_stats_SW4_D_${j}.txt
        sudo tshark -r outputSW1_${j}.pcap -q -z io,stat,1,ip.addr==10.10.10.4 > io_stats_SW1_C_${j}.txt
        sudo tshark -r outputSW4_${j}.pcap -q -z io,stat,1,ip.addr==10.10.10.4 > io_stats_SW4_C_${j}.txt
        ;;
    8)
        sudo tshark -r outputSW1_${j}.pcap -q -z io,stat,1,ip.addr==10.10.10.3 > io_stats_SW1_D_${j}.txt
        sudo tshark -r outputSW4_${j}.pcap -q -z io,stat,1,ip.addr==10.10.10.3 > io_stats_SW4_D_${j}.txt
        sudo tshark -r outputSW1_${j}.pcap -q -z io,stat,1,ip.addr==10.10.10.4 > io_stats_SW1_C_${j}.txt
        sudo tshark -r outputSW4_${j}.pcap -q -z io,stat,1,ip.addr==10.10.10.4 > io_stats_SW4_C_${j}.txt
        ;;
    9) 
        sudo tshark -r outputSW1_${j}.pcap -q -z io,stat,1,ip.addr==10.10.10.1 > io_stats_SW1_D_${j}.txt
        sudo tshark -r outputSW4_${j}.pcap -q -z io,stat,1,ip.addr==10.10.10.1 > io_stats_SW4_D_${j}.txt
        ;;
    10)
        sudo tshark -r outputSW1_${j}.pcap -q -z io,stat,1,ip.addr==10.10.10.1 > io_stats_SW1_D_${j}.txt
        sudo tshark -r outputSW4_${j}.pcap -q -z io,stat,1,ip.addr==10.10.10.1 > io_stats_SW4_D_${j}.txt
        ;;
    11)
        sudo tshark -r outputSW1_${j}.pcap -q -z io,stat,1,ip.addr==10.10.10.1 > io_stats_SW1_D_${j}.txt
        sudo tshark -r outputSW4_${j}.pcap -q -z io,stat,1,ip.addr==10.10.10.1 > io_stats_SW4_D_${j}.txt
        sudo tshark -r outputSW1_${j}.pcap -q -z io,stat,1,ip.addr==10.10.10.2 > io_stats_SW1_C_${j}.txt
        sudo tshark -r outputSW4_${j}.pcap -q -z io,stat,1,ip.addr==10.10.10.2 > io_stats_SW4_C_${j}.txt
        ;;
    12)
        sudo tshark -r outputSW1_${j}.pcap -q -z io,stat,1,ip.addr==10.10.10.1 > io_stats_SW1_D_${j}.txt
        sudo tshark -r outputSW4_${j}.pcap -q -z io,stat,1,ip.addr==10.10.10.1 > io_stats_SW4_D_${j}.txt
        sudo tshark -r outputSW1_${j}.pcap -q -z io,stat,1,ip.addr==10.10.10.2 > io_stats_SW1_C_${j}.txt
        sudo tshark -r outputSW4_${j}.pcap -q -z io,stat,1,ip.addr==10.10.10.2 > io_stats_SW4_C_${j}.txt
        ;;
    13)
        sudo tshark -r outputSW1_${j}.pcap -q -z io,stat,1,ip.addr==10.10.10.2 > io_stats_SW1_C_${j}.txt
        sudo tshark -r outputSW4_${j}.pcap -q -z io,stat,1,ip.addr==10.10.10.2 > io_stats_SW4_C_${j}.txt
        ;;
    14)
        sudo tshark -r outputSW1_${j}.pcap -q -z io,stat,1,ip.addr==10.10.10.2 > io_stats_SW1_C_${j}.txt
        sudo tshark -r outputSW4_${j}.pcap -q -z io,stat,1,ip.addr==10.10.10.2 > io_stats_SW4_C_${j}.txt
        ;;
    15)
        sudo tshark -r outputSW1_${j}.pcap -q -z io,stat,1,ip.addr==10.10.10.3 > io_stats_SW1_D_${j}.txt
        sudo tshark -r outputSW4_${j}.pcap -q -z io,stat,1,ip.addr==10.10.10.3 > io_stats_SW4_D_${j}.txt
        sudo tshark -r outputSW1_${j}.pcap -q -z io,stat,1,ip.addr==10.10.10.4 > io_stats_SW1_C_${j}.txt
        sudo tshark -r outputSW4_${j}.pcap -q -z io,stat,1,ip.addr==10.10.10.4 > io_stats_SW4_C_${j}.txt
        ;;
    16)
        sudo tshark -r outputSW1_${j}.pcap -q -z io,stat,1,ip.addr==10.10.10.3 > io_stats_SW1_D_${j}.txt
        sudo tshark -r outputSW4_${j}.pcap -q -z io,stat,1,ip.addr==10.10.10.3 > io_stats_SW4_D_${j}.txt
        sudo tshark -r outputSW1_${j}.pcap -q -z io,stat,1,ip.addr==10.10.10.4 > io_stats_SW1_C_${j}.txt
        sudo tshark -r outputSW4_${j}.pcap -q -z io,stat,1,ip.addr==10.10.10.4 > io_stats_SW4_C_${j}.txt
        ;;
    17)
        sudo tshark -r outputSW1_${j}.pcap -q -z io,stat,1,ip.addr==10.10.10.1 > io_stats_SW1_D_${j}.txt
        sudo tshark -r outputSW4_${j}.pcap -q -z io,stat,1,ip.addr==10.10.10.1 > io_stats_SW4_D_${j}.txt
        ;;
    18)
        sudo tshark -r outputSW1_${j}.pcap -q -z io,stat,1,ip.addr==10.10.10.1 > io_stats_SW1_D_${j}.txt
        sudo tshark -r outputSW4_${j}.pcap -q -z io,stat,1,ip.addr==10.10.10.1 > io_stats_SW4_D_${j}.txt
        ;;
    19)
        sudo tshark -r outputSW1_${j}.pcap -q -z io,stat,1,ip.addr==10.10.10.1 > io_stats_SW1_D_${j}.txt
        sudo tshark -r outputSW4_${j}.pcap -q -z io,stat,1,ip.addr==10.10.10.1 > io_stats_SW4_D_${j}.txt
        sudo tshark -r outputSW1_${j}.pcap -q -z io,stat,1,ip.addr==10.10.10.2 > io_stats_SW1_C_${j}.txt
        sudo tshark -r outputSW4_${j}.pcap -q -z io,stat,1,ip.addr==10.10.10.2 > io_stats_SW4_C_${j}.txt
        ;;
    20)
        sudo tshark -r outputSW1_${j}.pcap -q -z io,stat,1,ip.addr==10.10.10.1 > io_stats_SW1_D_${j}.txt
        sudo tshark -r outputSW4_${j}.pcap -q -z io,stat,1,ip.addr==10.10.10.1 > io_stats_SW4_D_${j}.txt
        sudo tshark -r outputSW1_${j}.pcap -q -z io,stat,1,ip.addr==10.10.10.2 > io_stats_SW1_C_${j}.txt
        sudo tshark -r outputSW4_${j}.pcap -q -z io,stat,1,ip.addr==10.10.10.2 > io_stats_SW4_C_${j}.txt
        ;;
    21)
        sudo tshark -r outputSW1_${j}.pcap -q -z io,stat,1,ip.addr==10.10.10.2 > io_stats_SW1_C_${j}.txt
        sudo tshark -r outputSW4_${j}.pcap -q -z io,stat,1,ip.addr==10.10.10.2 > io_stats_SW4_C_${j}.txt
        ;;
    22)
        sudo tshark -r outputSW1_${j}.pcap -q -z io,stat,1,ip.addr==10.10.10.2 > io_stats_SW1_C_${j}.txt
        sudo tshark -r outputSW4_${j}.pcap -q -z io,stat,1,ip.addr==10.10.10.2 > io_stats_SW4_C_${j}.txt
        ;;
    23)
        sudo tshark -r outputSW1_${j}.pcap -q -z io,stat,1,ip.addr==10.10.10.3 > io_stats_SW1_D_${j}.txt
        sudo tshark -r outputSW4_${j}.pcap -q -z io,stat,1,ip.addr==10.10.10.3 > io_stats_SW4_D_${j}.txt
        sudo tshark -r outputSW1_${j}.pcap -q -z io,stat,1,ip.addr==10.10.10.4 > io_stats_SW1_C_${j}.txt
        sudo tshark -r outputSW4_${j}.pcap -q -z io,stat,1,ip.addr==10.10.10.4 > io_stats_SW4_C_${j}.txt
        ;;
    *)
        sudo tshark -r outputSW1_${j}.pcap -q -z io,stat,1,ip.addr==10.10.10.3 > io_stats_SW1_D_${j}.txt
        sudo tshark -r outputSW4_${j}.pcap -q -z io,stat,1,ip.addr==10.10.10.3 > io_stats_SW4_D_${j}.txt
        sudo tshark -r outputSW1_${j}.pcap -q -z io,stat,1,ip.addr==10.10.10.4 > io_stats_SW1_C_${j}.txt
        sudo tshark -r outputSW4_${j}.pcap -q -z io,stat,1,ip.addr==10.10.10.4 > io_stats_SW4_C_${j}.txt
        ;;
esac
done

