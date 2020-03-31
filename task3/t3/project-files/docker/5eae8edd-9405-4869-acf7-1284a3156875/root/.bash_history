show running-config
help
ifconfig
ip 192.168.1.2/24 192.168.1.1
ip help
ip address 192.168.1.2/24 192.168.1.1
ip address 192.168.1.2 255.255.255.0
ifconfig
ping 192.168.101.2
ping 10.2.2.2
ping 10.3.3.2
ping 10.4.4.2
ping
ping 192.168.101.2
ping 10.2.2.2
ping 192.168.101.2
iperf
iperf --help
iperf -u -s 192.168.101.2 -i 1 -b 100K
ifconfig
iperf -u -s 192.168.101.2 -i 1 -b 100K
iperf -u -c 192.168.1.2 -i 1 -b 100k
iperf -u -c 192.168.101.2 -i 1 -b 100k
iperf --help
iperf -c 192.168.101.2 -i 1 -b 100k
iperf --help
iperf -c 192.168.101.2 -i 1
iperf -u -c 192.168.101.2 -i 1 -b 100k
iperf -s -u
iperf -u -c 192.168.101.2 -i 1
