##  Cloud Simulation using CloudSim Plus  

---
#### Overview
[CloudSim Plus](https://cloudsimplus.org/) is a modern, up-to-date, full-featured and fully documented simulation framework. It’s easy to use and extend, enabling modeling, simulation, and experimentation of Cloud computing infrastructures and application services. Based on the CloudSim framework, it aims to improve several engineering aspects, such as maintainability, reusability and extensibility. The aim of the assignment is to create Cloud Simulators for evaluating executions of applications in cloud datacenters with different characteristics and deployment models.

#### Instructions
##### Prerequisites
Install [Simple Build Toolkit (SBT)](https://www.scala-sbt.org/1.x/docs/index.html)
##### Run the simulations
1. Clone the repository
2. Navigate to the project
3. Run cmd prompt in the project
4. Execute below cmd
```
sbt clean compile run
```
##### VM Allocation Policies
Vm Allocation Policies enable the Datacenter to select a Host to place or migrate a VM.
We have evaluated 3 different policies:
1. VmAllocationPolicyWorstFit
Chooses, as the host for a VM, that one with the least number of PEs in use, which are enough for the VM2. 
2. VmAllocationPolicySimple
A VmAllocationPolicy implementation that chooses, as the host for a VM, that one with the fewest PEs in use. 
3. VmAllocationPolicyBestFit
Chooses, as the host for a VM, that one with the most number of PEs in use, which are enough for a VM.

##### Broker Implementations
Represents a broker acting on behalf of a cloud customer. A broker implements the policies for selecting a VM to run a Cloudlet and a Datacenter to run the submitted VMs. 
We have evaluated 3 different implementations:
1. DatacenterBrokerFirstFit
Uses a First Fit mapping between submitted cloudlets and Vm's, trying to place a Cloudlet at the first suitable Vm which can be found (according to the required Cloudlet's PEs)
2. DatacenterBrokerSimple
A simple implementation of DatacenterBroker that try to host customer's VMs at the first Datacenter found. If there isn't capacity in that one, it will try the other ones.
3. DatacenterBrokerBestFit
Uses a Best Fit mapping between submitted cloudlets and Vm's, trying to place a Cloudlet at the best suitable Vm which can be found (according to the required Cloudlet's PEs)

##### Evaluation & Analysis

10 VMs were allocated to 4 Hosts of 3 datacenters each. The allocation is dependent on the policy that we used.  Allocation based on different VM allocation policies:
1. VmAllocationPolicyWorstFit
```sh
INFO  0.00: VmAllocationPolicyWorstFit: Vm 0 has been allocated to Host 0/DC 1
INFO  0.00: VmAllocationPolicyWorstFit: Vm 1 has been allocated to Host 1/DC 1
INFO  0.00: VmAllocationPolicyWorstFit: Vm 2 has been allocated to Host 2/DC 1
INFO  0.00: VmAllocationPolicyWorstFit: Vm 3 has been allocated to Host 3/DC 1

INFO  0.10: VmAllocationPolicyWorstFit: Vm 4 has been allocated to Host 0/DC 3
INFO  0.10: VmAllocationPolicyWorstFit: Vm 8 has been allocated to Host 1/DC 3
INFO  0.10: VmAllocationPolicyWorstFit: Vm 9 has been allocated to Host 2/DC 3

INFO  0.20: VmAllocationPolicyWorstFit: Vm 5 has been allocated to Host 0/DC 5
INFO  0.20: VmAllocationPolicyWorstFit: Vm 6 has been allocated to Host 1/DC 5
INFO  0.20: VmAllocationPolicyWorstFit: Vm 7 has been allocated to Host 2/DC 5
```
2. VmAllocationPolicySimple
```sh
INFO  0.00: VmAllocationPolicySimple: Vm 0 has been allocated to Host 0/DC 1
INFO  0.00: VmAllocationPolicySimple: Vm 1 has been allocated to Host 1/DC 1
INFO  0.00: VmAllocationPolicySimple: Vm 2 has been allocated to Host 2/DC 1
INFO  0.00: VmAllocationPolicySimple: Vm 3 has been allocated to Host 3/DC 1

INFO  0.10: VmAllocationPolicySimple: Vm 4 has been allocated to Host 0/DC 3
INFO  0.10: VmAllocationPolicySimple: Vm 8 has been allocated to Host 1/DC 3
INFO  0.10: VmAllocationPolicySimple: Vm 9 has been allocated to Host 2/DC 3

INFO  0.20: VmAllocationPolicySimple: Vm 5 has been allocated to Host 0/DC 5
INFO  0.20: VmAllocationPolicySimple: Vm 6 has been allocated to Host 1/DC 5
INFO  0.20: VmAllocationPolicySimple: Vm 7 has been allocated to Host 2/DC 5
```

3. VmAllocationPolicyBestFit
```sh
INFO  0.00: VmAllocationPolicyBestFit: Vm 0 has been allocated to Host 0/DC 1
INFO  0.00: VmAllocationPolicyBestFit: Vm 1 has been allocated to Host 1/DC 1
INFO  0.00: VmAllocationPolicyBestFit: Vm 2 has been allocated to Host 1/DC 1
INFO  0.00: VmAllocationPolicyBestFit: Vm 3 has been allocated to Host 2/DC 1
INFO  0.00: VmAllocationPolicyBestFit: Vm 4 has been allocated to Host 3/DC 1

INFO  0.10: VmAllocationPolicyBestFit: Vm 8 has been allocated to Host 0/DC 3
INFO  0.10: VmAllocationPolicyBestFit: Vm 9 has been allocated to Host 1/DC 3

INFO  0.20: VmAllocationPolicyBestFit: Vm 5 has been allocated to Host 0/DC 5
INFO  0.20: VmAllocationPolicyBestFit: Vm 6 has been allocated to Host 0/DC 5
INFO  0.20: VmAllocationPolicyBestFit: Vm 7 has been allocated to Host 1/DC 5
```

Based on the above logs, it is clear that both VmAllocationPolicyWorstFit & VmAllocationPolicySimple are similar as they are based on the worst fit policy which utilizes the least number of PEs in use and most available PEs respectively. VmAllocationPolicyBestFit policy utilizes the most number of PEs in use to allocate. 

DataCenterBroker helps in mapping cloudlets to VMs for execution. Different implementations garnered different results & execution time and thus different costs. 
DatacenterBrokerBestFit & DatacenterBrokerWorstFit had similar performance and similar costs but they performed better than DatacenterBrokerSimple. 

DatacenterBrokerBestFit
```
================== Simulation finished at time 5.52 ==================

                                                                  DatacenterBrokerBestFit7

Cloudlet|Status |DC|Host|Host PEs |VM|VM PEs   |CloudletLen|CloudletPEs|StartTime|FinishTime|ExecTime|   Cost   |   Cost   |   Cost   |   Cost   |   Cost   
      ID|       |ID|  ID|CPU cores|ID|CPU cores|         MI|  CPU cores|  Seconds|   Seconds| Seconds| Execution|        BW|   Storage|    Memory|     Total
------------------------------------------------------------------------------------------------------------------------------------------------------------
      17|SUCCESS| 3|   1|        8| 9|        8|       1000|          2|        0|         1|       0|      0.60|    102.40|      0.20|      1.02|    104.23
      18|SUCCESS| 3|   1|        8| 9|        8|       2000|          2|        0|         1|       0|      0.93|    102.40|      0.32|      1.59|    105.23
       4|SUCCESS| 1|   2|        8| 3|        8|       8000|          2|        0|         1|       1|      2.40|    102.40|      0.82|      4.10|    109.72
       9|SUCCESS| 1|   3|        8| 4|        8|       8000|          2|        0|         1|       1|      2.40|     51.20|      0.41|      2.05|     56.06
      19|SUCCESS| 3|   1|        8| 9|        8|       7000|          2|        0|         1|       1|      2.43|    102.40|      0.83|      4.15|    109.81
       8|SUCCESS| 1|   3|        8| 4|        8|       9000|          2|        0|         1|       1|      2.70|    102.40|      0.92|      4.61|    110.63
       1|SUCCESS| 1|   1|        8| 1|        2|      10000|          2|        0|         1|       1|      3.03|    102.40|      1.03|      5.17|    111.64
       6|SUCCESS| 1|   2|        8| 3|        8|      10000|          2|        0|         1|       1|      3.03|    102.40|      1.03|      5.17|    111.64
      12|SUCCESS| 3|   0|        8| 8|        8|      10000|          2|        0|         1|       1|      3.09|    102.40|      1.05|      5.27|    111.82
       3|SUCCESS| 1|   1|        8| 2|        4|      16000|          2|        0|         2|       1|      5.13|    102.40|      1.75|      8.76|    118.04
      14|SUCCESS| 3|   0|        8| 8|        8|      16000|          2|        0|         2|       1|      5.13|    102.40|      1.75|      8.76|    118.04
       0|SUCCESS| 1|   0|        8| 0|        2|      20000|          2|        0|         2|       2|      6.12|    102.40|      2.09|     10.44|    121.05
       5|SUCCESS| 1|   2|        8| 3|        8|      20000|          2|        0|         2|       2|      6.12|    102.40|      2.09|     10.44|    121.05
      10|SUCCESS| 1|   3|        8| 4|        8|      20000|          2|        0|         2|       2|      6.12|    204.80|      4.18|     20.89|    235.99
      11|SUCCESS| 1|   3|        8| 4|        8|      20000|          2|        0|         2|       2|      6.12|    102.40|      2.09|     10.44|    121.05
      16|SUCCESS| 3|   1|        8| 9|        8|      20000|          2|        0|         2|       2|      6.12|    102.40|      2.09|     10.44|    121.05
       7|SUCCESS| 1|   2|        8| 3|        8|      22500|          2|        0|         2|       2|      7.08|    102.40|      2.42|     12.08|    123.98
      20|SUCCESS| 5|   0|       32| 5|       16|      40000|          2|        0|         4|       4|     12.00|     51.20|      2.05|     10.24|     75.49
       2|SUCCESS| 1|   1|        8| 2|        4|      40000|          2|        0|         4|       4|     12.33|    102.40|      4.21|     21.04|    139.98
      13|SUCCESS| 3|   0|        8| 8|        8|      40000|          2|        0|         4|       4|     12.33|    102.40|      4.21|     21.04|    139.98
      15|SUCCESS| 3|   0|        8| 8|        8|      40000|          2|        0|         4|       4|     12.33|    102.40|      4.21|     21.04|    139.98
      21|SUCCESS| 5|   0|       32| 5|       16|      50000|          2|        0|         5|       5|     15.33|    204.80|     10.47|     52.33|    282.92
------------------------------------------------------------------------------------------------------------------------------------------------------------
```
DatacenterBrokerSimple
```sh
================== Simulation finished at time 8.59 ==================

                                                                  DatacenterBrokerSimple7

Cloudlet|Status |DC|Host|Host PEs |VM|VM PEs   |CloudletLen|CloudletPEs|StartTime|FinishTime|ExecTime|   Cost   |   Cost   |   Cost   |   Cost   |   Cost   
      ID|       |ID|  ID|CPU cores|ID|CPU cores|         MI|  CPU cores|  Seconds|   Seconds| Seconds| Execution|        BW|   Storage|    Memory|     Total
------------------------------------------------------------------------------------------------------------------------------------------------------------
      17|SUCCESS| 5|   0|       32| 5|       16|       1000|          2|        0|         1|       0|      0.60|    102.40|      0.20|      1.02|    104.23
      18|SUCCESS| 5|   0|       32| 6|       16|       2000|          2|        0|         1|       0|      0.93|    102.40|      0.32|      1.59|    105.23
       4|SUCCESS| 1|   3|        8| 4|        8|       8000|          2|        0|         1|       1|      2.40|    102.40|      0.82|      4.10|    109.72
      19|SUCCESS| 5|   1|       32| 7|       16|       7000|          2|        0|         1|       1|      2.40|    102.40|      0.82|      4.10|    109.72
       9|SUCCESS| 5|   1|       32| 7|       16|       8000|          2|        0|         1|       1|      2.70|     51.20|      0.46|      2.30|     56.66
       6|SUCCESS| 3|   1|        8| 9|        8|      10000|          2|        0|         1|       1|      3.00|    102.40|      1.02|      5.12|    111.54
       8|SUCCESS| 5|   0|       32| 6|       16|       9000|          2|        0|         1|       1|      3.03|    102.40|      1.03|      5.17|    111.64
      12|SUCCESS| 1|   1|        8| 2|        4|      10000|          2|        0|         1|       1|      3.06|    102.40|      1.04|      5.22|    111.73
       3|SUCCESS| 1|   2|        8| 3|        8|      16000|          2|        0|         2|       1|      5.13|    102.40|      1.75|      8.76|    118.04
      14|SUCCESS| 1|   3|        8| 4|        8|      16000|          2|        0|         2|       1|      5.13|    102.40|      1.75|      8.76|    118.04
       5|SUCCESS| 3|   0|        8| 8|        8|      20000|          2|        0|         2|       2|      6.33|    102.40|      2.16|     10.80|    121.69
      16|SUCCESS| 3|   1|        8| 9|        8|      20000|          2|        0|         2|       2|      6.33|    102.40|      2.16|     10.80|    121.69
       7|SUCCESS| 5|   0|       32| 5|       16|      22500|          2|        0|         2|       2|      7.08|    102.40|      2.42|     12.08|    123.98
       1|SUCCESS| 1|   1|        8| 1|        2|      10000|          2|        0|         3|       3|      9.33|    102.40|      3.18|     15.92|    130.84
       2|SUCCESS| 1|   1|        8| 2|        4|      40000|          2|        0|         4|       4|     12.33|    102.40|      4.21|     21.04|    139.98
      13|SUCCESS| 1|   2|        8| 3|        8|      40000|          2|        0|         4|       4|     12.33|    102.40|      4.21|     21.04|    139.98
      15|SUCCESS| 3|   0|        8| 8|        8|      40000|          2|        0|         4|       4|     12.33|    102.40|      4.21|     21.04|    139.98
      11|SUCCESS| 1|   1|        8| 1|        2|      20000|          2|        0|         5|       5|     15.33|    102.40|      5.23|     26.16|    149.13
       0|SUCCESS| 1|   0|        8| 0|        2|      20000|          2|        0|         6|       6|     18.33|    102.40|      6.26|     31.28|    158.27
      10|SUCCESS| 1|   0|        8| 0|        2|      20000|          2|        0|         6|       6|     18.33|    204.80|     12.51|     62.57|    298.21
      20|SUCCESS| 1|   0|        8| 0|        2|      40000|          2|        0|         8|       8|     24.55|     51.20|      4.19|     20.95|    100.89
      21|SUCCESS| 1|   1|        8| 1|        2|      50000|          2|        0|         8|       8|     24.55|    204.80|     16.76|     83.80|    329.90
------------------------------------------------------------------------------------------------------------------------------------------------------------
```
DatacenterBrokerFirstFit
```sh
================== Simulation finished at time 5.52 ==================

                                                                 DatacenterBrokerFirstFit7

Cloudlet|Status |DC|Host|Host PEs |VM|VM PEs   |CloudletLen|CloudletPEs|StartTime|FinishTime|ExecTime|   Cost   |   Cost   |   Cost   |   Cost   |   Cost   
      ID|       |ID|  ID|CPU cores|ID|CPU cores|         MI|  CPU cores|  Seconds|   Seconds| Seconds| Execution|        BW|   Storage|    Memory|     Total
------------------------------------------------------------------------------------------------------------------------------------------------------------
      17|SUCCESS| 3|   1|        8| 9|        8|       1000|          2|        0|         1|       0|      0.60|    102.40|      0.20|      1.02|    104.23
      18|SUCCESS| 3|   1|        8| 9|        8|       2000|          2|        0|         1|       0|      0.93|    102.40|      0.32|      1.59|    105.23
       4|SUCCESS| 1|   2|        8| 3|        8|       8000|          2|        0|         1|       1|      2.40|    102.40|      0.82|      4.10|    109.72
       9|SUCCESS| 1|   3|        8| 4|        8|       8000|          2|        0|         1|       1|      2.40|     51.20|      0.41|      2.05|     56.06
      19|SUCCESS| 3|   1|        8| 9|        8|       7000|          2|        0|         1|       1|      2.43|    102.40|      0.83|      4.15|    109.81
       8|SUCCESS| 1|   3|        8| 4|        8|       9000|          2|        0|         1|       1|      2.70|    102.40|      0.92|      4.61|    110.63
       1|SUCCESS| 1|   1|        8| 1|        2|      10000|          2|        0|         1|       1|      3.03|    102.40|      1.03|      5.17|    111.64
       6|SUCCESS| 1|   2|        8| 3|        8|      10000|          2|        0|         1|       1|      3.03|    102.40|      1.03|      5.17|    111.64
      12|SUCCESS| 3|   0|        8| 8|        8|      10000|          2|        0|         1|       1|      3.09|    102.40|      1.05|      5.27|    111.82
       3|SUCCESS| 1|   1|        8| 2|        4|      16000|          2|        0|         2|       1|      5.13|    102.40|      1.75|      8.76|    118.04
      14|SUCCESS| 3|   0|        8| 8|        8|      16000|          2|        0|         2|       1|      5.13|    102.40|      1.75|      8.76|    118.04
       0|SUCCESS| 1|   0|        8| 0|        2|      20000|          2|        0|         2|       2|      6.12|    102.40|      2.09|     10.44|    121.05
       5|SUCCESS| 1|   2|        8| 3|        8|      20000|          2|        0|         2|       2|      6.12|    102.40|      2.09|     10.44|    121.05
      10|SUCCESS| 1|   3|        8| 4|        8|      20000|          2|        0|         2|       2|      6.12|    204.80|      4.18|     20.89|    235.99
      11|SUCCESS| 1|   3|        8| 4|        8|      20000|          2|        0|         2|       2|      6.12|    102.40|      2.09|     10.44|    121.05
      16|SUCCESS| 3|   1|        8| 9|        8|      20000|          2|        0|         2|       2|      6.12|    102.40|      2.09|     10.44|    121.05
       7|SUCCESS| 1|   2|        8| 3|        8|      22500|          2|        0|         2|       2|      7.08|    102.40|      2.42|     12.08|    123.98
      20|SUCCESS| 5|   0|       32| 5|       16|      40000|          2|        0|         4|       4|     12.00|     51.20|      2.05|     10.24|     75.49
       2|SUCCESS| 1|   1|        8| 2|        4|      40000|          2|        0|         4|       4|     12.33|    102.40|      4.21|     21.04|    139.98
      13|SUCCESS| 3|   0|        8| 8|        8|      40000|          2|        0|         4|       4|     12.33|    102.40|      4.21|     21.04|    139.98
      15|SUCCESS| 3|   0|        8| 8|        8|      40000|          2|        0|         4|       4|     12.33|    102.40|      4.21|     21.04|    139.98
      21|SUCCESS| 5|   0|       32| 5|       16|      50000|          2|        0|         5|       5|     15.33|    204.80|     10.47|     52.33|    282.92
------------------------------------------------------------------------------------------------------------------------------------------------------------
```

#### Steps undertaken 
1. Including the cloudsimplus dependency and building the code
2. Configuring the network using switches & linking them to the datacenter.
3. Experimenting with various allocation policies like VmAllocationPolicyWorstFit, VmAllocationPolicySimple & VmAllocationPolicyBestFit and different broker implementations like DatacenterBrokerBestFit, DatacenterBrokerSimple & DatacenterBrokerFirstFit. The policies are configurable & can be selected in the cloud_provider_configs file. Adding cost parameters for an effective comparison & evaluation. 
4. Simulating the cloud using different parameters and designing confiurable parameters. There are 2 configurable files - cloud_provider_configs & cloud_consumer_configs. The former for the cloud providers to setup the environment like setting up the datacenter, hosts and VM & later to define the cloudlets.
5. Created a multi-faceted cloud which handles the IAAS, PAAS and SAAS cloudlets. The cloudlets are segregated based on their parameters. Like IAAS will have parameters like Operating System, Software and Deployment. PAAS will have a Deployment and Software parameter. SAAS will just have the Software config. Apart from this additional information, the cloudlets will also necessary parameters like length, fileSize and OutputSize parameters. The addiional parameters help us in segregating the cloudlets. The Datacenter which is executing the cloudlet will act like an IAAS, SAAS or PAAS based on the cloudlet in that moment. For eg., let's say that the Datacenter is executing a Saas cloudlet at the moment. The access can be restricted to just the software in this case. When the cloudlet completes the execution and a new cloudlet comes in, IAAS for eg., the datacenter can allow OS level access. Thus making the system IAAS. 


###### Note:
Configuration files were managed using TypeSafe Configs and loggers are managed using SLFL4J. Test cases are managed using Junit.










