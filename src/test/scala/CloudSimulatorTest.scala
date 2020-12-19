import java.util

import com.typesafe.config.{Config, ConfigFactory, ConfigList}
import junit.framework.TestCase
import org.cloudbus.cloudsim.brokers.DatacenterBroker
import org.cloudbus.cloudsim.cloudlets.{Cloudlet, CloudletSimple}
import org.junit.Assert.{assertEquals, assertTrue}
import org.cloudbus.cloudsim.core.CloudSim
import org.cloudbus.cloudsim.datacenters.{Datacenter, DatacenterSimple}
import org.cloudbus.cloudsim.hosts.Host
import org.cloudbus.cloudsim.vms.Vm

class CloudSimulatorTest extends TestCase {
    var cloudSimulatorTest = new CloudSimulator

    private val conf: Config = ConfigFactory.load
    val VMS: Int = conf.getInt("VMS")
    val HOSTS: Int = conf.getInt("HOSTS")
    val HOST_PES: Int = conf.getInt("HOST_PES")
    val DATACENTERS: Int = conf.getInt("DATACENTERS")
    val HOST_RAM: Int = conf.getInt("HOST_RAM")
    val HOST_STORAGE: Int = conf.getInt("HOST_STORAGE")
    val HOST_BW: Int = conf.getInt("HOST_BW")
    val VM_RAM: Int = conf.getInt("VM_RAM")
    val VM_STORAGE: Int = conf.getInt("VM_STORAGE")
    val VM_BW: Int = conf.getInt("VM_BW")
    val MIPS_CAPACITY: Int = conf.getInt("MIPS_CAPACITY")
    val VM_PES: Int = conf.getInt("VM_PES")
    val cloudletJSON: ConfigList = conf.getList("cloudlets")
    val CLOUDLET_PES: Int =  conf.getInt("CLOUDLET_PES")
    val simulation: CloudSim = new CloudSim

    def testCreateBroker() {
        assertTrue(cloudSimulatorTest.createBroker(simulation).isInstanceOf[DatacenterBroker])
    }
    def testCreateVm{
        assertTrue(cloudSimulatorTest.createVm(MIPS_CAPACITY, VM_PES, VM_RAM, VM_BW, VM_STORAGE).isInstanceOf[Vm])
    }
    def testCreateHost{
        assertTrue(cloudSimulatorTest.createHost(HOST_PES, HOST_RAM, HOST_BW, HOST_STORAGE, MIPS_CAPACITY).isInstanceOf[Host])
    }
    def testCreateDatacenter{
        val datacenterList = new util.ArrayList[Datacenter]
        assertTrue(cloudSimulatorTest.createDatacenter(datacenterList.size(), simulation, HOST_PES, HOST_RAM, HOST_BW, HOST_STORAGE, MIPS_CAPACITY).isInstanceOf[DatacenterSimple])
    }
    def testCreateCloudlet{
        val cloudletMap= new util.HashMap[String, String]
        val cloudletList = new util.ArrayList[Cloudlet]
        assertTrue(cloudSimulatorTest.createCloudlet(cloudletMap, cloudletList, CLOUDLET_PES).isInstanceOf[CloudletSimple])
    }
    def testDataCenterListSize{
        assertEquals(cloudSimulatorTest.datacenterList.size(), 3)
    }
}