**ShimConfigurator** - find latest version in _releases_ section  
**Last update** - _02.04.2018_  
**Your version** - file _version\_%date%_

**Properties setup** 
1. _Common properties_:  
  a. PathToShim - path to shim folder ( ex: D:\pdi_client_80_0137\data-integration\plugins\pentaho-big-data-plugin\hadoop-configurations\cdh511 )  
  b. First cluster node - exactly first cluster node - n1, for emr/hdi use master DNS  
  c. .pem file location - path to pem file, use only for emr cluster
2. _Additional properties_:  
  a. Path to test.properties - path to test.properties, use for configuring run_all job sequence  
  b. DFS install dir - setup property pmr.kettle.dfs.install.dir in plugin.properties to /opt/pentaho/mapreduce_%VALUE%   
  c. Named cluster name - name for creating named cluster, if unset will use first nide as name  
3. _Security properties_:  
  a. Kerberos - username/password for kerberos auth, if you don't want kerberos wrapping just leave this fields empty  
  b. REST - username/password for REST auth, use for HDP/CDH/HDI clusters, use for MAPR only if you want to configure local MAPR client  
  c. SSH - username/password for ssh auth, necessary for all clusters  
  d. EMR keys - public/private keays for EMR, added to core-site.xml

**Profile setup**
1. _Profile drop box_ - show all saved profiles, put profile file ( ex: chd513.properties ) to /profiles  
2. _Profile name_ - set profile name, existing profile will be overwritten
3. Only setup properties are saved to profile ( checkboxes are not saved )

**Additional setup**
1. _Download krb5.conf_ - download krb5.conf in 3 places: root tool directory, PENTAHO_JAVA_HOME, JAVA_HOME/ /etc for Linux . If environment variable is absent or user hasn't enough permissions to copy file in this directories file will not be copied  
For kerberos wrapping will search krb5.conf file in the same sequence
2. _Configure mapr client_ - will configure local mapr client. You should have local MAPR client installed. Exactly:  
  a. copy ssl_truestore and -site.xml files to mapr client hadoop configs folder  
  b. execute configure.bat with necessary params  
3. _Copy drivers_ - copy drivers jars mysql to /lib folder others to shim/lib folder
4. _Use BiServer setup_ - will show installed shims and download and configure choosing shim in Spoon, PRD, PME and Server, also can be used with separate installed Spoon 

**Using tool**:

Unzip -bin.zip folder and run java -jar shim-config-loader-1.0.0.jar . Before run check if user has all necessary permissions.

**What exactly does this tool**:

1. Download -site.xml files / krb5.conf
2. Create new named cluster
3. Configure pentaho product to use selected shim  
  a. Setup config.properties ( also setup mapr classpath )  
  b. Setup plugin.properties
4. Edit -site.xml files  
  a. Add cross-platform property  
  b. Edit core-site.xml for mapr with uid property  
  c. Edit core-site.xml for EMR with keys
4. Configure mapr client
5. Put driver jars into pentaho product 

_Any bugs and suggestions leave in **Issues** section on git._