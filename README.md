# MusicPlayer

Project Title : Music Player
Java version : java version "1.8.0_211" (java 8)




Information
 -How to execute .jar file :
  cmd -> move to directory contains musicplayer.jar -> java -jar musicplayer.jar

 - The executable jar file must locate in the directory where the external library folder exist.
 
 -Application Initial setting : We added 2 sample songs with relative path.

 - if you need .txt file to save playlist, you can use musicList.txt (empty file)
   ,of course you can use another txt file.
 
 -Music saved format : Music_Title;Music_Artist;URL(file path);Duration

 -Special Tool used for building basic shape : JavaFx Scene Builder 2.0 (Oracle)
	1) Separate application into three parts. (Model, View, Controller)
	2) Model - Song class used for storing media data
	    View - Root.fxml file 
	    Controller - rootController.java handle events and updates UI
	3) Designed friendly user interface.

  Extra Functions
	1) Look and Feel - implemented by using ToggleButton that makes Background-color Dark and Bright in turn.
	
  2) File Check - whether the file choosen is mp3 type or txt type.
	
  3) Repeat - implemented same as Look and Feel.
	   A song that is selected in the song list will repeat again and again.
	
  4) Rearrange - By clicking table head including Song name, Artist, and Duration,
	   You can rearrange songs differently.

  5) Multiselect - when remove songs, it supports multi-select in song list.
	   How to : Ctrl + Mouse Click

  6) Shortcuts - implemented like Alt + KeyCode.
		Alt + B : play previous song
		Alt + P : play song selected
		Alt + U : pause 
		Alt + S : stop
		Alt + F : play next song

 -External Libraries
	- jfoenix-8.0.7.jar (support JFX components)
	- jl1.0.1.jar (support .mp3 file)
	- mp3plugin.jar
	- mp3spi.jar (get mp3 properties)
	- java-2.0.jar (include mp3 type information in AudioInfo, AudioAttributes class)
	- tika-app-1.21 (bring media meta data)
	- tritonus_share.jar (support AudioFileFormat class)
