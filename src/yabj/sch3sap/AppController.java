package yabj.sch3sap;

import java.io.File;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class AppController {
	public boolean active = false;
	private FileChooser fileChooser = new FileChooser();
	protected Media curr;
	protected MediaPlayer controls;
	private Timer scheduler = new Timer(); //TODO ScheduledExecutorService
	public int index = 0;
	@FXML private TableView<QueueElement> list;
	@FXML private FkTimeChooser dateTime;
	@FXML private TextField path;
	@FXML private Button choose;
	@FXML private Button add;
	@FXML private FkTimeChooser editDateTime;
	@FXML private TextField editPath;
	@FXML private Button editChoose;
	@FXML private Button edit, delete;
	@FXML private Button execute;
	@FXML private Button pause;
	@FXML private Button stop;
	@FXML private Button settings;
	@FXML private Button information;
	{
		fileChooser.setTitle("Оберіть аудіо-файл.");
		fileChooser.setSelectedExtensionFilter(new ExtensionFilter("Аудіо", "*.mp4", "*.aiff"));
		Platform.runLater(() -> { list.widthProperty().addListener((width, old, value) -> {
			list.getColumns().get(0).setPrefWidth((double)value / 100 * 15);
			list.getColumns().get(1).setPrefWidth((double)value / 100 * 30);
			list.getColumns().get(2).setPrefWidth((double)value / 100 * 65);
		}); });
	}
	@FXML protected void onExecute() throws ParseException{
		if(!active){
			for(QueueElement item : list.getItems()){
				scheduler.schedule(new TimerTask(){
					@Override
					public void run() {
						curr = new Media("file://" + getNormalizedPath(item.getPath()));
						controls = new MediaPlayer(curr);
						controls.setOnReady(() -> {
							controls.play();
						}); controls.setOnPlaying(() -> {
							pause.setVisible(true); stop.setVisible(true);
							pause.setText("⏸"); stop.setText("⏹");
						}); controls.setOnPaused(() -> {
							pause.setText("▶"); stop.setText("⏹");
						}); controls.setOnStopped(() -> {
							pause.setText("⏮"); stop.setText("⏭");
						}); controls.setOnEndOfMedia(() -> {
							pause.setVisible(false); stop.setVisible(false);
						});
					}
				}, Date.from( item.getTime().atDate(LocalDate.now()).atZone(ZoneId.systemDefault()).toInstant() ) );
			} execute.setTextFill(Color.RED);
			execute.setText("ЗУПИНИТИ");
			active = true;
		} else{
			scheduler.cancel();
			scheduler = new Timer();
			execute.setTextFill(Color.GREEN);
			execute.setText("УВІМКНУТИ");
			active = false;
		}
	}
	@FXML protected void onFileSelect(){
		File file = fileChooser.showOpenDialog(execute.getScene().getWindow());
		if(file != null) path.setText(file.getAbsolutePath());
	}
	@FXML protected void onAdd(){
		if(dateTime.getValue() == null || isBlank(path.getText())) return;
		if(!new File(path.getText()).exists()) return;
		if( dateTime.getValue().isBefore(LocalTime.now()) ) return;
		list.getItems().add(new QueueElement(
			dateTime.getValue(),
			new File(path.getText()).getName(),
			path.getText()
		)); list.getSelectionModel().selectedItemProperty().addListener((item, oldValue, newValue) -> {
			if(newValue == null) return;
			editDateTime.setValue(newValue.getTime());
			editPath.setText(newValue.getPath());
		}); System.out.println();
	}
	@FXML protected void onFileEdit(){
		File file = fileChooser.showOpenDialog(execute.getScene().getWindow());
		if(file != null) editPath.setText(file.getAbsolutePath());
	}
	@FXML protected void onEdit(){
		if(list.getSelectionModel().isEmpty()) return;
		if(editDateTime.getValue() == null || isBlank(editPath.getText())) return;
		if(!new File(editPath.getText()).exists()) return;
		if(editDateTime.getValue().isBefore(LocalTime.now())) return;
		list.getItems().set( list.getSelectionModel().getSelectedIndex(), new QueueElement(
			editDateTime.getValue(),
			new File(editPath.getText()).getName(),
			path.getText()
		));
	}
	@FXML protected void onDelete(){
		if(list.getSelectionModel().isEmpty()) return;
		list.getItems().remove( list.getSelectionModel().getSelectedIndex() );
	}
	@FXML protected void onPause(){
		if(controls.getStatus() == Status.PLAYING) controls.pause();
		else if(controls.getStatus() == Status.PAUSED) controls.play();
		else if(controls.getStatus() == Status.STOPPED) controls.play();
	}
	@FXML protected void onStop(){
		if(controls.getStatus() == Status.PLAYING) controls.stop();
		else if(controls.getStatus() == Status.PAUSED) controls.stop();
		else if(controls.getStatus() == Status.STOPPED){ pause.setVisible(false); stop.setVisible(false); }
	}
	@FXML protected void onSettings(){
		//TODO Settings
	}
	@FXML protected void onInfo(){
		//TODO Info
	}
	private String getNormalizedPath(String link){ //Microsoft Windows path to RFC-3986
		if(!(link.charAt(0) == '/')){
            link = link.replace("\\", "/");
            link = "/" + link;
        } return link;
	}
	private boolean isBlank(String str){ //Java 8 support
		if(str.equals("") || str == null) return true;
		for(char ch : str.toCharArray()){
			if(!Character.isWhitespace(ch)) return false;
		} return true;
	}
}
