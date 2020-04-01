ping 192.168.1.2
ifconfig
iperf -u -c 192.168.1.2 -i 1 -b 100K
iperf -u -s -i 1
iperf -s -i 1
iperf -u -s -i 1
iperf -u -c 192.168.1.2 -b 2M
iperf -u -s -i -1
iperf -u -s -i 1
iperf -s -u
