package com.epam.main;

import com.epam.logger.TextAreaAppender;
import com.epam.spring.ClusterConfigLoader;
import com.epam.spring.config.HttpCredentials;
import com.epam.spring.config.Krb5Credentials;
import com.epam.spring.config.LoadConfigs;
import com.epam.spring.config.SshCredentials;
import com.epam.spring.manager.LoadConfigsManager;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
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
  TextArea output;
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
      }
    }
  }

  private void buttonStartAction() {
    if ( pathToSave.getText().isEmpty() || cluster_node_FQDN.getText().isEmpty() ) {
      logger.info( "One or more required field(s) is empty! Only test.properties field is not required!" );
    } else {
      //output.setText( "" );
      buttonStart.setDisable( true );

      Thread thread = new Thread( () -> {
        clusterConfigLoader
          .loadConfigs( new LoadConfigs( new HttpCredentials( restUser.getText(), restPassword.getText() ),
            new Krb5Credentials( kerberosUser.getText(), kerberosPassword.getText() ),
            new SshCredentials( sshUser.getText(), sshPassword.getText(), pathToPemFile.getText() ),
            cluster_node_FQDN.getText().trim(), pathToSave.getText(),
            LoadConfigsManager.ClusterType.valueOf( clusterType.getValue() ) ) );
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

