package application;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.String;
import java.lang.System;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Slider;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Map;
import java.util.ResourceBundle;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import org.apache.sis.util.Exceptions;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.mp3.Mp3Parser;
import org.tritonus.share.sampled.file.TAudioFileFormat;
import org.xml.sax.ContentHandler;
import org.apache.tika.parser.Parser;
import org.xml.sax.helpers.DefaultHandler;

import com.googlecode.mp4parser.util.Path;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXToggleButton;
import com.sun.xml.internal.ws.api.ResourceLoader;
import com.uwyn.jhighlight.tools.FileUtils;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.control.TableColumn;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import javax.sound.sampled.UnsupportedAudioFileException;

public class rootController implements Initializable {

	@FXML
	private AnchorPane pane_Background, pane_Settings;

	@FXML
	private StackPane pane_Dialog;

	@FXML
	private JFXButton openFile, addSong, removeSong, openPlaylist, newPlaylist, savePlaylist, removePlaylist;

	@FXML
	private Button btn_Close, btn_Close_Setting;

	@FXML
	private JFXButton btn_Settings, btn_Previous, btn_Play, btn_Stop, btn_Next, btn_Pause;

	@FXML
	private Label music_Title, music_Artist, time;

	@FXML
	private Slider volume_Slide;

	@FXML
	private ProgressBar play_Slide;

	@FXML
	private JFXToggleButton btn_Toggle, btn_Toggle2;

	@FXML
	private ChangeListener<Duration> progressChangeListener;	//for progress bar change
	private MediaPlayer mp; 	// now playing media player
	private Duration duration;	// media duration

	//Object for settings pane fade in and out
	private static FadeTransition fadeOut = new FadeTransition();
	private static FadeTransition fadeIn = new FadeTransition();

	// variables for toggleButtons
	private int i = 0; // dark mode toggle bit
	private int j = 0; // Repeat toggle bit

	// variables for media duration display
	private String s; // for second
	private String m; // for minute

	@FXML
	private TableView<Song> table_Song;

	@FXML
	private TableColumn<Song, String> column_SongName, column_Artist, column_Duration;

	private int table_Index = 0;

	ObservableList<Song> song_List = FXCollections.observableArrayList();

	// true is new list, false is already saved list .txt
	boolean list_State = true;

	// store already saved play List .txt
	File list_File;

	// save playing song
	Song playingSong;
	boolean music_State = false;

	boolean tableClick = true;

	// For pane_Settings fade in and out
	// fade in anchorpane
	private void showTransition(AnchorPane anchorPane) {

		fadeIn.setNode(anchorPane);
		fadeIn.setDuration(Duration.millis(1000));
		fadeIn.setFromValue(0.0);
		fadeIn.setToValue(1.0);
		anchorPane.setVisible(true);
		fadeIn.play();

	}

	// fade out anchorpane
	private void hideTransition(AnchorPane anchorPane) {

		fadeOut.setNode(anchorPane);
		fadeOut.setDuration(Duration.millis(1000));
		fadeOut.setFromValue(1.0);
		fadeOut.setToValue(0.0);
		anchorPane.setVisible(false);
		fadeOut.play();

	}

	// Whenever toggled, make background color different side(bright, dark)
	private void toggleBackground(AnchorPane anchorPane, AnchorPane anchorPane2) {

		if (i == 0) { // make it dark
			// #440099 #3DF0F0
			anchorPane.setBackground(
					new Background(new BackgroundFill(Color.rgb(44, 00, 90), CornerRadii.EMPTY, Insets.EMPTY)));
			anchorPane2.setBackground(
					new Background(new BackgroundFill(Color.rgb(44, 00, 90), CornerRadii.EMPTY, Insets.EMPTY)));
			i = 1;
		} else if (i == 1) { // make it bright
			// #1DA1F2
			anchorPane.setBackground(
					new Background(new BackgroundFill(Color.rgb(29, 161, 242), CornerRadii.EMPTY, Insets.EMPTY)));
			anchorPane2.setBackground(
					new Background(new BackgroundFill(Color.rgb(61, 240, 240), CornerRadii.EMPTY, Insets.EMPTY)));
			i = 0;

		}

	}

	// make application Repeat
	private void toggleRepeat(AnchorPane anchorPane, AnchorPane anchorPane2) {

		if (j == 0) {
			j = 1;
		} else if (j == 1) {
			j = 0;
		}

	}

	//settings for media player like volume control and change progress bar
	void settings(MediaPlayer m2) {

		// volume control
		volume_Slide.valueProperty().addListener(new InvalidationListener() {

			@Override
			public void invalidated(Observable observable) {
				// TODO Auto-generated method stub

				if (volume_Slide.isValueChanging()) {
					m2.setVolume(volume_Slide.getValue() * 0.01);
				}

			}
		});

		// progress bar control
		progressChangeListener = new ChangeListener<Duration>() {

			@Override
			public void changed(ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) {
				// TODO Auto-generated method stub
				play_Slide.setProgress(1.0 * m2.getCurrentTime().toMillis() / m2.getTotalDuration().toMillis());
			}
		};

		m2.setOnStopped(() -> {
			m2.seek(m2.getStartTime());
		});

		m2.currentTimeProperty().addListener(progressChangeListener);
		m2.currentTimeProperty().addListener((Observable ov) -> {
			updateValues();
		});

	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		System.out.println("FXML load complete");

		try {

			firstInitial();

			// shortcut settings
			btn_Stop.setText("_S");		//      stop shortcut : alt + S
			btn_Play.setText("_P"); 	//      play shortcut : alt + P
			btn_Pause.setText("_U"); 	//     pause shortcut : alt + U
			btn_Previous.setText("_B"); // play back shortcut : alt + B
			btn_Next.setText("_F"); 	// play next shortcut : alt + F

			btn_Stop.setMnemonicParsing(true);
			btn_Play.setMnemonicParsing(true);
			btn_Pause.setMnemonicParsing(true);
			btn_Previous.setMnemonicParsing(true);
			btn_Next.setMnemonicParsing(true);

			//setting table columns
			column_SongName.setCellValueFactory(cellData -> cellData.getValue().songNameProperty());
			column_Artist.setCellValueFactory(cellData -> cellData.getValue().artistNameProperty());
			column_Duration.setCellValueFactory(cellData -> cellData.getValue().durationProperty());
			table_Song.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

			table_Song.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
				if (newSelection != null) {
					tableClick = true;
				}
			});

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	//include initial musics in application
	public void firstInitial() {

		File file1 = null;
		File file2 = null;
		String resource = "Shape_of_You.mp3";
		String resource2 = "Love_Yourself.mp3";
		URL res = getClass().getResource(resource);
		URL res2 = getClass().getResource(resource2);

		//it works when jar file execute
		if (res.getProtocol().equals("jar")) {
			try {
				InputStream input = getClass().getResourceAsStream(resource);
				InputStream input2 = getClass().getResourceAsStream(resource2);
				file1 = File.createTempFile("temp", ".mp3");
				file2 = File.createTempFile("temp", ".mp3");
				OutputStream out = new FileOutputStream(file1);
				OutputStream out2 = new FileOutputStream(file2);
				int read;
				byte[] bytes = new byte[1024];

				while ((read = input.read(bytes)) != -1) {
					out.write(bytes, 0, read);
				}

				while ((read = input2.read(bytes)) != -1) {
					out2.write(bytes, 0, read);
				}

				out.close();
				out2.close();
				file1.deleteOnExit();
				file2.deleteOnExit();

			} catch (IOException ex) {
				ex.printStackTrace();
			}
		} else {
			file1 = new File(res.getFile());
			file2 = new File(res2.getFile());
		}

		table_Index++;
		Song song1 = new Song("Ed Sheeran", table_Index + ". " + "Shape of You", "03:53", file1.getAbsolutePath());

		table_Index++;
		Song song2 = new Song("Justin Biebar", table_Index + ". " + "Love Yourself", "03:53", file2.getAbsolutePath());
		
		song_List.add(song1);
		song_List.add(song2);
		table_Song.setItems(song_List);

	}

	// button Actions below
	@FXML
	public void buttonPlay(ActionEvent event) {

		//if there is no selected row in Tableview
		if (table_Song.getSelectionModel().getSelectedItem() == null) {
			System.out.println("Music not selected!!!");
			return;
		}

		// if playing song, press play button again change music
		if (music_State) {
			mp.stop();
			music_State = false;
		}

		// table is clicked or repeat component is on
		if (tableClick || j == 1) {
			if (table_Song.getSelectionModel().getSelectedItem() != null) {

				playingSong = table_Song.getSelectionModel().getSelectedItem();
				Media temp = new Media("file:///" + playingSong.getUrl().replace("\\", "/"));
				duration = temp.getDuration();
				mp = new MediaPlayer(temp);
			}
		}

		mp.play();
		settings(mp);
		music_State = true;

		music_Title.setText(playingSong.getSongName());
		music_Artist.setText(playingSong.getArtistName());
		tableClick = false;

		mp.setOnEndOfMedia(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				autoPlayer();

			}
		});

	}

	@FXML
	public void buttonStop(ActionEvent event) {

		//if there is no selected row in Tableview
		if (table_Song.getSelectionModel().getSelectedItem() == null) {
			System.out.println("No music");
			return;

		}
		mp.stop();
		music_Title.setText("Music Title");
		music_Artist.setText("Music Artist");

	}

	@FXML
	public void buttonPause(ActionEvent event) {

		//if there is no selected row in Tableview
		if (table_Song.getSelectionModel().getSelectedItem() == null) {
			System.out.println("No music");
			return;
		}
		mp.pause();
		music_State = false;

	}

	@FXML
	public void buttonNext(ActionEvent event) {

		//if there is no selected row in Tableview
		if (table_Song.getSelectionModel().getSelectedItem() == null) {
			System.out.println("There isn't Next music.");
			return;
		}
		int index = 0;

		for (Song current : song_List) {
			if (current == playingSong)
				break;
			index++;
		}

		//get next Song
		index = (index + 1) % table_Index;	// use moduler action for circular play
		mp.stop();
		playingSong = song_List.get(index);
		Media temp = new Media("file:///" + playingSong.getUrl().replace("\\", "/"));
		duration = temp.getDuration();
		mp = new MediaPlayer(temp);
		settings(mp);
		mp.play();
		music_Title.setText(playingSong.getSongName());
		music_Artist.setText(playingSong.getArtistName());

		mp.setOnEndOfMedia(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				autoPlayer();

			}
		});
	}

	@FXML
	public void buttonPrevious(ActionEvent event) {

		//if there is no selected row in Tableview
		if (table_Song.getSelectionModel().getSelectedItem() == null) {
			System.out.println("There isn't previous music.");
			return;
		}

		int index = 0;

		for (Song current : song_List) {
			if (current == playingSong)
				break;
			index++;
		}

		if (index - 1 >= 0) {
			mp.stop();
			playingSong = song_List.get(index - 1);		//get previous song
			Media temp = new Media("file:///" + playingSong.getUrl().replace("\\", "/"));
			duration = temp.getDuration();
			mp = new MediaPlayer(temp);
			settings(mp);
			mp.play();
			music_Title.setText(playingSong.getSongName());
			music_Artist.setText(playingSong.getArtistName());
		}

		mp.setOnEndOfMedia(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				autoPlayer();

			}
		});

	}

	// Close and open settings button handler
	@FXML
	private void handleButtonAction(ActionEvent event) {

		if (event.getSource() == btn_Close) {
			System.out.println("Application Close!!! Good Bye~");
			System.exit(0);

		} else if (event.getSource() == btn_Settings) {
			showTransition(pane_Settings);
		}

		if (event.getSource() == btn_Close_Setting) {
			hideTransition(pane_Settings);
		}

		if (event.getSource() == btn_Toggle) {

			toggleBackground(pane_Background, pane_Settings);

		} else if (event.getSource() == btn_Toggle2) {

			toggleRepeat(pane_Background, pane_Settings);
		}

	}

	// About file and list Actions Handler
	@FXML
	public void openFileAction(ActionEvent event) throws IOException {

		System.out.println("Open file");

		FileChooser fileChooser = new FileChooser();
		File file = fileChooser.showOpenDialog(new Stage());

		if (fileTypeCheck(file, "mp3")) {
			newPlaylistAction(event);// clear list
			addToTable(file);
		}
	}

	@FXML
	public void addSongAction(ActionEvent event) {

		System.out.println("Add Song");

		FileChooser fileChooser = new FileChooser();

		// set extension filter
		FileChooser.ExtensionFilter extFiler = new FileChooser.ExtensionFilter("mp3 files (*.mp3)", "*.mp3", "*.MP3");
		fileChooser.getExtensionFilters().add(extFiler);

		File file = fileChooser.showOpenDialog(new Stage());

		if (fileTypeCheck(file, "mp3"))
			addToTable(file);

	}

	@FXML
	public void removeSongAction(ActionEvent event) {

		System.out.println("Remove Song");

		ObservableList<Song> remove_List = table_Song.getSelectionModel().getSelectedItems();

		//when there is no songs in table
		if(remove_List.size() == 0){
			return;
		}

		//when selected songs are removed, if there is no songs in table, then stop media player and set Text
		if (table_Song.getSelectionModel().getSelectedItem() == null) {

			mp.stop();
			music_Title.setText("Music Title");
			music_Artist.setText("Music Artist");

		}

		// if remove now playing song, media player stop
		if (remove_List.contains(playingSong)) {
			mp.stop();
			music_Title.setText("Music Title");
			music_Artist.setText("Music Artist");
		}

		table_Index -= remove_List.size();
		table_Song.getItems().removeAll(table_Song.getSelectionModel().getSelectedItems());
		reOrder();

	}

	@FXML
	public void openPlaylistAction(ActionEvent event) throws IOException {

		System.out.println("Open Play list");

		FileChooser fileChooser = new FileChooser();
		File file = fileChooser.showOpenDialog(new Stage());

		//check whether the file is txt
		if (fileTypeCheck(file, "txt")) {

			newPlaylistAction(event); // clear list

			list_File = file;
			list_State = false;

			//read play list
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line;

			while ((line = br.readLine()) != null) {
				table_Index++;
				String[] words = line.split(";");
				Song song = new Song(words[1], table_Index + ". " + words[0], words[3], words[2]);
				song_List.add(song);
			}
			table_Song.setItems(song_List);
			br.close();
		}
	}

	@FXML
	public void savePlaylistAction(ActionEvent event) {

		System.out.println("Save play list");

		File file;
		// song_List is new
		if (list_State) {
			FileChooser fileChooser = new FileChooser();
			file = fileChooser.showOpenDialog(new Stage());
		}
		// song_List is old
		else {
			file = list_File;
		}

		//check whether the file is txt
		if (fileTypeCheck(file, "txt")) {
			try {
				BufferedWriter writer = new BufferedWriter(new FileWriter(file, false));
				for (int row = 0; row < table_Index; row++) {
					Song temp = song_List.get(row);
					String d = String.format("%s;%s;%s;%s%n", reName(temp.getSongName()), temp.getArtistName(),
							temp.getUrl(), temp.getDuration());
					writer.write(d);
				}
				writer.flush();
				writer.close();

				list_File = file;
				list_State = false;

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@FXML
	public void newPlaylistAction(ActionEvent event) {

		//init all components
		System.out.println("New Play list");
		song_List.clear();
		table_Song.setItems(song_List);
		table_Index = 0;
		list_State = true;
		list_File = null;
		music_Title.setText("Music Title");
		music_Artist.setText("Music Artist");
		if(mp != null)
		mp.stop();

	}

	@FXML
	public void removePlaylistAction(ActionEvent event) {

		System.out.println("Remove play list");
		// list is not saved
		if (list_State)
			newPlaylistAction(event);
		// list is saved in file
		else {
			File temp = list_File;
			newPlaylistAction(event);
			if (temp.exists()) {
				if (temp.delete()) {
					System.out.println("success file remove");
				} else {
					System.out.println("fail file remove");
				}
			} else {
				System.out.println("file is not exists");
			}

		}

	}

	// to check file type
	public boolean fileTypeCheck(File f, String type) {

		if (f != null) {
			String name = f.getName();
			String Upper = type.toUpperCase();

			if (name.endsWith(type) || name.endsWith(Upper))
				return true;
			else {
				System.out.printf("This is not %s\n", type);
				return false;
			}
		} else {
			System.out.println("cancel");
			return false;
		}
	}

	// add file to table
	public void addToTable(File f) {

		String temp = f.getName();
		String name = temp.substring(0, temp.length() - 4);
		name = name.replace("_", " ");
		// String path = f.getAbsolutePath().replace("\\", "/");
		System.out.println(name);
		// System.out.println(f.getAbsolutePath());

		try {	// bring information from song
			InputStream input = new FileInputStream(new File(f.getAbsolutePath()));
			ContentHandler handler = new DefaultHandler();
			Metadata metadata = new Metadata();
			Parser parser = new Mp3Parser();
			ParseContext parseCtx = new ParseContext();
			parser.parse(input, handler, metadata, parseCtx);
			input.close();

			String artist = metadata.get("xmpDM:artist");
			getTotalDuration(f);

			table_Index++;
			Song song = new Song(artist, table_Index + ". " + name, m + ":" + s, f.getAbsolutePath());

			song_List.add(song);
			table_Song.setItems(song_List);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// for time formatting
	private static String formatTime(Duration elapsed, Duration duration) {

		int intElapsed = (int) Math.floor(elapsed.toSeconds());
		int elapsedHours = intElapsed / (60 * 60);
		if (elapsedHours > 0) {
			intElapsed -= elapsedHours * 60 * 60;
		}
		int elapsedMinutes = intElapsed / 60;
		int elapsedSeconds = intElapsed - elapsedHours * 60 * 60 - elapsedMinutes * 60;

		if (duration.greaterThan(Duration.ZERO)) {
			int intDuration = (int) Math.floor(duration.toSeconds());
			int durationHours = intDuration / (60 * 60);
			if (durationHours > 0) {
				intDuration -= durationHours * 60 * 60;
			}
			int durationMinutes = intDuration / 60;
			int durationSeconds = intDuration - durationHours * 60 * 60 - durationMinutes * 60;
			if (durationHours > 0) {
				return String.format("%d:%02d:%02d/%d:%02d:%02d", elapsedHours, elapsedMinutes, elapsedSeconds,
						durationHours, durationMinutes, durationSeconds);
			} else {
				return String.format("%02d:%02d/%02d:%02d", elapsedMinutes, elapsedSeconds, durationMinutes,
						durationSeconds);
			}
		} else {
			if (elapsedHours > 0) {
				return String.format("%d:%02d:%02d", elapsedHours, elapsedMinutes, elapsedSeconds);
			} else {
				return String.format("%02d:%02d", elapsedMinutes, elapsedSeconds);
			}
		}
	}

	// update ui according to changes
	protected void updateValues() {

		if (time != null && play_Slide != null && duration != null) {
			Platform.runLater(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub

					Duration currentTime = mp.getCurrentTime();
					time.setText(formatTime(currentTime, duration));
					play_Slide.setDisable(duration.isUnknown());

				}

			});
		}
	}

	// take off number to Song Name ex) 1. Hello -> Hello
	public String reName(String s) {
		int n;
		for (n = 0; n < s.length(); n++) {
			char c = s.charAt(n);
			if (c == '.')
				break;
		}
		s = s.substring(n + 2, s.length());
		return s;
	}

	// when remove playlist reordering songs
	void reOrder() {
		for (int k = 0; k < table_Index; k++) {
			String s = reName(song_List.get(k).getSongName());
			song_List.get(k).setSongName((k + 1) + ". " + s);
		}
		table_Song.setItems(song_List);
	}

	public void getTotalDuration(File f) {

		try {
			// get total play time about songs
			AudioFileFormat format = AudioSystem.getAudioFileFormat(f);
			AudioFormat baseFormat = format.getFormat();

			if (format instanceof TAudioFileFormat) {
				
				Map properties = ((TAudioFileFormat)format).properties();
				Long du = (Long) properties.get("duration");

				int mili = (int) (du / 1000);
				int second = (mili / 1000) % 60;
				int minute = (mili / 1000) / 60;

				String s1 = (second >= 10) ? String.valueOf(second) : "0" + String.valueOf(second);
				String m1 = (minute >= 10) ? String.valueOf(minute) : "0" + String.valueOf(minute);

				setMS(m1, s1);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// set minutes and seconds
	public void setMS(String m, String s) {
		this.m = m;
		this.s = s;
	}

	// for play songs in turn
	public void autoPlayer() {
		int index = 0;
		for (Song current : song_List) {
			if (current == playingSong)
				break;
			index++;
		}

		index = (index + 1) % table_Index;
		mp.stop();
		playingSong = song_List.get(index);
		Media temp = new Media("file:///" + playingSong.getUrl().replace("\\", "/"));
		duration = temp.getDuration();
		mp = new MediaPlayer(temp);
		settings(mp);
		mp.play();
		music_Title.setText(playingSong.getSongName());
		music_Artist.setText(playingSong.getArtistName());

		ActionEvent event = new ActionEvent();
		buttonPlay(event);

	}

}
