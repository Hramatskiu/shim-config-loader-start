package com.epam.shim.configurator.profile;

import com.epam.loader.config.credentials.HttpCredentials;
import com.epam.loader.config.credentials.Krb5Credentials;
import com.epam.loader.config.credentials.SshCredentials;
import com.epam.loader.plan.manager.LoadConfigsManager;
import com.epam.shim.configurator.util.CopyDriversUtil;
import com.epam.shim.configurator.util.PropertyHandler;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class ProfileBuilder {
  public List<String> loadProfileNames() {
    try {
      return getAllProfilePaths( CopyDriversUtil.getRootUtilityFolder() + File.separator + "profile" ).stream()
        .map( Path::getFileName ).map( name -> name.toString() ).collect( Collectors.toList() );
    } catch ( IOException e ) {
      throw new RuntimeException( e );
    }
  }

  public Profile buildProfile( Path profilePath ) {
    return new Profile( extractKrb5Credentials( profilePath.toAbsolutePath().toString() ),
      extractSshCredentials( profilePath.toAbsolutePath().toString() ),
      extractHttpCredentials( profilePath.toAbsolutePath().toString() ),
      extractClusterType( profilePath.toAbsolutePath().toString() ),
      extractPathToShim( profilePath.toAbsolutePath().toString() ),
      extractDfsInstallDir( profilePath.toAbsolutePath().toString() ),
      extractHosts( profilePath.toAbsolutePath().toString() ),
      extractProfileName( profilePath.toAbsolutePath().toString() ) );
  }

  public Path getProfilePath( String profileName ) throws IOException {
    return ( Files.find( Paths.get( CopyDriversUtil.getRootUtilityFolder() + File.separator + "profile" ), 1,
      ( p, bfa ) -> bfa.isRegularFile()
        && p.getFileName().toString().matches( profileName ) ).collect( Collectors.toList() ).get( 0 ) );
  }

  public void saveProfile( Profile profile ) throws IOException {
    String profilePath = createNewProfilePath( profile.getName() );
    saveKrb5Properties( profilePath, profile.getKrb5Credentials() );
    saveSshProperties( profilePath, profile.getSshCredentials() );
    saveHttpProperties( profilePath, profile.getHttpCredentials() );
    saveProfileName( profilePath, profile.getName() );
    savePathToShim( profilePath, profile.getPathToShim() );
    saveDfsInstallDir( profilePath, profile.getDfsInstallDir() );
    saveHosts( profilePath, profile.getHosts() );
    saveClusterName( profilePath, profile.getClusterType().toString() );
  }

  private void saveKrb5Properties( String profilePath, Krb5Credentials krb5Credentials ) {
    PropertyHandler.setProperty( profilePath, "krb5.username", krb5Credentials.getUsername() );
    PropertyHandler.setProperty( profilePath, "krb5.password", krb5Credentials.getPassword() );
  }

  private void saveSshProperties( String profilePath, SshCredentials sshCredentials ) {
    PropertyHandler.setProperty( profilePath, "ssh.username", sshCredentials.getUsername() );
    PropertyHandler.setProperty( profilePath, "ssh.password", sshCredentials.getPassword() );
    PropertyHandler.setProperty( profilePath, "ssh.identity", sshCredentials.getIdentityPath() );
  }

  private void saveHttpProperties( String profilePath, HttpCredentials httpCredentials ) {
    PropertyHandler.setProperty( profilePath, "http.username", httpCredentials.getUsername() );
    PropertyHandler.setProperty( profilePath, "http.password", httpCredentials.getPassword() );
  }

  private void saveProfileName( String profilePath, String profileName ) {
    PropertyHandler.setProperty( profilePath, "name", profileName );
  }

  private void savePathToShim( String profilePath, String pathToShim ) {
    PropertyHandler.setProperty( profilePath, "pathToShim", pathToShim );
  }

  private void saveDfsInstallDir( String profilePath, String dfsInstallDir ) {
    PropertyHandler.setProperty( profilePath, "dfsInstallDir", dfsInstallDir );
  }

  private void saveHosts( String profilePath, String hosts ) {
    PropertyHandler.setProperty( profilePath, "hosts", hosts );
  }

  private void saveClusterName( String profilePath, String clusterName ) {
    PropertyHandler.setProperty( profilePath, "clusterType", clusterName );
  }

  private String createNewProfilePath( String profileName ) throws IOException {
    if ( !Files.exists( Paths.get(
      CopyDriversUtil.getRootUtilityFolder() + File.separator + "profile" + File.separator + profileName
        + ".properties" ) ) ) {
      Files.createFile( Paths.get(
        CopyDriversUtil.getRootUtilityFolder() + File.separator + "profile" + File.separator + profileName
          + ".properties" ) );
    }

    return Paths.get( CopyDriversUtil.getRootUtilityFolder() + File.separator + "profile" ).toAbsolutePath().toString()
      + File.separator + profileName + ".properties";
  }

  private List<Path> getAllProfilePaths( String profilesFolder ) throws IOException {
    if ( !Files.exists( Paths.get( profilesFolder ) ) ) {
      Files.createDirectory( Paths.get( profilesFolder ) );
    }

    return ( Files.find( Paths.get( profilesFolder ), 1, ( p, bfa ) -> bfa.isRegularFile()
      && p.getFileName().toString().matches( ".*\\.properties" ) ).collect( Collectors.toList() ) );
  }

  private Krb5Credentials extractKrb5Credentials( String pathToFile ) {
    return new Krb5Credentials( PropertyHandler.getPropertyFromFile( pathToFile, "krb5.username" ),
      PropertyHandler.getPropertyFromFile( pathToFile, "krb5.password" ) );
  }

  private SshCredentials extractSshCredentials( String pathToFile ) {
    return new SshCredentials( PropertyHandler.getPropertyFromFile( pathToFile, "ssh.username" ),
      PropertyHandler.getPropertyFromFile( pathToFile, "ssh.password" ),
      PropertyHandler.getPropertyFromFile( pathToFile, "ssh.identity" ) );
  }

  private HttpCredentials extractHttpCredentials( String pathToFile ) {
    return new HttpCredentials( PropertyHandler.getPropertyFromFile( pathToFile, "http.username" ),
      PropertyHandler.getPropertyFromFile( pathToFile, "http.password" ) );
  }

  private String extractPathToShim( String pathToFile ) {
    return PropertyHandler.getPropertyFromFile( pathToFile, "pathToShim" );
  }

  private String extractDfsInstallDir( String pathToFile ) {
    return PropertyHandler.getPropertyFromFile( pathToFile, "dfsInstallDir" );
  }

  private String extractHosts( String pathToFile ) {
    String host = PropertyHandler.getPropertyFromFile( pathToFile, "hosts" );
    return host != null ? host.substring( 1, host.length() - 1 ) : StringUtils.EMPTY;
  }

  private LoadConfigsManager.ClusterType extractClusterType( String pathToFile ) {
    return LoadConfigsManager.ClusterType.valueOf( PropertyHandler.getPropertyFromFile( pathToFile, "clusterType" ) );
  }

  private String extractProfileName( String pathToFile ) {
    return PropertyHandler.getPropertyFromFile( pathToFile, "name" );
  }
}
