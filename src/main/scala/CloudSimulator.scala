import java.util

import org.cloudbus.cloudsim.hosts.Host
import org.cloudbus.cloudsim.network.switches.{AbstractSwitch, AggregateSwitch, EdgeSwitch, RootSwitch}
import com.typesafe.config.{Config, ConfigFactory, ConfigList, ConfigObject}
import org.cloudbus.cloudsim.allocationpolicies.{VmAllocationPolicyAbstract, VmAllocationPolicyBestFit, VmAllocationPolicyRoundRobin, VmAllocationPolicySimple, VmAllocationPolicyWorstFit}
import org.cloudbus.cloudsim.brokers.{DatacenterBroker, DatacenterBrokerBestFit, DatacenterBrokerFirstFit, DatacenterBrokerSimple}
import org.cloudbus.cloudsim.cloudlets.{Cloudlet, CloudletSimple}
import org.cloudbus.cloudsim.core.CloudSim
import org.cloudbus.cloudsim.datacenters.network.NetworkDatacenter
import org.cloudbus.cloudsim.datacenters.{Datacenter, DatacenterSimple}
import org.cloudbus.cloudsim.hosts.network.NetworkHost
import org.cloudbus.cloudsim.hosts.{Host, HostSimple}
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple
import org.cloudbus.cloudsim.resources.{Pe, PeSimple}
import org.cloudbus.cloudsim.utilizationmodels.{UtilizationModel, UtilizationModelFull}
import org.cloudbus.cloudsim.vms.{Vm, VmSimple}
import org.cloudsimplus.builders.tables.{CloudletsTableBuilder, TextTableColumn}
import org.slf4j.{Logger, LoggerFactory}
import java.text.DecimalFormat
import java.util.{Comparator, List}
import java.util.Comparator.{comparingDouble, comparingLong}

import org.cloudsimplus.listeners.CloudletVmEventInfo
import org.cloudsimplus.slametrics.SlaContract

import scala.collection.View.Empty.sum
import scala.collection.convert.ImplicitConversions.`collection AsScalaIterable`
import scala.jdk.CollectionConverters.CollectionHasAsScala

object CloudSimulator {
  def main(args: Array[String]): Unit = {
    new CloudSimulator
  }
}

class CloudSimulator {
  private val logger: Logger = LoggerFactory.getLogger(classOf[CloudSimulator])
  private val cloud_provider_configs: Config = ConfigFactory.parseResources("cloud_provider_configs.conf")
  private val cloud_consumer_configs: Config = ConfigFactory.parseResources("cloud_consumer_configs.conf")
  private val conf = ConfigFactory.load()
    .withFallback(cloud_provider_configs)
    .withFallback(cloud_consumer_configs)
    .resolve()

  val VMS: Int = conf.getInt("VMS")
  val DATACENTERS: Int = conf.getInt("DATACENTERS")
  val DC: ConfigList = conf.getList("DC")
  val vm_json: ConfigList = conf.getList("vm_json")
  val MIPS_CAPACITY: Int = conf.getInt("MIPS_CAPACITY")
  val cloudletJSON: ConfigList = conf.getList("cloudlets")
  val CLOUDLET_PES: Int =  conf.getInt("CLOUDLET_PES")
  val COST: Double =  conf.getDouble("COST")
  val COST_PER_MEM: Double =  conf.getDouble("COST_PER_MEM")
  val COST_PER_STORAGE: Double =  conf.getDouble("COST_PER_STORAGE")
  val COST_PER_BW: Double =  conf.getDouble("COST_PER_BW")
  // for Policy Selection, values for keys indicate => 1 : VmAllocationPolicyRoundRobin, 2: VmAllocationPolicySimple, 3:  VmAllocationPolicyBestFit
  val VM_ALLOCATION_POLICY = conf.getInt("VM_ALLOCATION_POLICY")
  //for Broker Selection, value should be => 1 : DatacenterBrokerBestFit, 2: DatacenterBrokerSimple, 3: DatacenterBrokerFirstFit
  val BROKER_IMPLEMENTATION = conf.getInt("BROKER_IMPLEMENTATION")

  val simulation: CloudSim = new CloudSim
  val datacenterList = new util.ArrayList[Datacenter]
  // assign values to consts
  logger.info("Starting the CloudSimulator execution")
  // create cloudlets from tasks

  val tasks: util.ArrayList[util.HashMap[String, String]] = createTasks(cloudletJSON)
  val dc_list: util.ArrayList[util.HashMap[String, String]] = createDC(DC)
  (dc_list).map(dc => datacenterList.add(createDatacenter(datacenterList.size(), simulation, dc.get("HOST_PES").toInt, dc.get("HOST_RAM").toInt , dc.get("HOST_BW").toInt, dc.get("HOST_STORAGE").toInt, MIPS_CAPACITY)))
  val broker: DatacenterBroker = createBroker(simulation)
  val vm_list: util.ArrayList[util.HashMap[String, String]] = createVMList(vm_json)
  createAndSubmitVms(broker, MIPS_CAPACITY, vm_list)
  createAndSubmitCloudlets(broker, tasks, CLOUDLET_PES)
  simulation.start
  printResults(broker, COST_PER_STORAGE, COST_PER_MEM)
  logger.info("CloudSimulator execution completed")

  def createDC(DC : ConfigList) : util.ArrayList[util.HashMap[String, String]] = {
    logger.info("Creating data center configs based on cloud provider configs")
    val dc_list = new util.ArrayList[util.HashMap[String, String]]
    (0 until DC.size()).foreach  {
      i => val configs = new util.HashMap[String, String]
        val c = DC.get(i).asInstanceOf[ConfigObject].toConfig
        checkToken(configs,c,"HOSTS")
        checkToken(configs,c,"HOST_PES")
        checkToken(configs,c,"HOST_RAM")
        checkToken(configs,c,"HOST_STORAGE")
        checkToken(configs,c,"HOST_BW")
        dc_list.add(configs:util.HashMap[String, String])
    }
    dc_list
  }

  def createVMList(vm_json : ConfigList) : util.ArrayList[util.HashMap[String, String]] = {
    logger.info("Creating vm_lists based on cloud provider configs")
    val vm_list = new util.ArrayList[util.HashMap[String, String]]
    (0 until vm_json.size()).foreach  {
      i => val configs = new util.HashMap[String, String]
        val c = vm_json.get(i).asInstanceOf[ConfigObject].toConfig
        checkToken(configs,c,"VM_PES")
        checkToken(configs,c,"VM_RAM")
        checkToken(configs,c,"VM_STORAGE")
        checkToken(configs,c,"VM_BW")
        vm_list.add(configs:util.HashMap[String, String])
    }
    vm_list
  }


  def findAllocationPolicy : VmAllocationPolicyAbstract = {
    VM_ALLOCATION_POLICY match {
      case 1 => new VmAllocationPolicyWorstFit
      case 2 => new VmAllocationPolicySimple
      case 3 => new VmAllocationPolicyBestFit
      case _ => new VmAllocationPolicySimple
    }
  }

  def findBrokerImplementation(simulation: CloudSim) : DatacenterBroker = {
    BROKER_IMPLEMENTATION match {
      case 1 => new DatacenterBrokerBestFit(simulation)
      case 2 => new DatacenterBrokerSimple(simulation)
      case 3 => new DatacenterBrokerFirstFit(simulation)
      case _ => new DatacenterBrokerSimple(simulation)
    }
  }
  /**
   * Creates a Datacenter with hostlist and allocation policy is specified
   */
  def createDatacenter(datacenterCount: Int, simulation:CloudSim, HOST_PES: Int, HOST_RAM: Int, HOST_BW: Int, HOST_STORAGE: Int, MIPS_CAPACITY: Int): Datacenter = {
    val numberOfHosts = EdgeSwitch.PORTS * AggregateSwitch.PORTS * RootSwitch.PORTS
    val hostList = new util.ArrayList[Host](numberOfHosts)
    (0 until numberOfHosts).foreach( _ => {
      logger.info("Creating HOST {} for Datacenter {} ",hostList.size(),datacenterCount)
      hostList.add(createHost(HOST_PES, HOST_RAM, HOST_BW, HOST_STORAGE, MIPS_CAPACITY))
    })
    val dc: NetworkDatacenter = new NetworkDatacenter(simulation, hostList, findAllocationPolicy)
    logger.info("Creating Datacenter {}",datacenterCount)
    dc.getCharacteristics
      .setCostPerSecond(COST)
      .setCostPerMem(COST_PER_MEM)
      .setCostPerStorage(COST_PER_STORAGE)
      .setCostPerBw(COST_PER_BW)
    createNetwork(dc)
    dc
  }

  protected def createNetwork(datacenter: NetworkDatacenter): Unit = {
    val edgeSwitches = new Array[EdgeSwitch](1)
    edgeSwitches.indices.foreach { i=>
      edgeSwitches(i) = new EdgeSwitch(simulation, datacenter)
      datacenter.addSwitch(edgeSwitches(i))
    }

    datacenter.getHostList[NetworkHost].forEach{ host=>
      val switchNum = getSwitchIndex(host, edgeSwitches(0).getPorts)
      edgeSwitches(switchNum).connectHost(host)
    }
  }

  def getSwitchIndex(host: NetworkHost, switchPorts: Int): Int = Math.round(host.getId % Integer.MAX_VALUE / switchPorts)

  /**
   * Creates a host based on the Processing engines
   * @return Host
   */
  def createHost(HOST_PES: Int, HOST_RAM: Int, HOST_BW: Int, HOST_STORAGE: Int, MIPS_CAPACITY: Int): Host = {
    val peList = new util.ArrayList[Pe]
    (0 until HOST_PES).foreach(_ => peList.add(new PeSimple(MIPS_CAPACITY, new PeProvisionerSimple)))
    new NetworkHost(HOST_RAM, HOST_BW, HOST_STORAGE, peList)
  }

  /**
   * Creates a broker based on the cloud provider requirements
   * @return broker
   */
  def createBroker(simulation: CloudSim): DatacenterBroker = {
    logger.info("Creating Broker")
    findBrokerImplementation(simulation)

  }

  /**
   * Creates a list of Vms and maps them with the broker
   */
  def createAndSubmitVms(broker: DatacenterBroker, MIPS_CAPACITY: Int, vm_list: util.ArrayList[util.HashMap[String,String]]): Unit = {
    val vmlist = new util.ArrayList[Vm]
    vm_list.map {
      vm => logger.info("Creating VM {}",vmlist.size)
      vmlist.add(createVm(MIPS_CAPACITY, vm.get("VM_PES").toInt, vm.get("VM_RAM").toInt, vm.get("VM_BW").toInt, vm.get("VM_STORAGE").toInt ))
    }
    logger.info("Mapping the broker to the VMs")
    broker.submitVmList(vmlist)
  }
  /**
   * Create a VM based on parameters supplied in the conf file
   * @return vm
   */
  def createVm(MIPS_CAPACITY: Int, VM_PES: Int, VM_RAM: Int, VM_BW: Int, VM_STORAGE: Int): Vm = {
    new VmSimple(MIPS_CAPACITY, VM_PES).setRam(VM_RAM).setBw(VM_BW).setSize(VM_STORAGE)
  }

  /**
   * Method to submit the cloudlet list to brokers
   */
  def createAndSubmitCloudlets(broker:DatacenterBroker, tasks: util.ArrayList[util.HashMap[String, String]], CLOUDLET_PES: Int ): Unit = {
    val cloudletList = new util.ArrayList[Cloudlet]
    tasks.asScala.foreach(
      cloudletMap => cloudletList.add(createCloudlet(cloudletMap, cloudletList, CLOUDLET_PES)))
    logger.info("Submitting the cloudlets to the broker")
    broker.submitCloudletList(cloudletList)
  }

  /**
   * Method to create a new cloudlet based on the configurations supplied in the conf file or selects a default value of 1024
   * @return cloudlet
   */
  def createCloudlet(cloudletMap: util.Map[String, String], cloudletList: util.ArrayList[Cloudlet], CLOUDLET_PES: Int): Cloudlet = {
    val utilization: UtilizationModel = new UtilizationModelFull
    logger.info("Creating Cloudlet {}",cloudletList.size())
    new CloudletSimple(cloudletMap.getOrDefault("length","1024").toInt, CLOUDLET_PES )
      .setFileSize(cloudletMap.getOrDefault("FileSize", "1024").toInt)
      .setOutputSize(cloudletMap.getOrDefault("OutputSize", "1024").toInt)
      .setUtilizationModel(utilization)
  }

  /**
   * Read the configlist of cloudlets and fetch the cloudlet configurations and store it in a map
   */
  def createTasks(cloudletJSON : ConfigList) : util.ArrayList[util.HashMap[String, String]] = {
    logger.info("Creating tasks based on user configs")
    val tasks= new util.ArrayList[util.HashMap[String, String]]
    (0 until cloudletJSON.size()).foreach  {
      i => val configs = new util.HashMap[String, String]
        val c = cloudletJSON.get(i).asInstanceOf[ConfigObject].toConfig
        checkToken(configs,c,"OutputSize")
        checkToken(configs,c,"FileSize")
        checkToken(configs,c,"os")
        checkToken(configs,c,"deploy")
        checkToken(configs,c,"sw")
        checkToken(configs,c,"length")
        tasks.add(configs:util.HashMap[String, String])
    }
    tasks
  }

  def checkToken(configs: util.HashMap[String, String], c: Config, token: String): util.HashMap[String, String] ={
    c.hasPath(token) match {
      case false =>
      case true =>
        configs.put(token, c.getString(token))
    }
    configs
  }
  /**
   * Print the results for evaluation
   */
  def printResults(broker:DatacenterBroker, COST_PER_STORAGE: Double, COST_PER_MEM: Double): Unit = {
    logger.info("Printing the results")
    val df = new DecimalFormat("0.00")
    new CloudletsTableBuilder(broker.getCloudletFinishedList)
      .addColumn(new TextTableColumn("   Cost   ", "Execution"), (cloudlet: Cloudlet) =>  df.format(cloudlet.getActualCpuTime*cloudlet.getCostPerSec()))
      .addColumn(new TextTableColumn("   Cost   ", "BW"), (cloudlet: Cloudlet) =>  df.format(cloudlet.getFileSize*cloudlet.getCostPerBw))
      .addColumn(new TextTableColumn("   Cost   ", "Storage"), (cloudlet: Cloudlet) =>  df.format(cloudlet.getActualCpuTime*cloudlet.getFileSize*COST_PER_STORAGE))
      .addColumn(new TextTableColumn("   Cost   ", "Memory"), (cloudlet: Cloudlet) =>  df.format(cloudlet.getFileSize*cloudlet.getActualCpuTime*COST_PER_MEM))
      .addColumn(new TextTableColumn("   Cost   ", "Total"), (cloudlet: Cloudlet) =>  df.format(cloudlet.getActualCpuTime*cloudlet.getCostPerSec()
        + cloudlet.getFileSize*cloudlet.getCostPerBw
        + cloudlet.getActualCpuTime*cloudlet.getFileSize*COST_PER_STORAGE
        + cloudlet.getFileSize*cloudlet.getActualCpuTime*COST_PER_MEM)).setTitle(broker.getName).build()
  }
}
