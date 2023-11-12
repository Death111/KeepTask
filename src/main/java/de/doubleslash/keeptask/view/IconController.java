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

package de.doubleslash.keeptask.view;

import de.doubleslash.keeptask.common.Resources;
import de.doubleslash.keeptask.common.Resources.RESOURCE;
import de.doubleslash.keeptask.controller.Controller;
import de.doubleslash.keeptask.model.Model;
import de.doubleslash.keeptask.model.WorkItem;
import java.awt.image.BufferedImage;
import java.time.LocalDateTime;
import javafx.collections.ListChangeListener;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

public class IconController {
  private final Model model;
  private final Controller controller;
  private final Stage primaryStage;

  private final Canvas taskbarCanvas = new Canvas(64, 64);

  public IconController(Model model, Controller controller, Stage primaryStage) {
    this.model = model;
    this.controller = controller;
    this.primaryStage = primaryStage;
  }

  public void initialize() {
    model.getWorkItems().addListener((ListChangeListener<? super WorkItem>) change -> {
      updateProjectFilterButtons();
    });
    updateProjectFilterButtons();
  }

  private void updateProjectFilterButtons() {
    LocalDateTime now = LocalDateTime.now();
    int expiredTasks = (int) model.getWorkItems().stream().filter(item -> {
      LocalDateTime dueDateTime = item.getDueDateTime();
      if (dueDateTime == null) {
        return false;
      }
      return !item.isFinished() && now.isAfter(dueDateTime);
    }).count();

    updateTaskbarIcon(expiredTasks, primaryStage);
  }

  private void updateTaskbarIcon(int expiredTasks, Stage primaryStage) {
    final GraphicsContext gcIcon = taskbarCanvas.getGraphicsContext2D();
    Image applicationIcon = new Image(Resources.getResource(RESOURCE.ICON_MAIN).toString());

    gcIcon.clearRect(0, 0, taskbarCanvas.getWidth(), taskbarCanvas.getHeight());
    gcIcon.drawImage(applicationIcon, 0, 0, 64, 64);

    //gcIcon.setFill(Color.PINK);
    //gcIcon.fillRect(0,0,64,64);

    if (expiredTasks > 0) {
      gcIcon.setFill(Color.RED);
      gcIcon.fillOval(32, 32, 32, 32);

      gcIcon.setStroke(Color.WHITE);
      gcIcon.setTextAlign(TextAlignment.CENTER);
      Font aDefault = Font.getDefault();
      gcIcon.setFont(new Font(aDefault.getName(), 30));
      gcIcon.setStroke(Color.WHITE);
      gcIcon.setLineWidth(2);

      gcIcon.strokeText(Integer.toString(expiredTasks), 47, 57);
    }

    final SnapshotParameters snapshotParameters = new SnapshotParameters();
    snapshotParameters.setFill(Color.TRANSPARENT);
    final WritableImage image = taskbarCanvas.snapshot(snapshotParameters, null);

    final BufferedImage bi = SwingFXUtils.fromFXImage(image, null);
    final Image icon = SwingFXUtils.toFXImage(bi, null);

    //ImageView e = new ImageView(image);
    //mainPane.getChildren().add(e);

    primaryStage.getIcons().setAll(icon);
  }
}
