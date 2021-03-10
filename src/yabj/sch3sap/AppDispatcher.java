package yabj.sch3sap;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class AppDispatcher extends Application {
	@Override
	public void start(Stage stage) throws Exception {
		Parent gui = FXMLLoader.load(getClass().getResource("gui_notation.fxml"));
		Scene scene = new Scene(gui, 400, 260);
		stage.setScene(scene);
		stage.setTitle("Диспетчер Дзвінків Школи №3");
		stage.show();
		stage.setOnCloseRequest(e -> {
			Platform.exit();
			System.exit(0);
		});
	}
	public static void main(String[] args) {
		launch(args);
	}
}
