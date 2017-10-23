package com.epam.main;

import com.epam.loader.ClusterConfigLoader;
import com.epam.loader.config.credentials.EmrCredentials;
import com.epam.loader.config.credentials.HttpCredentials;
import com.epam.loader.config.credentials.Krb5Credentials;
import com.epam.loader.config.credentials.LoadConfigs;
import com.epam.loader.config.credentials.SshCredentials;
import com.epam.loader.plan.manager.LoadConfigsManager;
import com.epam.logger.TextAreaAppender;
import com.epam.shim.configurator.ShimDependentConfigurator;
import com.epam.shim.configurator.config.ModifierConfiguration;
import com.epam.shim.configurator.profile.Profile;
import com.epam.shim.configurator.profile.ProfileBuilder;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainPage {

  private final Logger logger = Logger.getLogger( MainPage.class );

  @FXML
  Button buttonStart;
  @FXML
  Button buttonOpenShim;
  @FXML
  TextField pathToSave;
  @FXML
  TextField pathToPemFile;
  @FXML
  TextField cluster_node_FQDN;
  @FXML
  TextField kerberosUser;
  @FXML
  TextField kerberosPassword;
  @FXML
  TextField restUser;
  @FXML
  TextField restPassword;
  @FXML
  TextField sshUser;
  @FXML
  TextField sshPassword;
  @FXML
  TextField dfsInstallDir;
  @FXML
  TextField pathToTestProperties;
  @FXML
  TextField newProfile;
  @FXML
  TextField emrAccessKey;
  @FXML
  TextField emrSecretKey;
  @FXML
  Button profileSave;
  @FXML
  Button profileLoad;
  @FXML
  Button buttonOpenPemFile;
  @FXML
  Button buttonOpenTestProperties;
  @FXML
  ComboBox profiles;
  @FXML
  Label testPathLabel;
  @FXML
  Label dfsInstallDirLabel;
  @FXML
  TextArea output;
  @FXML
  Label emrKeys;
  @FXML
  Label pemFileLabel;
  @FXML
  Label restLabel;
  @FXML
  Label sshLabel;
  @FXML
  Label kerberosLabel;
  @FXML
  ComboBox<String> clusterType;

  private static ClusterConfigLoader clusterConfigLoader;
  private ProfileBuilder profileBuilder;

  public static void setClusterConfigLoader( ClusterConfigLoader newClusterConfigLoader ) {
    clusterConfigLoader = newClusterConfigLoader;
  }

  @FXML
  void buttonInit( ActionEvent event ) {
    if ( event.getTarget() instanceof Button ) {
      if ( event.getTarget() == buttonStart ) {
        buttonStartAction();
      } else if ( event.getTarget() == buttonOpenShim ) {
        buttonOpenShimAction();
      }
    }
  }

  @FXML
  void initialize() {
    TextAreaAppender.setTextArea( output );
    clusterType.valueProperty().addListener( ( observable, oldValue, newValue ) -> {
      showNecessaryFields( newValue );
    } );
    profileBuilder = new ProfileBuilder();
    profiles.getItems().setAll( profileBuilder.loadProfileNames() );
    clusterConfigLoader = new ClusterConfigLoader();
    clusterConfigLoader.init();
    initClusterComboBox();
  }

  private void initClusterComboBox() {
    List<String> clusterTypes = new ArrayList<>();
    clusterTypes.add( LoadConfigsManager.ClusterType.HDP.toString() );
    clusterTypes.add( LoadConfigsManager.ClusterType.CDH.toString() );
    clusterTypes.add( LoadConfigsManager.ClusterType.MAPR.toString() );
    clusterTypes.add( LoadConfigsManager.ClusterType.EMR.toString() );

    clusterType.getItems().setAll( clusterTypes );
    clusterType.setValue( LoadConfigsManager.ClusterType.HDP.toString() );
  }

  @FXML
  private void loadProfile( ActionEvent event ) {
    try {
      Profile profile = profileBuilder.buildProfile( profileBuilder.getProfilePath( profiles.getValue().toString() ) );
      pathToSave.setText( profile.getPathToShim() );
      cluster_node_FQDN.setText( profile.getHosts() );
      kerberosUser.setText( profile.getKrb5Credentials().getUsername() );
      kerberosPassword.setText( profile.getKrb5Credentials().getPassword() );
      sshPassword.setText( profile.getSshCredentials().getPassword() );
      sshUser.setText( profile.getSshCredentials().getUsername() );
      pathToPemFile.setText( profile.getSshCredentials().getIdentityPath() );
      restUser.setText( profile.getHttpCredentials().getUsername() );
      restPassword.setText( profile.getHttpCredentials().getPassword() );
      dfsInstallDir.setText( profile.getDfsInstallDir() );
      clusterType.setValue( profile.getClusterType().toString() );
      emrSecretKey.setText( profile.getEmrCredentials().getSecretKey() );
      emrAccessKey.setText( profile.getEmrCredentials().getAccessKey() );
      pathToTestProperties.setText( profile.getPathToTestProperties() );
      newProfile.setText( profile.getName() );
    } catch ( IOException e ) {
      e.printStackTrace();
    }
  }

  @FXML
  private void saveProfile( ActionEvent event ) {
    Profile profile = new Profile( new Krb5Credentials( kerberosUser.getText(), kerberosPassword.getText() ),
      new SshCredentials( sshUser.getText(), sshPassword.getText(), pathToPemFile.getText() ),
      new HttpCredentials( restUser.getText(), restPassword.getText() ),
      LoadConfigsManager.ClusterType.valueOf( clusterType.getValue() ),
      pathToSave.getText(), dfsInstallDir.getText(),
      cluster_node_FQDN.getText().trim(), newProfile.getText(),
      new EmrCredentials( emrAccessKey.getText(), emrSecretKey.getText() ),
      pathToTestProperties.getText() );

    try {
      profileBuilder.saveProfile( profile );
      if ( !profiles.getItems().contains( profile.getName() + ".properties" ) ) {
        profiles.getItems().add( profile.getName() + ".properties" );
      }

    } catch ( IOException e ) {
      e.printStackTrace();
    }
  }

  @FXML
  void compoBoxOnShowing( Event event ) {
    if ( event.getTarget() instanceof ComboBox ) {
      if ( event.getTarget() == clusterType ) {
        //        List<String> clusterTypes = new ArrayList<>();
        //        clusterTypes.add( LoadConfigsManager.ClusterType.HDP.toString() );
        //        clusterTypes.add( LoadConfigsManager.ClusterType.CDH.toString() );
        //        clusterTypes.add( LoadConfigsManager.ClusterType.MAPR.toString() );
        //        clusterTypes.add( LoadConfigsManager.ClusterType.EMR.toString() );
        //
        //        clusterType.getItems().setAll( clusterTypes );
        //        //clusterType.setValue( LoadConfigsManager.ClusterType.HDP.toString() );
      }
    }
  }

  private void showNecessaryFields( String newValue ) {
    switch ( newValue ) {
      case "EMR":
        setVisibilityForPemFileInput( true );
        setVisibilityForAuth( true );
        setUpEMRFields();
        break;
      case "MAPR":
        setVisibilityForPemFileInput( false );
        setVisibilityForAuth( true );
        setUpMAPRFields();
        break;
      case "CDH":
      case "HDP":
        setVisibilityForPemFileInput( false );
        setVisibilityForAuth( false );
        break;
      default:
    }
  }

  private void setUpEMRFields() {
    sshUser.setText( "hadoop" );
    sshPassword.setText( "password" );
  }

  private void setUpMAPRFields() {
    sshUser.setText( "mapr" );
    sshPassword.setText( "password" );
  }

  private void setVisibilityForAuth( boolean isSshOnly ) {
    restLabel.setVisible( !isSshOnly );
    restUser.setVisible( !isSshOnly );
    restPassword.setVisible( !isSshOnly );
    //    sshLabel.setVisible( isSshOnly );
    //    sshUser.setVisible( isSshOnly );
    //    sshPassword.setVisible( isSshOnly );
  }

  private void setVisibilityForPemFileInput( boolean isPemNeeded ) {
    pemFileLabel.setVisible( isPemNeeded );
    if ( !isPemNeeded ) {
      pathToPemFile.setText( "" );
    }
    pathToPemFile.setVisible( isPemNeeded );
    buttonOpenPemFile.setVisible( isPemNeeded );
    emrKeys.setVisible( isPemNeeded );
    emrAccessKey.setVisible( isPemNeeded );
    emrSecretKey.setVisible( isPemNeeded );
  }

  private void buttonStartAction() {
    if ( pathToSave.getText().isEmpty() || cluster_node_FQDN.getText().isEmpty() ) {
      logger.info( "One or more required field(s) is empty! Only test.properties field is not required!" );
    } else {
      //output.setText( "" );
      buttonStart.setDisable( true );

      Thread thread = new Thread( () -> {
        boolean isDownloaded = clusterConfigLoader
          .loadConfigs( new LoadConfigs( new HttpCredentials( restUser.getText(), restPassword.getText() ),
            createKrb5Configs(),
            new SshCredentials( sshUser.getText(), sshPassword.getText(), pathToPemFile.getText() ),
            modifyHosts( cluster_node_FQDN.getText().trim() ), pathToSave.getText(),
            LoadConfigsManager.ClusterType.valueOf( clusterType.getValue() ) ) );

        if ( isDownloaded ) {
          ShimDependentConfigurator.configureShimProperties( new ModifierConfiguration( pathToSave.getText(),
              dfsInstallDir.getText(), pathToTestProperties.getText(), false,
              LoadConfigsManager.ClusterType.valueOf( clusterType.getValue() ),
              modifyHosts( cluster_node_FQDN.getText().trim() ) ),
            new EmrCredentials( emrAccessKey.getText(), emrSecretKey.getText() ) );
        }
        buttonStart.setDisable( false );
      } );

      thread.start();
    }
  }

  private String modifyHosts( String host ) {
    return host.contains( "svqxbdcn6" ) ? host + "," + host.replace( "n1", "n2" )
      + "," + host.replace( "n1", "n3" ) : host;
  }

  private Krb5Credentials createKrb5Configs() {
    return cluster_node_FQDN.getText().contains( "sn" ) || cluster_node_FQDN.getText().contains( "secn" )
      ? new Krb5Credentials( kerberosUser.getText(), kerberosPassword.getText() )
      : new Krb5Credentials();
  }

  private void buttonOpenShimAction() {
    Stage stage = new Stage();
    DirectoryChooser directoryChooser = new DirectoryChooser();
    directoryChooser.setTitle( "Choose Shim directory" );
    File file = directoryChooser.showDialog( stage );
    if ( file != null ) {
      pathToSave.setText( file.getAbsolutePath() );
    }
  }

  @FXML
  void buttonOpenTestPropertiesAction( ActionEvent event ) {
    Stage stage = new Stage();
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle( "Choose test.properties file" );
    File file = fileChooser.showOpenDialog( stage );
    if ( file != null ) {
      pathToTestProperties.setText( file.getAbsolutePath() );
    }
  }

  @FXML
  void buttonOpenPemFileAction( ActionEvent event ) {
    Stage stage = new Stage();
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle( "Choose test.properties file" );
    File file = fileChooser.showOpenDialog( stage );
    if ( file != null ) {
      pathToPemFile.setText( file.getAbsolutePath() );
    }
  }
}

