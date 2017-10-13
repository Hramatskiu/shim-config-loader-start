package com.epam.main;

import com.epam.loader.ClusterConfigLoader;
import com.epam.loader.config.credentials.HttpCredentials;
import com.epam.loader.config.credentials.Krb5Credentials;
import com.epam.loader.config.credentials.LoadConfigs;
import com.epam.loader.config.credentials.SshCredentials;
import com.epam.loader.plan.manager.LoadConfigsManager;
import com.epam.logger.TextAreaAppender;
import com.epam.shim.configurator.ShimDependentConfigurator;
import com.epam.shim.configurator.config.ModifierConfiguration;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.apache.log4j.Logger;

import java.io.File;
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
  Label testPathLabel;
  @FXML
  Label dfsInstallDirLabel;
  @FXML
  TextArea output;
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

  private ClusterConfigLoader clusterConfigLoader;

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
    clusterConfigLoader = new ClusterConfigLoader();
    clusterConfigLoader.init();
  }

  @FXML
  void compoBoxOnShowing( Event event ) {
    if ( event.getTarget() instanceof ComboBox ) {
      if ( event.getTarget() == clusterType ) {
        List<String> clusterTypes = new ArrayList<>();
        clusterTypes.add( LoadConfigsManager.ClusterType.HDP.toString() );
        clusterTypes.add( LoadConfigsManager.ClusterType.CDH.toString() );
        clusterTypes.add( LoadConfigsManager.ClusterType.MAPR.toString() );
        clusterTypes.add( LoadConfigsManager.ClusterType.EMR.toString() );

        clusterType.getItems().setAll( clusterTypes );
        clusterType.setValue( LoadConfigsManager.ClusterType.HDP.toString() );
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
    sshLabel.setVisible( isSshOnly );
    sshUser.setVisible( isSshOnly );
    sshPassword.setVisible( isSshOnly );
  }

  private void setVisibilityForPemFileInput( boolean isPemNeeded ) {
    pemFileLabel.setVisible( isPemNeeded );
    if ( !isPemNeeded ) {
      pathToPemFile.setText( "" );
    }
    pathToPemFile.setVisible( isPemNeeded );
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
            new Krb5Credentials( kerberosUser.getText(), kerberosPassword.getText() ),
            new SshCredentials( sshUser.getText(), sshPassword.getText(), pathToPemFile.getText() ),
            cluster_node_FQDN.getText().trim(), pathToSave.getText(),
            LoadConfigsManager.ClusterType.valueOf( clusterType.getValue() ) ) );

        if ( isDownloaded ) {
          ShimDependentConfigurator.configureShimProperties( new ModifierConfiguration( pathToSave.getText(),
            dfsInstallDir.getText(), pathToTestProperties.getText() ) );
        }
        buttonStart.setDisable( false );
      } );

      thread.start();
    }
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
}

