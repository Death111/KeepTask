/*
 * Copyright 2023 - Death111
 *
 * This file is part of KeepTask.
 * KeepTask is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package de.doubleslash.keeptask;

import de.doubleslash.keeptask.common.FontProvider;
import de.doubleslash.keeptask.common.FxmlLayout;
import de.doubleslash.keeptask.common.Resources;
import de.doubleslash.keeptask.common.Resources.RESOURCE;
import de.doubleslash.keeptask.controller.Controller;
import de.doubleslash.keeptask.model.Model;
import de.doubleslash.keeptask.view.MainWindowController;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class App extends Application {

  private static final Logger LOG = LoggerFactory.getLogger(App.class);

  private ConfigurableApplicationContext springContext;

  private Model model;

  private Controller controller;

  private MainWindowController viewController;

  @Override
  public void init() throws Exception {
    LOG.info("Starting KeepTask.");
    final DefaultExceptionHandler defaultExceptionHandler = new DefaultExceptionHandler();
    defaultExceptionHandler.register();

    springContext = SpringApplication.run(App.class);
    ApplicationProperties applicationProperties = springContext.getBean(
        ApplicationProperties.class);
    LOG.info("KeepTask Version: '{}'.", applicationProperties.getBuildVersion());
    LOG.info("KeepTask Build Timestamp: '{}'.", applicationProperties.getBuildTimestamp());
    LOG.info("KeepTask Git Infos: id '{}', branch '{}', time '{}', dirty '{}'.",
        applicationProperties.getGitCommitId(), applicationProperties.getGitBranch(),
        applicationProperties.getGitCommitTime(), applicationProperties.getGitDirty());

    FxmlLayout.setContext(springContext);

    model = springContext.getBean(Model.class);
    controller = springContext.getBean(Controller.class);
  }

  @Override
  public void start(final Stage primaryStage) {
    LOG.info("Initialising the UI");
    try {
      initialiseApplication(primaryStage);
      LOG.info("UI successfully initialised.");
    } catch (final Exception e) {
      LOG.error("There was an error while initialising the UI", e);
      showExceptionAndExit(e);
    }
  }


  private void initialiseApplication(final Stage primaryStage) throws Exception {
    FontProvider.loadFonts();

    controller.init();

    initialiseAndShowUI(primaryStage);
  }

  private void initialiseAndShowUI(final Stage primaryStage) throws IOException {
    LOG.debug("Initialising main UI.");
    primaryStage.setTitle("KeepTask");
    primaryStage.initStyle(StageStyle.TRANSPARENT);
    primaryStage.getIcons().add(new Image(Resources.getResource(RESOURCE.ICON_MAIN).toString()));
    primaryStage.setAlwaysOnTop(true);
    primaryStage.setResizable(false);
    primaryStage.setOnCloseRequest(windowEvent -> LOG.info("On close request"));

    final FXMLLoader loader = FxmlLayout.createLoaderFor(RESOURCE.FXML_VIEW_LAYOUT);

    final Pane mainPane = loader.load();
    viewController = loader.getController();
    viewController.setMainStage(primaryStage);

    final Scene mainScene = new Scene(mainPane, Color.TRANSPARENT);
    primaryStage.setScene(mainScene);

    primaryStage.show();
  }

  private static void showExceptionAndExit(Exception e) {
    final Alert alert = new Alert(AlertType.ERROR);
    alert.setTitle("Error");
    alert.setHeaderText("Could not start application");
    alert.setContentText("Please send the error with your logs folder to a developer");

    final String exceptionText = getExceptionAsString(e);

    final Label label = new Label("The exception stacktrace was:");

    final TextArea textArea = new TextArea(exceptionText);
    textArea.setEditable(false);
    textArea.setWrapText(true);

    textArea.setMaxWidth(Double.MAX_VALUE);
    textArea.setMaxHeight(Double.MAX_VALUE);
    GridPane.setVgrow(textArea, Priority.ALWAYS);
    GridPane.setHgrow(textArea, Priority.ALWAYS);

    final GridPane expContent = new GridPane();
    expContent.setMaxWidth(Double.MAX_VALUE);
    expContent.add(label, 0, 0);
    expContent.add(textArea, 0, 1);

    alert.getDialogPane().setExpandableContent(expContent);

    alert.showAndWait();
    System.exit(1);
  }

  private static String getExceptionAsString(Exception e) {
    final StringWriter sw = new StringWriter();
    final PrintWriter pw = new PrintWriter(sw);
    e.printStackTrace(pw);
    final String exceptionText = sw.toString();
    return exceptionText;
  }

  @Override
  public void stop() throws Exception {
    springContext.stop();
  }

}
