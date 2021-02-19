package yabj.sch3sap;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class SettingsController {
	public boolean isSelectedOnStart;
	private FileChooser fileChooser = new FileChooser();
	@FXML private CheckBox clearFromQueue;
	@FXML private TextField path;
	@FXML private Button fileChoose;
	@FXML private Button importBtn;
	@FXML private Button exportBtn;
	@FXML private ChoiceBox<String> language;
	public TableView<QueueElement> list; {
		Platform.runLater(() -> {
			language.getItems().addAll("Українська");
			fileChooser.setTitle("Оберіть файл *.s3pn");
			fileChooser.setSelectedExtensionFilter(new ExtensionFilter("Нотація Плейлистів", "*.s3pn"));
			if(isSelectedOnStart) clearFromQueue.setSelected(true);
		});
	}
	@FXML protected void onClearChanged(){
		if(clearFromQueue.isSelected()) AppController.deleteOnPlay = true;
		else AppController.deleteOnPlay = false;
	}
	@FXML protected void onImport() throws IOException{
		this.importList(path.getText());
	}
	@FXML protected void onExport() throws IOException{
		this.exportList(path.getText());
	}
	@FXML protected void onChoose(){
		File file = fileChooser.showOpenDialog(path.getScene().getWindow());
		if(file != null) path.setText(file.getAbsolutePath());
	}
	public void exportList(String path) throws IOException{
		path = AppController.getNormalizedPath(path); File file;
		if(new File(path).isDirectory()) file = new File(path + ((path.charAt(path.length()-1)=='/')?"":"/") + "playlist.s3pn");
		else file = new File(path);
		FileWriter writer = new FileWriter(file);
		for(int i = 0; i < list.getItems().size(); i++){
			QueueElement el = list.getItems().get(i);
			LocalTime time = el.getTime();
			writer.append(String.format("%02d:%02d", time.getHour(), time.getMinute())+" | "+el.getPath());
			if(i+1 != list.getItems().size()) writer.append('\n'); 
		} writer.flush();
		writer.close();
	} public void importList(String path) throws FileNotFoundException{
		path = AppController.getNormalizedPath(path);
		File file = new File(path);
		Scanner reader = new Scanner(file);
		while(reader.hasNextLine()){
			String[] record = reader.nextLine().split(" | ");
			LocalTime time = LocalTime.parse(record[0], DateTimeFormatter.ofPattern("HH:mm"));
			list.getItems().add(new QueueElement(time, new File(record[2]).getName(), record[2]));
		} reader.close();
	}
}
