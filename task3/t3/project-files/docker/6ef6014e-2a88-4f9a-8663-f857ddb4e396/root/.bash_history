ifconfig
ping 192.168.64.2
wr
ping 192.168.101.2
ping 192.168.101.3
iperf -u -c 192.168.101.3 -i 1 -b 100k
ping 192.168.101.3
ping 192.168.101.2
iperf -u -c 192.168.101.3 -b 1M -i 1 -f
iperf -u -c 192.168.101.3 -b 2M
