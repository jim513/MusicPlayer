package application;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;

public class Main extends Application {

	//define your offsets here
    private double xOffset = 0;
    private double yOffset = 0;

	@Override
	public void start(Stage primaryStage) throws Exception{

			Parent root = FXMLLoader.load(getClass().getResource("Root.fxml"));

			//For movable code
			//grab your root here
	           root.setOnMousePressed(new EventHandler<MouseEvent>() {
	           @Override
	           public void handle(MouseEvent event) {
	               xOffset = event.getSceneX();
	               yOffset = event.getSceneY();
	           }
	       });

	       //move around here
	       root.setOnMouseDragged(new EventHandler<MouseEvent>() {
	           @Override
	           public void handle(MouseEvent event) {
	               primaryStage.setX(event.getScreenX() - xOffset);
	               primaryStage.setY(event.getScreenY() - yOffset);
	           }
	       });


       		Scene scene = new Scene(root);
			primaryStage.initStyle(StageStyle.TRANSPARENT);

			//For set root layout transparent
			scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
			root.setStyle("-fx-background-color: rgba(0, 0, 0, 0);");
			primaryStage.setScene(scene);
			primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
